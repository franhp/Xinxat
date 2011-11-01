package xinxat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@SuppressWarnings("serial")
public class UpdateRooms extends HttpServlet {
		
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		
		doUpdate();
				
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		String destroy = req.getParameter("destroy");
		if("yes".equals(destroy)){
			ArrayList<String> rooms = xinxat.server.Server.listRooms();
			for(String room : rooms){
				resp.getWriter().println("\n-" + room);
				ArrayList<String> users = xinxat.server.Server.getUsersFromRoom(room);
				for(String user: users){
					resp.getWriter().println("\n--" + user);
				}
			}
			xinxat.server.Server.resetRooms();
		}
	}
	
	public static void doUpdate(){
		try {
			//Cleanup
			xinxat.server.Server.resetRooms();
			//Ir a buscar usuarios
			URL url = new URL("http://api.xinxat.com/?roomlist");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String entrada = "";
			String cadena = "";

			while ((entrada = br.readLine()) != null){
				cadena += entrada;
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource archivo = new InputSource();
			archivo.setCharacterStream(new StringReader(cadena)); 

			Document documento = db.parse(archivo);

			NodeList nodeLista = documento.getElementsByTagName("room");
			
			for (int s = 0; s < nodeLista.getLength(); s++) {
				Element element = (Element) nodeLista.item(s);
				String room = element.getAttribute("name");

				NodeList secondNodeList = element.getElementsByTagName("users");
				for(int i = 0; i < secondNodeList.getLength(); i++){
					Element secondElement = (Element)secondNodeList.item(i);
					NodeList thirdNodeList = secondElement.getElementsByTagName("user");
					for(int y = 0; y < thirdNodeList.getLength(); y++){
						Element userElement = (Element)thirdNodeList.item(y);
				
						String user = userElement.getAttribute("nickname");
						String state = userElement.getAttribute("state");
					
						if("1".equals(state)) {
							xinxat.server.Server.addUserToRoom(user, room);
						}
						else if("-1".equals(state)){
							xinxat.server.Server.ban(user, room);
						}
					}
				}

			}
				
		  }
		  catch (Exception e) {
		    	e.printStackTrace();
		  }
	}
	

}