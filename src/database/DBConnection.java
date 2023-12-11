package database;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection{
	
	public Connection con =  null;
	private static final String URL = "jdbc:mysql://Rodjones@127.0.0.1:3306/house_reservation";
	private static final String USER = "Rodjones";
	private static final String PWD = "rod54lindaG";
	
	
	public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }
	
//	public void getConnection() {
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			//System.out.println("MySQL JDBC Driver missing!");
//			e.printStackTrace();
//		}
//		
//		try {
//			con = DriverManager.getConnection(URL, USER, PWD);
//			if (con != null) {
//			} else {
//				System.out.println("Failed to make the required connection.");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	public static void closeConnection(Connection con) {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
