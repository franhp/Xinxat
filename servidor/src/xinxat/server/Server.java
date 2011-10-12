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
		
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
	    Message msg = xmpp.parseMessage(req.getParameter("msg"));
	    
	    JID fromJid = msg.getFromJid();
	    JID toJid = msg.getRecipientJids();
	    String body = msg.getBody();
    	
    	Message missatge = new MessageBuilder().withFromJid(fromJid).withRecipient(toJid).withBody(body).build();
    	
    	pila.push(missatge.toString());
    	resp.getWriter().println("OK");
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	
    	resp.setContentType("text/plain");
    	
    	if(pila.isEmpty()) resp.getWriter().println("EMPTY");
	    else {
	    	while (!pila.isEmpty()){
	    		String missatge = pila.pop();
	    		resp.getWriter().println(missatge);
	    	}
	    }
    }
	
	
	
	
	
}