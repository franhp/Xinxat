package xinxat.server;

/**
 * This class is able to extract specific information 
 * from XMPP messages
 * 
 * @author Fran Hermoso <franhp@franstelecom.com>
 */

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class Xmpp {
	/**
	 * Contains the whole message
	 */
	public String message;
	
	/**
	 * Constructor
	 * @param message
	 */
	public Xmpp (String message){
		this.message = message;
	}
	
	/**
	 * Returns the recipients of the message
	 * 
	 * @return recipients
	 */
	public String getRecipient() throws SAXException, IOException, ParserConfigurationException{
		return parseFor("to");
	}
	
	/**
	 * Returns the sender user of the message
	 * 
	 * @return sender
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public String getSender() throws SAXException, IOException, ParserConfigurationException{
		return parseFor("from");
	}
	
	/**
	 * Returns the type of the message, groupchat or chat
	 * 
	 * @return type
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public String getType() throws SAXException, IOException, ParserConfigurationException {
		return parseFor("type");
	}
	
	/**
	 * Returns the whole message
	 * 
	 * @return message
	 */
	public String getAllMessage(){
		return this.message;
	}
	
	/**
	 * Returns the body of the message
	 * 
	 * @return body
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public String getBody()  throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(this.message));

        org.w3c.dom.Document doc = db.parse(is);
        org.w3c.dom.NodeList nodes = doc.getElementsByTagName("message");
		return nodes.item(0).getFirstChild().getTextContent();
	}
	
	/**
	 * Searches for an attribute in the message tag of a XMPP
	 * 
	 * @param attribute
	 * @return attribute's value
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private String parseFor(String attribute) throws SAXException, IOException, ParserConfigurationException{
			DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(this.message));

	        org.w3c.dom.Document doc = db.parse(is);
	        org.w3c.dom.NodeList nodes = doc.getElementsByTagName("message");

	        String name = null;
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element element = (Element) nodes.item(i);
	           name = element.getAttribute(attribute);
	        }
			return name;
	}
}