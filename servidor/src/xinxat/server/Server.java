package xinxat.server;

/**
 * This class represents the main server that listens to every single
 * request and deals with it depending on its purpose.
 * 
 * @author Fran Hermoso <franhp@franstelecom.com>
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import xinxat.server.Xmpp;


@SuppressWarnings("serial")
public class Server extends HttpServlet {
	/**
	 * This Map holds a Stack of messages for every user in the system
	 */
	private Map<String, Stack<String>> messageStack = new HashMap<String, Stack<String>>();
	
	/**
	 * This Map holds a List of users for every channel
	 */
	private static Map<String, ArrayList<String>> rooms = new HashMap<String, ArrayList<String>>();
	
	/**
	 * This Map holds a List of bannned users for every channel
	 */
	private static Map<String, ArrayList<String>> bans = new HashMap<String,ArrayList<String>>();
	
	/**
	 * Datastore connection
	 */
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	/**
	 * This Servlet receives all the messages and puts them on the stack.
	 * 
	 * In order to send a message the user must send a POST to the following URL
	 * 		http://projecte-xinxat.appspot.com/messages
	 * 		Add the following fields to the request
	 * 			msg: 	contains the xmpp message 
	 * 					type= can be defined by: chat, groupchat, system
	 * 			token:	contains the token given by the frontend 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		//Read and check if the XMPP xml is correct
		Xmpp msg = new Xmpp(req.getParameter("msg"));
		
		
		try {
			Entity from = getUserByToken(req.getParameter("token"));
			if(from != null && msg.getSender().equalsIgnoreCase(from.getProperty("username").toString())){
				//If it is a private chat
				if(msg.getType().equals("chat")){
					String recipient = msg.getRecipient();
					//Get the Stack if it exists
					Stack<String> pila_usuari = messageStack.get(recipient);
					//If it doesn't exist, check if the user should have an stack
			    	if(pila_usuari == null) {
			    		Query q = new Query("user");
			    		q.addFilter("username", Query.FilterOperator.EQUAL, recipient);
						PreparedQuery pq = datastore.prepare(q);
						for (Entity result : pq.asIterable()) {
							String username = result.getProperty("username").toString();
							if(username != null)
								pila_usuari = new Stack<String>();
						}	
			    	}
			    	//Now that we have a Stack, fill it!
			    	if(pila_usuari != null){
				    	pila_usuari.push(msg.getAllMessage());
				    	messageStack.put(recipient, pila_usuari);
				    	resp.getWriter().println("OK");

					}
				} 
				
				//If it is a groupchat
				else if (msg.getType().equals("groupchat")){
					String destChannel = msg.getRecipient();
					ArrayList<String> peopleInRoom = rooms.get(destChannel);
					for (String user : peopleInRoom){
						Stack<String> pila_usuari = messageStack.get(user);
						//If it doesn't exist, check if the user should have an stack
				    	if(pila_usuari == null) {
				    		Query q = new Query("user");
				    		q.addFilter("username", Query.FilterOperator.EQUAL, user);
							PreparedQuery pq = datastore.prepare(q);
							for (Entity result : pq.asIterable()) {
								String username = result.getProperty("username").toString();
								if(username != null)
									pila_usuari = new Stack<String>();
							}	
				    	}
				    	//Now that we have a Stack, fill it!
				    	if(pila_usuari != null){
					    	pila_usuari.push(msg.getAllMessage());
					    	messageStack.put(user, pila_usuari);
					    	resp.getWriter().println("OK");
				    	}
				    	else resp.getWriter().println("No user found");
					}
				}
				//If it is a system message
				else if(msg.getType().equals("system")){
					String command = msg.getBody();
					String[] params = command.split(" ");
					
					if(params[0].equals("/join")){
						ArrayList<String> banned = null;
						try{
							banned = bans.get(params[1]);
						} catch (NullPointerException e){
							bans.put(params[1], new ArrayList<String>());
							banned = bans.get(params[1]);
						} finally {
							if(!banned.contains(params[1])){
								addUserToRoom(msg.getSender(), params[1]);
							}
							else sendMessage(msg.getSender(), "You are banned from this room");
						}
					}
					else if(params[0].equals("/leave"))
						deleteUserFromRoom(msg.getSender(), msg.getRecipient());
					else if(params[0].equals("/list"))
						resp.getWriter().println(getUsersFromRoom(params[1]));	
					else if(params[0].equals("/invite"))
						addUserToRoom(params[1], msg.getRecipient());
					else if(params[0].equals("/ban")){
						sendMessage(msg.getRecipient(), params[1] + "was banned because: " + params[2]);
						deleteUserFromRoom(params[1], msg.getRecipient());
						ban(params[1],msg.getRecipient());
					}
					else if(params[0].equals("/unban")){
						unban(params[1],msg.getRecipient());
					}
					else if(params[0].equals("/kick")){
						sendMessage(msg.getRecipient(), params[1] + " was kicked because: " + params[2]);
						deleteUserFromRoom(params[1], msg.getRecipient());
					}
				}
			}
			else resp.getWriter().println("Wrong user/password");
		}
		catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

    }
	

	/**
	 * This servlet outputs the pending messages still on the stack and
	 * also sets the presence
	 * 
	 * In order to receive the messages the user must send a GET request to the following URL:
	 * 			http://projecte-xinxat.appspot.com/messages
	 * 			status: contains the status message
	 * 			show:	contains one of the following: chat, dnd, away
	 * 			to:		contains the requestor's username
	 * 			token:	contains the token given by the frontend 
	 */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	Entity from = getUserByToken(req.getParameter("token"));
    	String recipient = req.getParameter("to");
		if(from != null && recipient.equals(from.getProperty("username"))){
			//Get the stack of pending messages from the map
	    	Stack<String> missatges = messageStack.get(recipient);
	    	if(missatges == null) missatges = new Stack<String>();
	    	
	    	//Set the presence of the user
			String show = req.getParameter("show");
			String status = req.getParameter("status");
			
			Query q = new Query("user");
			q.addFilter("username", Query.FilterOperator.EQUAL, recipient);
			PreparedQuery pq = datastore.prepare(q);
			
			//Write the presence on the datastore
			Entity user = new Entity("user");
			for (Entity result : pq.asIterable()) {
				user.setProperty("username", result.getProperty("username"));
				user.setProperty("password", result.getProperty("password"));
				user.setProperty("show", show);
				user.setProperty("status", status);
				long lastonline = System.currentTimeMillis() / 1000L;
				user.setProperty("lastonline", lastonline);
				datastore.delete(result.getKey());
			}
			datastore.put(user);
			
			//Return presence if no messages are unread
	    	if(missatges.isEmpty()){
	    			resp.setContentType("text/plain");
	    			resp.getWriter().println("<presence xml:lang=\"en\">" +
	    	    			"\n\t<show>" + show + "</show>" +
	    	    			"\n\t<status>" + status + "</status>" +
	    	    			"\n</presence>");
	    	}
	    	//Return the messages
		    else {
		    	//Reverse the Stack
		    	Stack <String> pila_reversed = new Stack<String>();
		    	while(!missatges.isEmpty()){
		    		String message = missatges.pop();
		    		pila_reversed.push(message);
		    	}
		    	
		    	//Output the Stack
		    	resp.setContentType("text/plain");
		    	resp.getWriter().println("<?xml version=\"1.0\"?>\n" +
		    								"<messages>\n");
		    	while (!pila_reversed.isEmpty()){
		    		resp.getWriter().println( pila_reversed.pop());
		    	}
		    	resp.getWriter().println("</messages>\n");
		    }

		}
		
    }
    
    /**
     * Creates a room
     * 
     * @param name
     */
    public static void createRoom(String name){
    	try{
    		ArrayList<String> room = rooms.get(name);
    		if(room!=null)
    			System.out.println("Room exists");
    	} catch (NullPointerException e){
    		rooms.put(name, new ArrayList<String>());
    	}
    }
    
    /**
     * Returns a list of the users in a room
     * 
     * @param room
     * @return ArrayList of users
     */
	public static ArrayList<String> getUsersFromRoom(String room){
		return rooms.get(room);
	}
	
	
	/**
	 * Adds a user to a room
	 * 
	 * @param user
	 * @param room
	 */
	public static void addUserToRoom(String user, String room){
		ArrayList<String> users = new ArrayList<String>();
		try{
			if(!rooms.get(room).equals(null)){
				for(String otherUser: rooms.get(room)){
					if(!otherUser.equalsIgnoreCase(user))
						users.add(otherUser);
				}
			}
		} catch (NullPointerException e){
			System.out.println("Creating channel");
		}
		users.add(user);
		rooms.put(room, users);
	}
	
	/**
	 * Deletes a user from a room
	 * 
	 * @param user
	 * @param room 
	 */
	public static void deleteUserFromRoom(String user, String room){
		ArrayList<String> users = new ArrayList<String>();
		for(String otherUser: rooms.get(room))
			if(!otherUser.equals(user))
				users.add(otherUser);
		rooms.put(room, users);
	}
	
	/**
	 * Deletes all the rooms
	 */
	public static void reset() {
		rooms.clear();
	}

	/**
	 * Checks if the user exists on the datastore
	 * 
	 * @param token password created by the frontend
	 * @return User's entity
	 */
	public Entity getUserByToken(String token){
		//Check if the token matches the user in the from field of the XMPP
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, token);
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities(FetchOptions.Builder.withDefaults()) >= 1) {
			for(Entity user : pquery.asIterable()){
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Bans a user from a room
	 * 
	 * @param who the user to be banned
	 * @param where the room where it will be banned
	 */
	public static void ban(String who, String where) {
		ArrayList<String> banned = null;
		try{
			banned = bans.get(where);
			if(!banned.contains(who))
				banned.add(who);
		} catch (NullPointerException e){
			bans.put(where, new ArrayList<String>());
			banned = bans.get(where);
			banned.add(who);
		} finally {
			bans.put(where, banned);
		}
		
	}
	

	/**
	 * Unbans a user from a room
	 * 
	 * @param who the user to be unbanned
	 * @param where the room where it will be unbanned
	 */
	public static void unban(String who, String where) {
		ArrayList<String> banned = bans.get(where);
		banned.remove(who);
		bans.put(where, banned);
		
	}




	/**
	 * Sends a message to a user as system
	 * 
	 * @param to the recipient user
	 * @param message the actual message
	 */
	private void sendMessage(String to, String message) {
		String msg = "<message to=\"" + to + "\" type=\"system\">" +
							"<body>" + message + "</body>" +
						"</message>";
		
		ArrayList<String> peopleInRoom = rooms.get(to);
		for (String user : peopleInRoom){
			Stack<String> pila_usuari = messageStack.get(user);
	    	//Now that we have a Stack, fill it!
	    	if(pila_usuari != null){
		    	pila_usuari.push(msg);
		    	messageStack.put(user, pila_usuari);
	    	}
		}
		
	}




}