package xinxat.server;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RoomList extends HttpServlet {
	
	/**
	 * This method returns an xml of all the rooms and their
	 * users
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		
		ArrayList<String> rooms = xinxat.server.Server.listRooms();
		resp.setContentType("text/xml");
		resp.getWriter().println("<rooms>");
		for (String room : rooms){
			resp.getWriter().println("\t<room name=\"" + room + "\" users=\"" + xinxat.server.Server.getUsersFromRoom(room).size() + "\" />");
		}
		resp.getWriter().println("</rooms>");
	}
}