package xinxat.server;

/**
 * This class returns a list of all the users and their presence status.
 * 
 * @author Fran Hermoso <franhp@franstelecom.com>
 */
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class Roster extends HttpServlet {
	
	/**
	 * This variable defines the time after which the user is considered offline
	 */
	private int time2BeOffline = 90;

	/**
	 * Returns the presence of every user when sending a request to:
	 * 		http://projecte-xinxat.appspot.com/roster
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query q = new Query("user");
		PreparedQuery pq = datastore.prepare(q);
		try {
			for (Entity result : pq.asIterable()) {
				long comp = 0;
					try{
						//If the user hasn't refreshed the lastonline key on the datastore for a long time ...
						long lastonline = Long.parseLong(result.getProperty("lastonline").toString());
						long now = (long)(System.currentTimeMillis() / 1000L);
						comp = now - lastonline;
					} catch (NumberFormatException e) {
						resp.getWriter().println("");
					}
					//The user is considered to be offline
					if(comp > time2BeOffline)
						result.setProperty("show", "offline");
					
					resp.getWriter().println("<presence from=\""+ result.getProperty("username") + "\">" +
												"\n\t<show>" +result.getProperty("show") + "</show>" + 
												"\n\t<status>" +result.getProperty("status") +"</status>" + 
										"\n</presence>");
			}
		}
		catch (NullPointerException e){
			resp.getWriter().println("Reload");
		}
	}

}


