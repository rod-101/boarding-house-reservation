package account;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import database.DBConnection;
import reservation.Reservation;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Login {
    private String email;
    private String password;
    private String fName = null;
    private String lName = null;
    private int userID = 0;
    private String phone = null;
    private String address = null;
    private char sex = '0';
    private String birthDate = null;
    private String role = null;
    
    static int counter = 0;
    
    void userLogin(){
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Email: ");
        email = sc.next();
        System.out.print("Password: ");
        password = sc.next();
        
        //checks if info matches data in the database
        try {
  			Connection con = DBConnection.getConnection();
  			
  			String query = "SELECT * FROM users WHERE email = ?";
  			String userPwd = "";
  			
	  		PreparedStatement stmt = null;
  			stmt = con.prepareStatement(query);
  			stmt.setString(1, email);
  			ResultSet rs = stmt.executeQuery();
  			
  			if(rs.next()) {
  				userPwd = rs.getString("password");
  				if(userPwd.equals(password)) {
  					//if passwords match, get all other info
  					email = rs.getString("email");
  					fName = rs.getString("first_name");
  					lName = rs.getString("last_name");
  					userID = rs.getInt("user_id");
  					phone = rs.getString("phone");
  					address = rs.getString("address");
  					sex = rs.getString("sex").charAt(0);
  					birthDate = rs.getString("birth_date");
  					role = rs.getString("role");
  					
  					loginMenu();
  				} else {
  					System.out.println("Wrong email or password.\n");
  					userLogin();
  				}
  			} else {
  				System.out.println("Wrong email or password.\n");
  				userLogin();
  			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
  
    private void loginMenu() {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("(1)go to account\n(2)exit");
    	
    	try {
    		int choice = sc.nextInt();
        	if(choice == 1 && role.equals("student")) {
        		StudentAccount student =  new StudentAccount(fName, lName, email, password, phone, address, sex, birthDate, userID);
        		student.displayAccount();
        	} else if(choice == 1 && role.equals("landlord")){
        		LandlordAccount landlord = new LandlordAccount(fName, lName, email, password, phone, address, sex, birthDate, userID);
        		landlord.displayAccount();
        		
        	} else if(choice == 2) {
        		System.out.println("System exited.");
        		System.exit(0);
        	} else {
        		System.out.println("Not in the choices.");
        		loginMenu();
        	}
    	} catch (Exception e) {
    		System.out.println("Invalid input");
    		loginMenu();
    	}
    	
    }
}

