package account;
import java.util.Scanner;
import database.DBConnection;



class Signup {
    String firstName;
    String lastName;
    String email;
    String password;
    String role;
    String confirmPass;
    String phone;
    String address;
    char sex;
    String birthDate;
    
    int attempts = 0;
    int maxAttempts = 3;
    boolean signUpFailed = false;

    
    
    StudentAccount student;
    LandlordAccount landlord;
    
    void signup() {
        System.out.println("Please select a role first.");
        System.out.println("\t(1)student\n\t(2)landlord");  
        askRole();
        collectUserInformation();
        
        if(signUpFailed == false) {
        	if(role.equals("student")) {	
            	student = new StudentAccount(firstName, lastName, email, password, phone, address, sex, birthDate);
            	student.insertUserData();
            } else {
            	landlord = new LandlordAccount(firstName, lastName, email, password, phone, address, sex, birthDate);
            	landlord.insertUserData();
            }
            
            signupMenu(); //go to account or exit the program
        }
    }
    
    
    
    
    void askRole() {
    	Scanner sc = new Scanner(System.in);
    	int counter = 0;
    	do {
    		counter++;
    		if(counter > 1) {
    			System.out.println("You must select a role.");
    		}
    		
        	System.out.print("Role: ");
        	role = sc.next();      
    	} while(!role.equals("1") && !role.equals("2"));
    	
    	switch(role) {
			case "1":
			role = "student";
			break;
			case "2":
			role = "landlord";
			break;
		default:   
				System.out.println("invalid role.");
		}
		
    }
    
    
    
    
    boolean collectUserInformation() {
    	System.out.println("Great! Take a moment to set up your profile.\n");
    	Scanner sc = new Scanner(System.in);
    	
    	System.out.println("First name: ");
        firstName = sc.nextLine();       

    	System.out.println("Last name: ");
        lastName = sc.nextLine();       

        System.out.print("Email: ");
        email = sc.next();       
        
        //takes and validates password
        do {
            System.out.print("Password: ");
            password = sc.next();

            System.out.print("Confirm Password: ");
            confirmPass = sc.next();
            
            if (!password.equals(confirmPass)) {
            	 attempts++;
                 if(attempts == maxAttempts) {
                 	break;
                 }
                System.out.println("\nPasswords do not match. Please try again.");
            } else {
                // Passwords match, break out of the loop
                break;
            }
        } while (attempts < maxAttempts);   	
        
        if(attempts == maxAttempts) {
        	System.out.println("Signup failed. Please try again later.");
        	return signUpFailed = true;
        }

    	System.out.print("Birth date (YYYY-MM-dd): ");
    	birthDate = sc.next();
    	sc.nextLine();
    	
    	System.out.print("sex (M/F): ");
    	sex = sc.next().charAt(0);

    	sc.nextLine(); //consumes the next line character in the buffer
    	System.out.println("Permanent address: ");
    	address = sc.nextLine();
    	
    	System.out.println("Contact number: ");
    	phone = sc.next();
    	
    	System.out.println("------------Set up done------------");
    	return signUpFailed;
    }
    
    
    
    
    void signupMenu() {
    	Scanner sc = new Scanner(System.in);
    	
    	System.out.println("(1)go to account\n(2)exit");
    	int choice = sc.nextInt();
    	
    	if(choice == 1) {
    		if(role.equals("student")) {
    			student.displayAccount();
    		} else if(role.equals("landlord")) {
    			landlord.displayAccount();
    		}
    	} else if(choice == 2){
    		System.out.println("System exited.");
    		System.exit(0);
    	}
    	
    }
    
   
}
