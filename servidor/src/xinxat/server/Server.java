package xinxat.server;

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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import xinxat.server.Xmpp;


@SuppressWarnings("serial")
public class Server extends HttpServlet {
	
	private Map<String, Stack<String>> pila = new HashMap<String, Stack<String>>();
	
	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		//Read and check if the XMPP xml is correct
		Xmpp msg = new Xmpp(req.getParameter("msg"));
		
		//Check if the token matches the user in the from field of the XMPP
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, req.getParameter("token"));
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities() < 1) resp.getWriter().println("Wrong password");
		for (Entity user : pquery.asIterable()) {
			try {
				if(user.getProperty("username").toString().equals(msg.getOrigin())){
					String recipient = null;
					try {
						recipient = msg.getRecipient();
						//Get the Stack if it exists
						Stack<String> pila_usuari = pila.get(recipient);
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
					    	pila.put(recipient, pila_usuari);
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
    
    @SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("user");
		query.addFilter("password", Query.FilterOperator.EQUAL, req.getParameter("token"));
		PreparedQuery pquery = datastore.prepare(query);
		if(pquery.countEntities() < 1)
				resp.getWriter().println("Wrong password");
		
		
		String recipient = req.getParameter("to");
		
		for (Entity requestingUser : pquery.asIterable()) {
			if(recipient.equals(requestingUser.getProperty("username"))){
			    	Stack<String> missatges = pila.get(recipient);
			    	if(missatges == null) missatges = new Stack<String>();
			    	
					String show = req.getParameter("show");
					String status = req.getParameter("status");
					
					//get
					Query q = new Query("user");
					q.addFilter("username", Query.FilterOperator.EQUAL, recipient);
					PreparedQuery pq = datastore.prepare(q);
			
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
					//put
					datastore.put(user);
					
			    	if(missatges.isEmpty()){
			    			resp.setContentType("text/plain");
			    			resp.getWriter().println("<presence xml:lang=\"en\">" +
			    	    			"\n\t<show>" + show + "</show>" +
			    	    			"\n\t<status>" + status + "</status>" +
			    	    			"\n</presence>");
			    	}
				    else {
				    	//Reverse la pila
				    	Stack <String> pila_reversed = new Stack<String>();
				    	while(!missatges.isEmpty()){
				    		String message = missatges.pop();
				    		pila_reversed.push(message);
				    	}
				    	
				    	//Escupe la pila
				    	while (!pila_reversed.isEmpty()){
				    		resp.setContentType("text/plain");
				    		resp.getWriter().println( pila_reversed.pop());
				    		
				    	}
				    }
			}

		}
		
    }
    
    
	
	
	
	
	
}