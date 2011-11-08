package xinxat.main;
/*
 * XMLParser.java
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author Procastinadors
 */
public class XMLParser {
	//Empezamos creando una variable Documento DOM donde metermos el XML
    private Document dom;
    //Creamos otra variable que sea una lista de mensajes (objeto message) para guardar los mensajes que nos devuelva la pila
    private ArrayList<Message> messages = new ArrayList<Message>();
    private ArrayList<Presence> presences = new ArrayList<Presence>();  
    private ArrayList<Room> rooms = new ArrayList<Room>();  
    
    private String xmlString;
    private String mode;
    
    public XMLParser(String xmlString, String mode){
    	this.xmlString = xmlString;
    	this.mode = mode;
    }
    
    /**
    * La funció parseXmlString rep una String en format xml
    * i crea un objecte DOM. Després amb un Iterador busca tots els
    * fills del node pare <messages> i busca els fills <message>
    * els recorre creant objectes tipus message i els fica en la ArrayList
    * Messages que hem definit a dalt
    */
    
	public ArrayList<?> parseXmlString(){

		//Creem una instància de DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			//Con la factoria creamos una instancia de DocumenBuilder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Creem un InputStream amb el contingut del text xml perquè la funció
			// parse () no accepta strings a seques
			InputStream is = new ByteArrayInputStream(this.xmlString.getBytes("UTF-8"));
			//Parsem el inputStream 
			dom = db.parse(is);
				/** ESTA PARTE NO ES COMUN **/			
			if (this.mode.equals("messages")){
				
				//Localitzem el node pare <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Busquem els fills <message> i per a cada un ...
				NodeList nl = nodoPadre.getElementsByTagName("message");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creem un objecte element
						Element el = (Element) nl.item(i);
						//Li passem l'element amb el message a la funció getMessage
						//perquè ens generi un nou objecte message
						Message m = getMessage(el);
						//Finalment ho afegim a la llista
						messages.add(m);
					}
				}
				
				return messages;
				
			} else if (this.mode.equals("roster")){
				
				//Localitzem el node pare <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Busquem els fills <message> i per a cada un ...
				NodeList nl = nodoPadre.getElementsByTagName("presence");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creem un objecte element
						Element el = (Element) nl.item(i);
						//Li passem l'element amb el message a la funció getMessage
						//perquè ens generi un nou objecte message
						Presence p = getPresence(el);
						//Finalment ho afegim a la llista
						presences.add(p);
					}
				}	
				
				return presences;
				
			} else if (this.mode.equals("room")){
			
				//Localitzem el node pare <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Busquem els fills <message> i per a cada un ...
				NodeList nl = nodoPadre.getElementsByTagName("room");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creem un objecte element
						Element el = (Element) nl.item(i);
						//Li passem l'element amb el message a la funció getMessage
						//perquè ens generi un nou objecte message
						Room r = getRoom(el);
						//Finalment ho afegim a la llista
						rooms.add(r);
					}
				}
				
				return rooms;
			}
		}catch(ParserConfigurationException pce) { pce.printStackTrace(); }catch(SAXException se) { se.printStackTrace(); }catch(IOException ioe) { ioe.printStackTrace(); }

		return null;
	}
	
	/**
	* La fución getMessage pren un objecte de tipus element (de la col · lecció d'objectes DOM) i
	* crea un objecte message transformant els atributs i tags del seu interior.
	* retorna el missatge creat
	* @param messageElement
	* @return
	*/

	private Message getMessage(Element messageElement) {

		//Els atributs es fan directament amb la funció. GetAttribute
		String from = " ";
		from = messageElement.getAttribute("from");
		String to = " ";
		to = messageElement.getAttribute("to");
		//Però els tags interiors es tracten a part amb una nova funció
		//que ha de ser diferent depenent del dau que vulguem generar
		//en el nostre cas només volem text, per això vam crear la funció getTextValue
		//que torna la String del contingut d'un tag
		String body  = " ";
		body = getTextValue(messageElement,"body");
		//Quan tenim tots els paràmetres definits creem l'objecte
		Message message = new Message(from, to, body);

		return message;
	}
	
	/**
	 * @param presenceElement
	 * @return
	 */
	private Presence getPresence(Element presenceElement) {

		//Els atributs es fan directament amb la funció. GetAttribute
		String from = presenceElement.getAttribute("from");
		//Però els tags interiors es tracten a part amb una nova funció
		//que ha de ser diferent depenent del dau que vulguem generar
		//en el nostre cas només volem text, per això vam crear la funció getTextValue
		//que torna la String del contingut d'un tag
		String show  = getTextValue(presenceElement,"show");
		String status  = getTextValue(presenceElement,"status");
		//Quan tenim tots els paràmetres definits creem l'objecte
		Presence presence = new Presence(from, show, status);

		return presence;
	}
	
	/**
	 * @param roomElement
	 * @return
	 */
	private Room getRoom(Element roomElement) {

		//Els atributs es fan directament amb la funció. GetAttribute
		String name = roomElement.getAttribute("name");
		//Quan tenim tots els paràmetres definits creem l'objecte
		Room room = new Room(name);

		return room;
	}
	
	/**
	* La funció getTextValue pren un element DOM i una String tagname
	* i busca dins d'aquest element, el primer fill que tingui de nom
	* la String que hem passat en tagname, i retorna el contingut en forma
	* de String
	*/
	/**
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
}
