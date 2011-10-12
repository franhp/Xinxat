package xinxat.server;

import java.io.IOException;

import java.util.Stack;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;


public class Server extends HttpServlet {
	
	private Stack<String> pila = new Stack<String>();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
  
		
		   //A la pila!
    	String msg = req.getParameter("msg");
    	pila.push(msg);
    	
    	
    	resp.getWriter().println("OK");
    	
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
    	
    	if(pila.isEmpty()) resp.getWriter().println("<presence xml:lang=\"en\">\n " +
    			"\t<show>chat</show>\n" +
    			"\t<status>" + req.getParameter("status") + "</status>\n" +
    			"</presence>");
    			//Falta hacer lo del status y tal
	    else {
	    	while (!pila.isEmpty()){
	    		String missatge = pila.pop();
	    		resp.getWriter().println(missatge);
	    		
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