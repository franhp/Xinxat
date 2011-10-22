package xinxat.server;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class Xmpp {
	
	public String message;
	
	public Xmpp (String message){
		this.message = message;
	}
	
	public String getRecipient() throws SAXException, IOException, ParserConfigurationException{
		return parseFor("to");
	}
	
	public String getOrigin() throws SAXException, IOException, ParserConfigurationException{
		return parseFor("from");
	}
	
	public String getAllMessage(){
		return this.message;
	}
	
	
	private String parseFor(String field) throws SAXException, IOException, ParserConfigurationException{
			DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(this.message));

	        org.w3c.dom.Document doc = db.parse(is);
	        org.w3c.dom.NodeList nodes = doc.getElementsByTagName("message");

	        String name = null;
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element element = (Element) nodes.item(i);
	           name = element.getAttribute(field);
	        }
			return name;
	}
}