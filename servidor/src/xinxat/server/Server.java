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
	
	private Stack<Message> pila = new Stack<Message>();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

	    JID fromJid = new JID(req.getParameter("from"));
	    JID recipientJids = new JID(req.getParameter("to"));
	    String body = req.getParameter("msg");
    	Message message = new MessageBuilder().withRecipientJids(recipientJids).withFromJid(fromJid).withBody(body).build();
    	
    	
    	pila.push(message);
    	
    	
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
    	
    	if(pila.isEmpty()) resp.getWriter().println("EMPTY");
	    else {
	    	while (!pila.isEmpty()){
	    		Message missatge = pila.pop();
	    		
	    		resp.getWriter().println("<message ");
	    		resp.getWriter().println("from=" + missatge.getFromJid());
	    		
	    		for (JID forJID : missatge.getRecipientJids()) {
	    			String recipientJid = forJID.toString();
	    			resp.getWriter().println("to=" + recipientJid); 
	    		}
	    		resp.getWriter().println("> <body>" + missatge.getBody() + "</body>");
	    		resp.getWriter().println("</message>");
	    		
	    	}
	    }
    }
	
	
	
	
	
}