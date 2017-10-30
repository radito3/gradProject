package org.elsys.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class RestClient {

	private final String USER_AGENT = "Mozilla/5.0";
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getResponse() {
		String result = "Client Two response: ";
		
		try {
			URL obj = new URL("https://servlettwo.cfapps.io/Servlet");
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	 		con.setRequestMethod("GET");
	 		con.setRequestProperty("User-Agent", USER_AGENT);
	 		
	 		InputStream in = con.getInputStream();

	 		result += new BufferedReader(new InputStreamReader(in))
	 				  .lines().collect(Collectors.joining("\n"));
	 		
	 		in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
