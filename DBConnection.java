import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/crime_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static Connection
    getConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to postgreSQL successfully!");
            return conn;
        }catch (Exception e){
            System.out.println("Connection failed");
            e.printStackTrace();
            return null;
        }
    }
}
