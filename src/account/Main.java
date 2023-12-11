package account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import database.DBConnection;


public class Main{
	int counter = 0;

    public static void main(String[] args){
    	
    	
        System.out.println("\n======== Welcome to BoardGO! ========\n");
        
        Main user = new Main();
        user.hasAccount();
    }
    
    
    
    void hasAccount() {
    	Scanner sc = new Scanner(System.in);
    	if(counter == 0) {
    		System.out.println("Have an existing account? (y/n): ");
    		
    	}
    	
    	char hasAccount = sc.next().charAt(0);
    	if(hasAccount == 'y') {
    		if(counter == 0) {
    			System.out.println("\n-------------- Log In --------------");
    		}
    		Login login = new Login();
            login.userLogin(); 
    	} else if(hasAccount == 'n') {
    		if(counter == 0) {
    			System.out.println("\n-------------- Sign Up --------------");
    		}
    		Signup signup = new Signup();
            signup.signup();
    	} else {
    		System.out.println("Invalid input.");
    		++counter;
    		hasAccount();
    	}
    }
    
    
}