package xinxat.server;

/**
 * This class represents the main server that listens to every single
 * request and deals with it depending on its destination.
 * 
 * @author Fran Hermoso <franhp@franstelecom.com>
 */
import java.io.IOException;
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
	private Map<String, Stack<String>> stack = new HashMap<String, Stack<String>>();
	
	/**
	 * This Servlet receives all the messages and puts them on the stack.
	 * 
	 * In order to send a message the user must send a POST to the following URL
	 * 		http://projecte-xinxat.appspot.com/messages
	 * 		Add the following fields to the request
	 * 			msg: 	contains the xmpp message
	 * 			token:	contains the token given by the frontend 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		//Read and check if the XMPP xml is correct
		Xmpp msg = new Xmpp(req.getParameter("msg"));
		
		//Check if the token matches the user in the from field of the XMPP
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, req.getParameter("token"));
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities(FetchOptions.Builder.withDefaults()) < 1) resp.getWriter().println("Wrong password");
		for (Entity user : pquery.asIterable()) {
			try {
				if(user.getProperty("username").toString().equals(msg.getSender())){
					String recipient = null;
					try {
						recipient = msg.getRecipient();
						//Get the Stack if it exists
						Stack<String> pila_usuari = stack.get(recipient);
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
					    	stack.put(recipient, pila_usuari);
					    	resp.getWriter().println("OK");
				    	}
				    	else resp.getWriter().println("No user found");
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}
				}
				else
					resp.getWriter().println("Wrong username");
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
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
    	//Check if the user exists
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, req.getParameter("token"));
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities(FetchOptions.Builder.withDefaults()) < 1)
				resp.getWriter().println("Wrong password");
		
		String recipient = req.getParameter("to");
		
		for (Entity requestingUser : pquery.asIterable()) {
			if(recipient.equals(requestingUser.getProperty("username"))){
					//Get the stack of pending messages from the map
			    	Stack<String> missatges = stack.get(recipient);
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
				    	while (!pila_reversed.isEmpty()){
				    		resp.setContentType("text/plain");
				    		resp.getWriter().println( pila_reversed.pop());
				    		
				    	}
				    }
			}

		}
		
    }
}