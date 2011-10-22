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

import xinxat.server.Xmpp;


@SuppressWarnings("serial")
public class Server extends HttpServlet {
	
	private Map<String, Stack<String>> pila = new HashMap<String, Stack<String>>();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
  
		
		//A la pila!
		Xmpp msg = new Xmpp(req.getParameter("msg"));
		
		String to = null;
		try {
			to = msg.getRecipient();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
    	finally {
	    	Stack<String> pila_usuari = pila.get(to);
	    	if(pila_usuari == null) pila_usuari = new Stack<String>();
	    	
	    	pila_usuari.push(msg.getAllMessage());
	    	
	    	pila.put(to, pila_usuari);
	    	resp.getWriter().println("OK");
    	}
    	/* Stanza?
    	 <presence 
    	 	from="romeo@gmail.com" 
    	 	to="juliet@gmail.com" 
    	 	xml:lang="en"> 
    	 	<show>dnd</show> 
    	 	<status>Good night, good night! parting is such sweet sorrow, 
    	 	that I shall say good night till it be morrow.</status> 
    	 </presence>
    	 */
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	
    	resp.setContentType("text/plain");
    	
    	
    	String to = req.getParameter("to");
    	Stack<String> missatges = pila.get(to);
    	if(missatges == null) missatges = new Stack<String>();
    	
    	if(missatges.isEmpty()) resp.getWriter().println("<presence xml:lang=\"en\">\n " +
    			"\t<show>chat</show>\n" +
    			"\t<status>" + req.getParameter("status") + "</status>\n" +
    			"</presence>");
    			//Falta hacer lo del status y tal
	    else {
	    	//Reverse la pila
	    	Stack <String> pila_reversed = new Stack<String>();
	    	while(!missatges.isEmpty()){
	    		String message = missatges.pop();
	    		pila_reversed.push(message);
	    	}
	    	
	    	//Escupe la pila
	    	while (!pila_reversed.isEmpty()){
	    		resp.getWriter().println( pila_reversed.pop());
	    		
	    		/*resp.getWriter().println("<?xml version=\"1.0\"?>\n" +
	    				"<stream:stream from=\"xinxat\"\n" +
	    				" \t id=\"someid\" xmlns=\"jabber:client\" \n" +
	    				" \t xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\">\n" +
	    				"\t\t<message ");
	    		
	    		//From
	    		resp.getWriter().println("\t\t\t from=" + missatge.getFromJid());
	    		//To
	    		for (JID forJID : missatge.getRecipientJids()) {
	    			String recipientJid = forJID.toString();
	    			resp.getWriter().println("\t\t\t to=" + recipientJid + ">\n"); 
	    		}
	    		//Body
	    		resp.getWriter().println("\t\t\t<body>\n\t\t\t\t" + missatge.getBody() + "\n\t\t\t</body>");
	    		
	    		resp.getWriter().println("\t\t</message>\n</stream:stream>");*/
	    		
	    	}
	    }
    }
    
    
	
	
	
	
	
}