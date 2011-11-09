package xinxat.main;

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
	 * La funcion parseXmlString recibe una String en formato xml
	 * y crea un objeto DOM. Despues con un iterador busca todos los
	 * hijos del nodo padre <messages> y busca los hijos <message>
	 * los recorre creando objetos tipo message y los mete en la arraylist
	 * messages que hemos definido arriba
	 */
    
	public ArrayList<?> parseXmlString(){

		//Creamos una instancia de DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			//Con la factoria creamos una instancia de DocumenBuilder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//Creamos un inputStream con el contenido del texto xml porque la funcion
			//parse() no acepta strings a secas
			InputStream is = new ByteArrayInputStream(this.xmlString.getBytes("UTF-8"));
			//Parseamos el inputStream 
			dom = db.parse(is);
				/** ESTA PARTE NO ES COMUN **/			
			if (this.mode.equals("messages")){
				
				//Localizamos el nodo padre <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Buscamos los hijos <message> y para cada uno ...
				NodeList nl = nodoPadre.getElementsByTagName("message");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creamos un objeto element
						Element el = (Element) nl.item(i);
						//Le pasamos el elemento con el message a la función getMessage
						//para que nos genera un nuevo objeto message
						Message m = getMessage(el);
						//Finalmente lo añadimos a la lista
						messages.add(m);
					}
				}
				
				return messages;
				
			} else if (this.mode.equals("roster")){
				
				//Localizamos el nodo padre <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Buscamos los hijos <message> y para cada uno ...
				NodeList nl = nodoPadre.getElementsByTagName("presence");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creamos un objeto element
						Element el = (Element) nl.item(i);
						//Le pasamos el elemento con el message a la función getMessage
						//para que nos genera un nuevo objeto message
						Presence p = getPresence(el);
						//Finalmente lo añadimos a la lista
						presences.add(p);
					}
				}	
				
				return presences;
				
			} else if (this.mode.equals("room")){
			
				//Localizamos el nodo padre <messages>
				Element nodoPadre = dom.getDocumentElement();
				//Buscamos los hijos <message> y para cada uno ...
				NodeList nl = nodoPadre.getElementsByTagName("room");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {
						//creamos un objeto element
						Element el = (Element) nl.item(i);
						//Le pasamos el elemento con el message a la función getMessage
						//para que nos genera un nuevo objeto message
						Room r = getRoom(el);
						//Finalmente lo añadimos a la lista
						rooms.add(r);
					}
				}
				
				return rooms;
			}
		}catch(ParserConfigurationException pce) { pce.printStackTrace(); }catch(SAXException se) { se.printStackTrace(); }catch(IOException ioe) { ioe.printStackTrace(); }

		return null;
	}
	
	/**
	 * La fucion getMessage toma un objeto de tipo element (de la coleccion de objetos DOM) y
	 * crea un objeto message transformando los atributos y tags de su interior. 
	 * Devuelve el mensaje creado
	 */
	
	private Message getMessage(Element messageElement) {

		//Los atributos se hacen directamente con la funcion .getAttribute
		String from = " ";
		from = messageElement.getAttribute("from");
		String to = " ";
		to = messageElement.getAttribute("to");
		//Pero los tags interiores se tratan a parte con una nueva funcion 
		//que tiene que ser diferente dependiendo del dado que queramos generar
		//en nuestro caso solo queremos texto, por eso creamos la funcion getTextValue
		//que devuelve la String del contenido de un tag
		String body  = " ";
		body = getTextValue(messageElement,"body");
		//Cuando tenemos todos los parametros definidos creamos el objeto
		Message message = new Message(from, to, body);

		return message;
	}
	
	private Presence getPresence(Element presenceElement) {

		//Los atributos se hacen directamente con la funcion .getAttribute
		String from = presenceElement.getAttribute("from");
		//Pero los tags interiores se tratan a parte con una nueva funcion 
		//que tiene que ser diferente dependiendo del dado que queramos generar
		//en nuestro caso solo queremos texto, por eso creamos la funcion getTextValue
		//que devuelve la String del contenido de un tag
		String show  = getTextValue(presenceElement,"show");
		String status  = getTextValue(presenceElement,"status");
		//Cuando tenemos todos los parametros definidos creamos el objeto
		Presence presence = new Presence(from, show, status);

		return presence;
	}
	
	private Room getRoom(Element roomElement) {

		//Los atributos se hacen directamente con la funcion .getAttribute
		String name = roomElement.getAttribute("name");
		//Cuando tenemos todos los parametros definidos creamos el objeto
		Room room = new Room(name);

		return room;
	}
	/**
	 * La funcion getTextValue toma un elemento DOM y una String tagName
	 * y busca dentro de ese elemento, el primero hijo que tenga de nombre
	 * la String que hemos pasado en tagName, y devuelve el contenido en forma
	 * de String
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
	
	/**
	 * La funcion printData no hace más que recorrer el arrayList messages
	 * y muestra todos los Mensajes gracias a la funcion toString especial
	 * del objeto
	 */
	/*
	private void printData(){
		
		Iterator<Message> itr = messages.iterator();
		while (itr.hasNext()) {
			Message nM = (Message) itr.next();
			MessageBox(nM.toString());
		}
	}*/
	
	/**
	 * MessageBox es una funcion util para simular un messageBox
	 * en Android, le pasamos una String
	 */
}
