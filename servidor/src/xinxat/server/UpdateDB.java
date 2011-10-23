package xinxat.server;


/**
 * This class syncrhronizes the frontend database with the backend's
 * It is called every minute by a cron job
 * 
 * @author Fran Hermoso <franhp@franstelecom.com>
 */
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
				URL url = new URL("http://api.xinxat.com/?users");
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

				NodeList nodeLista = documento.getElementsByTagName("user");


				for (int s = 0; s < nodeLista.getLength(); s++) {
						Element element = (Element) nodeLista.item(s);
						String name = element.getAttribute("nickname");
						String code = element.getAttribute("code");
						String show = element.getAttribute("show");
						String status = element.getAttribute("status");
						if(status.isEmpty()) status = "offline";
						String lastonline = element.getAttribute("lastonline");
						if(lastonline.isEmpty()) lastonline = "0";
						Entity user = new Entity("user");
						user.setProperty("username", name);
						user.setProperty("password", code);
						user.setProperty("show", show);
						user.setProperty("status", status);
						user.setProperty("lastonline", lastonline);
						datastore.put(user);
				}
				
		  }
		  catch (Exception e) {
		    	e.printStackTrace();
		  }
				
	}
 
		

}