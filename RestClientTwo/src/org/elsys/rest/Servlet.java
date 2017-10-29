package org.elsys.rest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static AtomicLong counter = new AtomicLong(0);

    public Servlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//not using the headers right now
		response.addHeader("ServeTimes", "" + counter.getAndIncrement());
		
		response.getWriter().append("Served " + counter.getAndIncrement() + " times");
	}

	// Post methods are generally used to update a (rest) resource. The way it's persisted should not matter. 
	// with Database
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}

}
