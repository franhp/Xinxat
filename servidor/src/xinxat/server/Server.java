package xinxat.server;

/**
 * This class represents the main server that listens to every single
 * request and deals with it depending on its destination.
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
	public Map<String, ArrayList<String>> rooms = new HashMap<String, ArrayList<String>>();
	
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
			//If it is a private chat
			if(msg.getType().equals("chat")){
				Entity from = getUserByToken(req.getParameter("token"));
				if(from != null && from.getProperty("username").toString().equals(msg.getSender())){
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
					else
						resp.getWriter().println("Wrong username/password");
				} 
			}
			//If it is a groupchat
			else if (msg.getType().equals("groupchat")){
				Entity from = getUserByToken(req.getParameter("token"));
				if(from != null && msg.getSender().equals(from.getProperty("username"))){
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
				else resp.getWriter().println("Wrong user/password");
			}
			
			else if(msg.getType().equals("system")){
				Entity from = getUserByToken(req.getParameter("token"));
				if(from != null && msg.getSender().equals(from.getProperty("username"))){
					String command = msg.getBody();
					String[] params = command.split(" ");
					
					if(params[0].equals("/join"))
						addUserToRoom(msg.getSender(), params[1]);
					else if(params[0].equals("/leave"))
						deleteUserFromRoom(msg.getSender(), msg.getRecipient());
					else if(params[0].equals("/list"))
						resp.getWriter().println(getUsersFromRoom(params[1]));	
					else if(params[0].equals("/invite"))
						addUserToRoom(params[1], msg.getRecipient());
					else if(params[0].equals("/ban"))
						deleteUserFromRoom(params[1], msg.getRecipient());
					else if(params[0].equals("/kick"))
						deleteUserFromRoom(params[1], msg.getRecipient());
				}
				else resp.getWriter().println("Wrong user/password");
			}
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
    
	public ArrayList<String> getUsersFromRoom(String room){
		return rooms.get(room);
	}

	public  void addUserToRoom(String user, String room){
		ArrayList<String> users = new ArrayList<String>();
		try{
			if(!rooms.get(room).equals(null)){
				for(String otherUser: rooms.get(room))
					users.add(otherUser);
			}
		} catch (NullPointerException e){
			System.out.println("no channel yet, creating it");
		}
		users.add(user);
		rooms.put(room, users);
	}
	
	public  void deleteUserFromRoom(String user, String room){
		ArrayList<String> users = new ArrayList<String>();
		for(String otherUser: rooms.get(room))
			if(!otherUser.equals(user))
				users.add(otherUser);
		rooms.put(room, users);
	}
	
	public  void reset() {
		rooms.clear();
	}

	/**
	 * Checks if the user exists on the datastore
	 * 
	 * @param token
	 * @return user
	 */
	public Entity getUserByToken(String token){
		//Check if the token matches the user in the from field of the XMPP
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, token);
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities(FetchOptions.Builder.withDefaults()) < 1) {
			for(Entity user : pquery.asIterable()){
				return user;
			}
		}
		return null;


	}


}