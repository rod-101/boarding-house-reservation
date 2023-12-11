
//TODO 
//if roomType=="bed spacer" =>  validate input "n/n"
//if roomType=="family" => validate input "n"
//if roomType=="single" => validate input "n"


package room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import account.LandlordAccount;
import database.DBConnection;
import java.sql.ResultSet;

public class MyRooms {
	public ArrayList<Room> rooms;
	Room room;
	private int roomID = 0;
	private int roomOwner = 0;
	private int roomType = 0;
	private int roomPrice = 0;
	private String roomCapacity = null;
	private String roomAddress = null;
	private int roomStatus = 0;
	private int current = 0;
	private int max = 0;
	
	public MyRooms(int userID) {
		rooms = new ArrayList<>();
		this.roomOwner = userID;
	}
	
	public void displayMyRooms(int ownerID) {
		rooms.clear();
		
		System.out.println("\n----------- Your Rooms ------------");
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT "
					+ "	rooms.room_id, "
					+ " rooms.owner, "
					+ " rooms.price, "
					+ " rooms.capacity, "
					+ " rooms.address, "
					+ " rooms.current_occupancy, "
					+ " rooms.max_occupancy, "
					+ " room_status.status_name, "
					+ " room_type.type_name "
					+ "FROM "
					+ "	rooms "
					+ "INNER JOIN "
					+ "	room_type ON rooms.type = room_type.type_id "
					+ "INNER JOIN "
					+ "	room_status ON rooms.status = room_status.status_id "
					+ "WHERE rooms.owner = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, ownerID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				room = new Room();
				room.id = rs.getInt("room_id");
				room.owner = rs.getInt("owner");
				room.statusName = rs.getString("status_name");
				room.typeName = rs.getString("type_name");
				room.price = rs.getInt("price");
				if(room.typeName.equals("family") || room.typeName.equals("single")) {
					room.capacity = rs.getString("capacity");
				} else if(room.typeName.equals("bed-spacer")) {
					room.current = rs.getInt("current_occupancy");
					room.max = rs.getInt("max_occupancy");
					room.capacity = room.current + "/" + room.max;
				}
				
				room.address = rs.getString("address");
				rooms.add(room);
			}
			DBConnection.closeConnection(con);
			
			if(rooms.size() > 0) {
				for (Room room : rooms) {
					room.displayRoomDetails();
					System.out.println("");
				}
				myRoomsActions();
			} else {
				System.out.println("Your queue is empty.\n");
				myRoomsActions();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	void myRoomsActions() {
		Scanner sc = new Scanner(System.in);
		System.out.println("[Actions] (1)Add (2)Delete (3)Menu");
		
		int choice = sc.nextInt();
		switch (choice) {
			case 1:
				addRoom();
				break;
			case 2:
				System.out.print("Enter room ID: ");
				int roomID = sc.nextInt();
				deleteRoom(roomID);
				break;
			case 3:
				LandlordAccount.currentPage = "Menu";
				break;
			default:
				System.out.println("Not in the options.");
				myRoomsActions();
		}
	}
	
	
	
	public void askRoomDetails() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\nPlease fill out the following to create a room.");
		
		System.out.println("Room Type\n\t1. family\n\t2. single\n\t3. bed-spacer");
		System.out.print("Select: ");
		roomType = sc.nextInt();
		
		System.out.print("Price: ");
		roomPrice = sc.nextInt();
		
		if(roomType == 1 || roomType == 2) {
			System.out.print("Capacity (min-max): ");
			roomCapacity = sc.next();
		} else if (roomType == 3){
			System.out.println("Occupancy (current/max)");
			
			System.out.print("\tCurrent: ");
			current = sc.nextInt();
			System.out.print("\tMaximum: ");
			max = sc.nextInt();
		}
		
		System.out.print("Status (1-available 2-occupied): ");
		roomStatus = sc.nextInt();
		
		sc.nextLine();
		
		System.out.print("Address: ");
		roomAddress = sc.nextLine();
	}	
	
	
	
	
	
	public void addRoom() {
		room = new Room();	
		askRoomDetails();
		room.setRoomDetails(roomOwner, roomType, roomPrice, roomCapacity, roomAddress, roomStatus, current, max);
		insertDataAndFetchRoomID();
		
		System.out.println("Room added. Id is " + roomID);
		displayMyRooms(roomOwner);
	}
	
	
	
	//gets the room id of the latest insert in database
	int insertDataAndFetchRoomID() {
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "INSERT INTO rooms (owner, type, price, capacity, address, status, current_occupancy, max_occupancy) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, roomOwner);
			statement.setInt(2, roomType);
			statement.setInt(3, roomPrice);
			statement.setString(4, roomCapacity);
			statement.setString(5, roomAddress);
			statement.setInt(6, roomStatus);
			statement.setInt(7, current);
			statement.setInt(8, max);
			statement.executeUpdate();
			
			String retrieveID = "SELECT LAST_INSERT_ID() AS last_room_id";
			ResultSet result = statement.executeQuery(retrieveID);
			if(result.next()) {
				roomID = result.getInt("last_room_id");
				room.setRoomID(roomID);
			}
				
			DBConnection.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return roomID;
	}

	
	
	public void deleteRoom(int roomID) {
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "DELETE FROM rooms WHERE room_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, roomID);
			stmt.executeUpdate();
			
			String query2 = "SELECT * FROM rooms WHERE room_id = ?";
			PreparedStatement stmt2 = con.prepareStatement(query2);
			stmt2.setInt(1, roomID);
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				System.out.println("room not deleted.");
			} else {
				System.out.println("room deleted.");
			}
			
			displayMyRooms(LandlordAccount.landlordID);
			DBConnection.closeConnection(con);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
}
