package org.elsys.rest;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Test")
public class RestServiceTwo {
	
	private static AtomicLong counter = new AtomicLong(0);
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    private void insertData() throws Exception {
        preparedStatement = connect
        		.prepareStatement("INSERT INTO Responses"
		    		+ "(Message) VALUES (?);");
        preparedStatement.setString(1, "Called " + counter.getAndIncrement() + " times");
        preparedStatement.executeUpdate();
    }
    
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getResponse() throws SQLException {
		StringBuilder message = new StringBuilder();
		try {
			// This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://localhost:8081/cinemaDB",
                            "cinemaAdmin", "cinema_Pass123");
            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            statement.executeQuery("CREATE TABLE Responses("
        		+ "Id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
        		+ "Message VARCHAR(50) NOT NULL"
        		+ ");");
            insertData();
			resultSet = statement
                    .executeQuery("SELECT res.Message"
							+ " FROM Responses AS res;");
			resultSet.next();
			message.append(String.format("Call message: ", resultSet.getString("Message")));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connect != null) {
                connect.close();
            }
		}
		
		return message.toString();
	}
}
