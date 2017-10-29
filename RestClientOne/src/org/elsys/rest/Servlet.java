package org.elsys.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final String USER_AGENT = "Mozilla/5.0";
       
    public Servlet() {
        super();
    }
    
    // HTTP GET request
 	private String sendGet() throws Exception {
 		//TODO read this from the app's environment e.g. a pre-defined variable
 		String url = "https://servlettwo.cfapps.io/Servlet";

 		URL obj = new URL(url);
 		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

 		// optional default is GET
 		con.setRequestMethod("GET");

 		//add request header
 		con.setRequestProperty("User-Agent", USER_AGENT);
 		
 		//TODO always close the stream's used in your codes. 
 		//OS handles may be left non-returned and the kernel might restrict access to resources 
 		//or crash if it's handles are depleted
 		//You may do it with a try, initializing an AutoCloseable e.g.
 		// 		try (InputStream in = con.getInputStream()){
 		// 	 		;}
 		InputStream in = con.getInputStream();
 		
 		String result = new BufferedReader(new InputStreamReader(in))
 				  .lines().collect(Collectors.joining("\n"));//cool :)
 		
 		return result;

 	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String message = "";
		
		try {
			message = sendGet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.getWriter().append("Servlet Two response: " + message);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}

}
