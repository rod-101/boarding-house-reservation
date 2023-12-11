package reservation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import account.StudentAccount;
import database.DBConnection;
import room.MyRooms;
import room.Room;

public class Reservation extends StudentAccount{
	static String currentPage = "Reservation";
	String moveInDate;
	int roomID;
	int roomOwner;
	int status;
	String reservationID;
	int roomType;
	
	public void displayReservation() {
		System.out.println("\n------------ Reservation ------------");
		StudentAccount.currentPage = currentPage;
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter room ID: ");
		roomID = sc.nextInt();
		
		checkIfIdExists(roomID);
		
		System.out.print("Move-in date (YYYY-MM-DD): ");
		moveInDate = sc.next();
		
		
		
		System.out.println("\nYour following information will be shared with the landlord:");
		System.out.println(" • Full name\n • Contact\n • Address\n • Sex\n • Birth date\n");
		
		finalizeReservation();
	}
	
	
	void finalizeReservation() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please review your inputs and select yes to send request.");
		System.out.println("(1)Yes\n" + "(2)Cancel");
		int choice = sc.nextInt();
		
		if(choice == 1) {
			sendRequest();
		} else if(choice == 2) {
			System.out.println("Request cancelled.");
		}
	}
	
	
	
	void checkIfIdExists(int roomID) {
		int  id = 0;
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT room_id, owner FROM rooms WHERE room_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, roomID);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				id = rs.getInt("room_id");
				roomOwner = rs.getInt("owner");
			} else if(roomID != id) {
				System.out.println("Id not found.");
				StudentAccount.student.mainMenu();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	String generateRequestID() {
		ArrayList<String> reqIds = new ArrayList<>();
		Random rand = new Random();
		int id;
		
		try {
			Connection con = DBConnection.getConnection();
			String query = "SELECT request_id FROM reservations";
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				String reqID = rs.getString("request_id");
				reqIds.add(reqID);
			}
			
										//THERE'S A LOGIC ERROR IN HERE
			id =  (rand.nextInt(9000) + 1000);
			reservationID = "#" + id;
			
			for(int i = 0; i < reqIds.size(); ++i) {
				if(reqIds.get(i).equals(reservationID)) {
					id =  (rand.nextInt(9000) + 1000);
					reservationID = "#" + id;
					i=0;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return reservationID;
	}
	
	
	void findRoomType() {
		try {
			Connection con = DBConnection.getConnection();
			String query = "SELECT type FROM rooms WHERE room_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, roomID);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				roomType = rs.getInt("type");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	void sendRequest() {
		status = 1; // pending
		try {
			Connection con = DBConnection.getConnection();
			
			
			String query = "INSERT INTO reservations (request_id, owner, room, boarder, status, type, move_in_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(query);
			//reservation_id
			stmt.setString(1, generateRequestID());
			stmt.setInt(2, roomOwner);
			stmt.setInt(3, roomID);
			stmt.setInt(4, StudentAccount.student.getStudentID());
			stmt.setInt(5, status);
			findRoomType();
			stmt.setInt(6, roomType);
			stmt.setString(7, moveInDate);
			stmt.executeUpdate();
			
			
			System.out.println("Request sent! Request ID: " + reservationID + "\nYou can check the status of your request on the 'My Requests' page.");
			DBConnection.closeConnection(con);
		} catch (Exception e) {
			System.out.println("invalid move-in date.");
		}
	} 
}
