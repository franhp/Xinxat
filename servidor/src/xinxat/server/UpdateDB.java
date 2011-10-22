package xinxat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;


@SuppressWarnings("serial")
public class UpdateDB extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {		
			try {

				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

				//Borrar todo
				Query q = new Query("user");
				PreparedQuery pq = datastore.prepare(q);
				for (Entity result : pq.asIterable()) 
					datastore.delete(result.getKey());
				
				//Ir a buscar usuarios
				URL url = new URL("http://xinxat.com/scripts/api.php?users&xml");
				//URL url = new URL("http://xinxat.com/scripts/api.php?roomlist&xml");
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
				//documento.getDocumentElement().normalize();

				NodeList nodeLista = documento.getElementsByTagName("user");

				String name = null;
				String code = null;
				for (int s = 0; s < nodeLista.getLength(); s++) {
						Element element = (Element) nodeLista.item(s);
						name = element.getAttribute("nickname");
						code = element.getAttribute("code");
						Entity user = new Entity("user");
						user.setProperty("username", name);
						user.setProperty("password", code);
						datastore.put(user);
				}
				
		  }
		  catch (Exception e) {
		    	e.printStackTrace();
		  }
				
	}
 
		

}