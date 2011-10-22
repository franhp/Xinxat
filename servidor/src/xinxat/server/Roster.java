package xinxat.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Roster extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		
		
		resp.getWriter().println("Roster Get");
		req.getParameter("hola");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
		
		
		resp.getWriter().println("Roster Post");
	}
}


