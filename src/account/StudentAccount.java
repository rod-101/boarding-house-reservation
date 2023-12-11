package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import database.DBConnection;
import feed.Feed;
import reservation.Reservation;

public class StudentAccount extends Account {
	public static String currentPage = null;
	public static int studentID;
	public static StudentAccount student;
	
	//for sign up
	public StudentAccount(String firstName, String lastName, String email,
						  String password, String phone, String address,
						  char sex, String birthDate) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = "student";
		this.username = firstName + " " + lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.sex = sex;
		this.birthDate = birthDate;
		this.userID = uniqueIdGenerator();
		StudentAccount.student = this;
	}
	
	//for login
	public StudentAccount(String firstName, String lastName, String email,
			  String password, String phone, String address,
			  char sex, String birthDate, int userID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = "student";
		this.username = firstName + " " + lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.sex = sex;
		this.birthDate = birthDate;
		this.userID = userID;
		StudentAccount.student = this;
	}
	
	public StudentAccount() {
		
	}
	
	
	
	public int getStudentID() {
		return student.userID;
	}
	
//	public String getFullName() {
//		String fullName = this.firstName + " " + this.lastName;
//		return  fullName;
//	}
	
	
	void displayAccount() {
		currentPage = "Profile";
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
	
	
	void myRequests() {
		currentPage = "My Requests";
		System.out.println("\n---------- My Requests ------------");
		try {
			Connection con = DBConnection.getConnection();
			String query = "SELECT request_id, room, status_name, owner "
					+ "FROM reservations INNER JOIN reservation_status "
					+ "ON reservations.status = reservation_status.status_id "
					+ "WHERE reservations.boarder = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, getStudentID());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				do {
					String requestId = rs.getString("request_id");
					int roomId = rs.getInt("room");
					String status = rs.getString("status_name");
					int owner = rs.getInt("owner");
					
					System.out.println("Request id: " + requestId);
					System.out.println("\tRoom id: " + roomId);
					System.out.println("\tOwner: " + owner);
					System.out.println("\tStatus: " + status);
					System.out.println("");
				} while(rs.next());
			} else {
				System.out.println("No reservation requests at the moment.\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myRequestsOptions();
	}
	
	
	void myRequestsOptions() {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("[Options]: (1)Cancel Request (2)View owner details (3)Back");
			
			int choice = sc.nextInt();
			if (choice == 1) {
				System.out.print("Enter request id: ");
				String reqId = sc.next();
				cancelRequest(reqId);
			} else if (choice == 2) {
				System.out.print("Enter owner id: ");
				int ownerId = sc.nextInt();
				viewOwnerDetails(ownerId);
			} else if (choice == 3) {
				mainMenu();
			} else {
				System.out.println("Not in the options.");
			}
		} catch (Exception e) {
			System.out.println("Invalid input.");
			myRequestsOptions();
		}
	}
	
	
	void viewOwnerDetails(int ownerId) {
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT"
							+ " first_name,"
							+ " last_name,"
							+ " sex,"
							+ " phone,"
							+ " address,"
							+ " birth_date"
							+ " FROM users WHERE user_id = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, ownerId);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				String fName = rs.getString("first_name");
				String lName = rs.getString("last_name");
				char sex = rs.getString("sex").charAt(0);
				String phone = rs.getString("phone");
				String address = rs.getString("address");
				String bDate = rs.getString("birth_date");
				
				System.out.println("User ID: " + ownerId);
				System.out.println("\tFirst name: " + fName);
				System.out.println("\tLast name: " + lName);
				System.out.println("\tSex: " + sex);
				System.out.println("\tPhone: " + phone);
				System.out.println("\tAddress: " + address);
				System.out.println("\tBirth date: " + bDate);
			} else {
				System.out.println("No such user.");
			}
			myRequestsOptions();
		} catch (Exception e) {
			e.printStackTrace();
			myRequestsOptions();
		}
	}
	
	
	void cancelRequest(String reqId) {
		try {
			Connection con = DBConnection.getConnection();
			
			String query2 = "SELECT rooms.type, current_occupancy FROM rooms"
							+ " INNER JOIN reservations"
							+ "	ON rooms.room_id = reservations.room"
							+ " WHERE request_id = ?";
			PreparedStatement stmt2 = con.prepareStatement(query2);
			stmt2.setString(1, reqId);
			ResultSet rs = stmt2.executeQuery();
			
			String query5 = "SELECT status FROM reservations WHERE request_id = ?";
			PreparedStatement stmt5 = con.prepareStatement(query5);
			stmt5.setString(1, reqId);
			ResultSet rs2 = stmt5.executeQuery();
			
			
			if(rs.next()) {
				int roomType = rs.getInt("type");
				int currentOccupancy = rs.getInt("current_occupancy");
				if(roomType == 3) {
					if(rs2.next()) {
						System.out.println("status retrieved.");
						int status = rs2.getInt("status");
						if(status == 3) {
							currentOccupancy -= 1;
							String query3 = "UPDATE rooms"
											+ " INNER JOIN reservations"
											+ " ON rooms.room_id = reservations.room"
											+ " SET current_occupancy = ?"
											+ " WHERE request_id = ?";
							PreparedStatement stmt3 = con.prepareStatement(query3);
							stmt3.setInt(1, currentOccupancy);
							stmt3.setString(2, reqId);
							stmt3.executeUpdate();
							
							String query = "UPDATE reservations"
									+ " SET status = 2"
									+ " WHERE request_id = ?";
							PreparedStatement stmt = con.prepareStatement(query);
							stmt.setString(1, reqId);
							stmt.executeUpdate();
							
							String query8 = "UPDATE rooms"
											+ " SET status = 1";
							PreparedStatement stmt8 = con.prepareStatement(query8);
							stmt8.executeUpdate();
							System.out.println("Request cancelled.");
							myRequests();
						} else if(status == 1) {
							String query = "UPDATE reservations"
									+ " SET status = 2"
									+ " WHERE request_id = ?";
							PreparedStatement stmt = con.prepareStatement(query);
							stmt.setString(1, reqId);
							stmt.executeUpdate();
							System.out.println("Request cancelled.");
							myRequests();
						} else if (status == 2) {
							System.out.println("Request already cancelled");
							myRequestsOptions();
						} else if (status == 4) {
							System.out.println("Cannot cancel denied request.");
							myRequestsOptions();
						}
					} else {System.out.println("There is no status retrieved.");}
					
				} else if(roomType == 1 || roomType == 2) {
					String query4 = "UPDATE reservations"
									+ " SET status = 2"
									+ " WHERE request_id = ?";
					PreparedStatement stmt4 = con.prepareStatement(query4);
					stmt4.setString(1, reqId);
					stmt4.executeUpdate();
					System.out.println("Request cancelled.");
				}
			} else { 
				System.out.println("No such request.");
				myRequestsOptions();
			}
			DBConnection.closeConnection(con);
		} catch (Exception e) {
			e.printStackTrace();
			myRequestsOptions();
		}
	}
	
	
	public void mainMenu() {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("[Menu]: (1)Feed (2)Reservation (3)Profile (4)My requests (5)Exit");
			int choice = sc.nextInt();
			
			if(choice == 1) {
				if(!currentPage.equals("Feed")) {
					Feed feed = new Feed();
					feed.getAvailableRooms();
					feed.displayFeed();
				} else {
					System.out.println("Already in the " + currentPage + " Page.");
				}
				mainMenu();
				
			} else if (choice == 2) {
				Reservation res = new Reservation();
				res.displayReservation();
				System.out.println("");
				mainMenu();
				
			} else if (choice == 3) {
				if (!currentPage.equals("Profile")) {
					displayAccount();
				} else {
					System.out.println("Already in the " + currentPage + " Page.");
				}
				mainMenu();
				
			} else if(choice == 4) {
				myRequests();
				
			} else if(choice == 5) {
				System.out.println("System exited.");
				System.exit(0);
			} else {
				System.out.println("None of the choices.");
				mainMenu();
			}
		} catch (Exception e) {
			System.out.println("Invalid input.");
			mainMenu();
		}
	}
	
	
	
}