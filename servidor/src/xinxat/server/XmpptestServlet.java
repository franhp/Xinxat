package xinxat.server;

import java.io.IOException;

import javax.servlet.http.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class XmpptestServlet
    extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest req,
      HttpServletResponse resp) throws IOException {
    
    // Parse incoming message
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    Message msg = xmpp.parseMessage(req);
    JID jid = msg.getFromJid();
    String body = msg.getBody();
    
    // Get a response from Eliza
    String response = "echo: " + body;
    
    // Send out response
    msg = new MessageBuilder()
        .withRecipientJids(jid)
        .withBody(response)
        .build();
    xmpp.sendMessage(msg);

  }
}