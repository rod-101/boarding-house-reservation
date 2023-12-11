package account;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import database.DBConnection;
import room.MyRooms;
import room.Room;

public abstract class Account {
	protected MyRooms myRooms;
	protected String firstName = null;
	protected String lastName = null;
	protected String username = null;
	protected String email = null;
	protected String password = null;
	protected String phone = null;
	protected String address = null;
    protected char sex = '\0';
    protected String birthDate = null;
	protected String role = null;
	protected int userID = 0;
	
	//for random user id
	Statement statement = null;
	
	//Map<owner_id, owner_name>
	static Map<Integer, String> ownerIDs = new HashMap<Integer, String>();
	Random rand;
	int randomID;
	boolean noAvailableID;
	
	
    abstract void displayAccount();
	abstract void mainMenu();
	
	
	
	//gets  'user_id'  and  'first_name'  from database
	private Map<Integer, String> getUserIdFromDb() {
		DBConnection db = new DBConnection();
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT user_id, first_name FROM users";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery(query);
			
			if(ownerIDs.size() == 9000) {
				System.out.println("There is no available ID at the moment.");
				noAvailableID = true;
			} else {
				while(rs.next()) {
					noAvailableID = false;
					int userID = rs.getInt("user_id");
					String firstName = rs.getString("first_name");
				}
			}	
			DBConnection.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ownerIDs;
	}
	
	
	
	//next to "getUserIdFromDb()". This generates user id that are not in use in the database
	protected int uniqueIdGenerator() {
		getUserIdFromDb();
		try {
			if (noAvailableID == false) {
				rand = new Random();
				randomID = rand.nextInt(9000) + 1000;
				try {
					if (ownerIDs.containsKey(randomID)) {
						randomID = rand.nextInt(9000) + 1000; //regenerate random id
					} else {
						ownerIDs.put(randomID, this.firstName);
					}
			 	} catch(Exception e) {
					e.printStackTrace();
				}
			} else { //
				System.out.println("No Available ID at the moment.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomID;
	}
	
	
	
	
	
	protected void insertUserData() {
		DBConnection db = new DBConnection();
		
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "INSERT INTO users (user_id, first_name, last_name, email, password, phone, address, sex, birth_date, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, userID);
			statement.setString(2, firstName);
			statement.setString(3, lastName);
			statement.setString(4, email);
			statement.setString(5, password);
			statement.setString(6, phone);
			statement.setString(7, address);
			statement.setString(8, String.valueOf(sex));
			statement.setString(9, birthDate);
			statement.setString(10, role);
			statement.executeUpdate();
			
			db.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
