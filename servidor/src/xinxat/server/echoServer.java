package xinxat.server;

import java.io.IOException;

import java.util.Stack;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class echoServer extends HttpServlet {
	
	public Stack<String> pila = new Stack<String>();
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	String missatge = req.getParameter("msg");
    	resp.getWriter().println("OK");
    	pila.push(missatge);
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	
    	resp.setContentType("text/plain");
    	
    	if(pila.isEmpty()) resp.getWriter().println("EMPTY");
	    else {
	    	while (!pila.isEmpty()){
	    		String missatge = pila.pop();
	    		resp.getWriter().println(missatge);
	    	}
	    }
    }

}