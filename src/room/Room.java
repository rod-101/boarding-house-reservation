package room;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import database.DBConnection;

public class Room {
	public int id;
	public int owner = 0;
	public int type = 0;
	public String typeName = null;
	public int price = 0;
	public String capacity = null;
	public String address = null;
	public int status = 0;
	public String statusName = null;
	public int current = 0;
	public int max = 0;
	
	public void displayRoomDetails() {
		System.out.println("Room id: " + this.id + 
								"\n\tOwner: " + this.owner +
								"\n\tStatus: " + this.statusName +
								"\n\tType: " + this.typeName + 
								"\n\tPrice: " + this.price + 
								"\n\t" + this.occupancyOrCapacity() + 
								"\n\tAddress: " + this.address
		);
	}
	
	//to return 'Occupancy:' when room type is 'bed-spacer'
	String occupancyOrCapacity() {
		if(this.typeName.equals("bed-spacer")) {
			return "Occupancy: " + this.current + "/" + this.max;
		} else {
			return "Capacity: " + capacity;
		}
		
	}
	
	
	void setRoomDetails(int roomOwner, int roomType, int roomPrice, String roomCapacity, String roomAddress, int roomStatus, int current, int max) {
		this.owner = roomOwner;
		this.type = roomType;
		this.price = roomPrice;
		this.address = roomAddress;
		this.status = roomStatus;
		if (roomType == 1 || roomType == 2) {
			this.capacity = roomCapacity;
			this.current = 0;
			this.max = 0;
		} else if(roomType == 3){
			this.current = current;
			this.max = max;
			this.capacity = current + "/" + max;
		}
	}
	
	public void setRoomID(int roomID) {
		this.id = roomID;
	}
	
	
	
	//assigns owner to each room
	void roomOwner() {
		try {
			Connection con = DBConnection.getConnection(); //opens a new db connection
			
			String query = "SELECT LAST_INSERT_ID() FROM users";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery(query);
			System.out.println("Data retrieved successfully.");
			this.owner = rs.getInt("user_id");
			
			DBConnection.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}



