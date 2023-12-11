package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import database.DBConnection;
import feed.Feed;
import room.MyRooms;

public class LandlordAccount extends Account{
	public static String currentPage;
	public static int landlordID;
	int currentCapacity = 0;
	int maxCapacity = 0;
	int typeOfRoom = 0;
	
	public LandlordAccount(String firstName, String lastName, String email,
					String password, String phone, String address,
					char sex, String birthDate) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = "landlord";
		this.username = firstName + " " + lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.sex = sex;
		this.birthDate = birthDate;
		this.userID = uniqueIdGenerator();
		myRooms = new MyRooms(this.userID);
		
	}
	
	
	public LandlordAccount(String firstName, String lastName, String email,
			String password, String phone, String address,
			char sex, String birthDate, int userID) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = "landlord";
		this.username = firstName + " " + lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.sex = sex;
		this.birthDate = birthDate;
		this.userID = userID;
		LandlordAccount.landlordID = userID;
		myRooms = new MyRooms(this.userID);
		
	}
	
	public LandlordAccount() {
		
	}
	
	
	void displayAccount() {
		currentPage = "Account";
		System.out.println("\n----------- Your Profile -----------");
    	System.out.println("Username: " + username);
    	System.out.println("User ID: " + userID);
    	System.out.println("Role: " + role);
		System.out.println("Email: " + email);
		System.out.println("Password: " + password);
		System.out.println("Contact number: " + phone);
		System.out.println("Permanent address: " + address);
		System.out.println("Sex: " + sex);
		System.out.println("Birth date: " + birthDate);
		
		mainMenu();
	}
	
	
	int getuserID() {
		return landlordID;
	}
	
	
	
	void mainMenu() {
		try {
			currentPage = "menu";
			Scanner sc = new Scanner(System.in);
			System.out.println("[Menu]: (1)Feed (2)My rooms (3)Profile (4)Requests (5)Exit");
			int choice = sc.nextInt();

			if(choice == 1) {
				if(!currentPage.equals("Feed")) {
					Feed feed = new Feed();
					feed.getAvailableRooms();
					feed.displayFeed();
				} else {
					System.out.println("Already in the " + currentPage + " page.");
				}
				mainMenu();
				
			} else if(choice == 2) {
				if(!currentPage.equals("My rooms")) {
					getuserID();
					myRooms.displayMyRooms(landlordID);
				} else {
					System.out.println("Already in the " + currentPage + " page.");
				}
				mainMenu();
				
			} else if(choice == 3) {
				if(!currentPage.equals("Account")) {
					displayAccount();
				} else {
					System.out.println("Already in the " + currentPage + " page.");
				}
				mainMenu();
				
			} else if(choice == 4) {
				if(!currentPage.equals("Requests")) {
					displayReservations();
				} else {
					System.out.println("Already in the " + currentPage + " page.");
				}
			} else if(choice == 5) {
				System.out.println("System exited.");
				System.exit(0);
			} else {
				System.out.println("Not a valid option.");
				mainMenu();
			}
		} catch (Exception e) {
			System.out.println("Invalid input");
			mainMenu();
		}
	}
	
	
	void viewRequestDetails() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter student id: ");
		int userId = sc.nextInt();
		try {
			Connection con = DBConnection.getConnection();
			String query = "SELECT user_id, first_name, last_name, phone,"
							+ " address, sex, birth_date"
							+ " FROM users WHERE user_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				int boarderId = rs.getInt("user_id");
				String fName = rs.getString("first_name");
				String lName = rs.getString("last_name");
				String phone = rs.getString("phone");
				String address = rs.getString("address");
				char sex = rs.getString("sex").charAt(0);
				String birthDate = rs.getString("birth_date");
				
				System.out.println("User id: " + boarderId);
				System.out.println("\tFirst name: " + fName);
				System.out.println("\tLast name: "+ lName);
				System.out.println("\tSex: " + sex);
				System.out.println("\tPhone: " + phone);
				System.out.println("\tPermanent Address: " + address);
				System.out.println("\tBirth date: " + birthDate);
			} else {
				System.out.println("Id not found.");
				viewRequestDetails();
			}
			
			DBConnection.closeConnection(con);
			requestsMenu();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	void respond() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter request id: ");
		String reqId = sc.next();
		
		System.out.println("Select response: ");
		System.out.println("1. confirm request \n2. deny request");
		int response = sc.nextInt();
		
		response += 2;
		if(response == 3) {
			confirmRequest(reqId, response);
			requestsMenu();
		} else if (response == 4){
			denyRequest(reqId, response);
			requestsMenu();
		}
	}
	
	void confirmRequest(String reqId, int response) {
		
		try {
			Connection con = DBConnection.getConnection();
			
			String query7 = "SELECT status FROM reservations WHERE request_id = ?";
			PreparedStatement stmt7 = con.prepareStatement(query7);
			stmt7.setString(1, reqId);
			ResultSet rs7 = stmt7.executeQuery();
			if(rs7.next()) {
				int requestStatus = rs7.getInt("status");
				if(requestStatus == 2) {
					System.out.println("Cannot confirm cancelled request.");
				} else if(requestStatus == 4) {
					System.out.println("Cannot confirm denied request.");
				} else if(requestStatus == 3) {
					System.out.println("Request already confirmed.");
				} else if(requestStatus == 1) {
					//gets the type and id of room of the passed request id
					String getType = "SELECT rooms.type, room_id"
									+ " FROM rooms INNER JOIN reservations"
									+ " ON rooms.room_id = reservations.room"
									+ " WHERE request_id = ?";
					PreparedStatement stmt5 = con.prepareStatement(getType);
					stmt5.setString(1, reqId);
					ResultSet rs2 = stmt5.executeQuery();
				
					if(rs2.next()) {
						int id = rs2.getInt("room_id");
						int type = rs2.getInt("type");
						
						//updates room to 'occupied' when room type is 'family' or 'single'
						if (type == 1 || type == 2) {
							String query2 = "UPDATE rooms"
											+ " SET rooms.status = 2" /*also set the current_occupancy and max_occupancy to 0*/
											+ " WHERE room_id = ?";
							PreparedStatement stmt2 = con.prepareStatement(query2);
							stmt2.setInt(1, id);
							stmt2.executeUpdate();
							
							//updates request status to 'confirmed'
							String query = "UPDATE reservations SET status = ? WHERE request_id = ?";
							PreparedStatement stmt = con.prepareStatement(query);
							stmt.setInt(1, response);
							stmt.setString(2, reqId);
							stmt.executeUpdate();
							
							System.out.println("Request confirmed.\n");
							
						//if room type is 'bed-spacer'
						} else if(type == 3) {
							String query3 = "SELECT"
											+ " rooms.current_occupancy,"
											+ " rooms.max_occupancy,"
											+ " rooms.capacity"
											+ " FROM rooms"
											+ " WHERE room_id = ?";
							PreparedStatement stmt3 = con.prepareStatement(query3);
							stmt3.setInt(1, id);
							ResultSet rs = stmt3.executeQuery();
							
							
							if(rs.next()) {
								currentCapacity = rs.getInt("current_occupancy");
								maxCapacity = rs.getInt("max_occupancy");
								
								if(currentCapacity < maxCapacity){ 
									currentCapacity += 1; 
									String query4 = "UPDATE rooms"
													+ " SET current_occupancy = ?" /*also, set capacity to null*/
													+ " WHERE room_id = ?";
									PreparedStatement stmt4 = con.prepareStatement(query4);
									
									stmt4.setInt(1, currentCapacity);
									stmt4.setInt(2, id);
									stmt4.executeUpdate();
									
									//updates request status to 'confirmed'
									String query = "UPDATE reservations SET status = ? WHERE request_id = ?";
									PreparedStatement stmt = con.prepareStatement(query);
									stmt.setInt(1, response);
									stmt.setString(2, reqId);
									stmt.executeUpdate();
									
									System.out.println("this room's occupancy: " + currentCapacity + "/" + maxCapacity);
									System.out.println("Request confirmed.\n");
									
									
									//update room status to occupied when room is full
									if(currentCapacity == maxCapacity) {
										String query6 = "UPDATE rooms SET status = 2";
										PreparedStatement stmt6 = con.prepareStatement(query6);
										stmt6.executeUpdate();
									}		
								} else {
									System.out.println("The room is full. Should not be displayed in the feed in the first place.");	
								}
							} else {
								System.out.println("The request id is non-existent");
							}
						}
					}
				}
			} else { System.out.println("Request id is non-existent.");}
			
			DBConnection.closeConnection(con);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void denyRequest(String reqId, int response) {
		try {
			Connection con = DBConnection.getConnection();
			String query = "UPDATE reservations SET status = ? WHERE request_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, response);
			stmt.setString(2, reqId);
			stmt.executeUpdate();
			System.out.println("Request denied.\n");
			
			DBConnection.closeConnection(con);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	void requestsMenu() {
		Scanner sc = new Scanner(System.in);
		System.out.println("[Options]: (1)Respond (2)View student details (3)Back");
		int choice = sc.nextInt();
		
		if(choice == 1) {
			respond();
		} else if(choice == 2) {
			viewRequestDetails();
		} else if(choice == 3) {
			mainMenu();
		} else {
			System.out.println("Invalid input.");
			requestsMenu();
		}
	}
	
	
	
	void displayReservations() {
		currentPage = "Requests";
		System.out.println("\n------- Reservation Requests -------");
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT "
					+ "	request_id, "
					+ "    room, "
					+ "    type_name, "
					+ "    boarder, "
					+ "    status_name,"
					+ "    move_in_date "
					+ "FROM "
					+ "	reservations "
					+ "INNER JOIN "
					+ "	reservation_status ON reservations.status = reservation_status.status_id  "
					+ "INNER JOIN "
					+ "	room_type ON reservations.type = room_type.type_id "
					+ "WHERE reservations.owner = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, getuserID());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				do {
					String moveIn = rs.getString("move_in_date");
					String requestId = rs.getString("request_id");
					int roomId = rs.getInt("room");
					int  boarder = rs.getInt("boarder");
					String status = rs.getString("status_name");
					String type = rs.getString("type_name");
					
					//display requests
					System.out.println("Request id: " + requestId);
					System.out.println("\tRoom id: " + roomId);
					System.out.println("\tType: " + type);
					System.out.println("\tStudent id: " + boarder);
					System.out.println("\tMove in: " + moveIn);
					System.out.println("\tStatus: " + status);
					System.out.println("");
				} while(rs.next());
			} else {
				System.out.println("No reservation requests at the moment.\n");
				mainMenu();
			}
			
			DBConnection.closeConnection(con);
			requestsMenu();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//retrieve reservation data with status_name
	/*SELECT reservation_id, room_id, owner, boarder, status_name
FROM reservations INNER JOIN reservation_status
ON reservations.status = reservation_status.status_id*/
}
