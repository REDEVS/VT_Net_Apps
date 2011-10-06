package com.love.apps.testServlet;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TestWebservletServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		
		int a = Integer.parseInt( req.getParameter("a") );
		int b = Integer.parseInt( req.getParameter("b") );
		
		resp.getWriter().println("Result: " + (a*b));
	}
	
}
