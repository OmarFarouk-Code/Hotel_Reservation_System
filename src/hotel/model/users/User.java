package hotel.model.users;
import hotel.core.Database;
import hotel.model.enums.Gender;
import hotel.model.enums.UserType;
import hotel.model.staff.Admin;
import hotel.model.staff.Receptionist;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public abstract class User implements Serializable  
{
    protected String UserName;
    protected String password;
    private UserType Typeofuser=null;
    protected Gender theGender;
    protected String newpassword;
    public User() {}

    public User(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber) {
        this.UserName = userName;
        this.password = password;
    }

     public UserType getTypeofuser() {
        return Typeofuser;
    }

    //HAS TO BE PROTECTED AND TRANSIENT TO AVOID SERIALIZATION ISSUES WITH SCANNER AND TO ALLOW ACCESS IN SUBCLASSES 
    protected transient Scanner input = new Scanner(System.in);

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static boolean Datechecker(String dateStr) {
        try {
            LocalDate.parse(dateStr); // Default YYYY-MM-DD
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void Login()
    {
        System.out.println("Please choose weather you 1.Guest 2.Admin 3.Receptionist");
        Typeofuser = UserType.valueOf(input.next().toUpperCase());//handling errors
        input.nextLine();//avoid skipping input
        System.out.println("Please enter your Username and password");
        System.out.print("Username: ");
        UserName=input.nextLine();
        System.out.print("Password: ");
        password=input.nextLine();
        if(Typeofuser== UserType.GUEST){
        boolean found=false;
        List<Guest> guestList = Database.getGuests();
        for(int i=0; i<Database.getGuests().size();i++) {
        if(guestList.get(i).getUserName().equals(UserName) && guestList.get(i).getPassword().equals(password)){
            found=true;
            break;
        }
        }
        if(found) {
            System.out.println("Access Granted,Welcome " + UserName);
            //homepage for guest
        }else {
            System.out.println("Access Denied,Please try again !");
            Login();
        }
    }if(Typeofuser==UserType.RECEPTIONIST) {
            boolean found = false;
            List<Receptionist> guestList = Database.getReceptionists();
            for (int i = 0; i < Database.getReceptionists().size(); i++) {
                if (guestList.get(i).getUserName().equals(UserName) && guestList.get(i).getPassword().equals(password)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("Access Granted,Welcome " + UserName);
                //Homepage of receptionist
            } else {
                System.out.println("Access Denied,Please try again !");
                Login();
            }
        }
            if(Typeofuser==UserType.ADMIN) {
                boolean found = false;
                List<Admin> guestList = Database.getAdmins();
                for (int i = 0; i < Database.getAdmins().size(); i++) {
                    if (guestList.get(i).getUserName().equals(UserName) && guestList.get(i).getPassword().equals(password)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    System.out.println("Access Granted,Welcome " + UserName);
                    //Homepage of admin
                } else {
                    System.out.println("Access Denied,Please try again !");
                    Login();
                }


        }


        }
        public void passwordconfirmation(String Pass1,String Pass2){
        while(!(Pass1.equals(Pass2))) {
            System.out.println("Password don't match,Please re-enter your password");
            Pass1=input.nextLine();
            System.out.println("Please confirm Password");
            Pass2=input.nextLine();
            }
        newpassword=Pass1;
        }
        public void ResetPassword(String theUserName,UserType TheUserType){
            String CurrentPassword;
             int counter=0;
            System.out.println("Please re-enter your current password");
            CurrentPassword=input.nextLine();
            if(TheUserType==UserType.ADMIN) {
                boolean found = false;
                List<Admin> guestList = Database.getAdmins();
                for (int i = 0; i < Database.getAdmins().size(); i++) {
                    if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                        found = true;
                        counter = i;
                        break;
                    }
                }
                if (found) {
                    System.out.println("Please enter new password");
                    newpassword = input.nextLine();
                    System.out.println("confirm password");
                    String confirmpass = input.nextLine();
                    passwordconfirmation(newpassword, confirmpass);
                    Admin targetAdmin = guestList.get(counter);
                    targetAdmin.setPassword(newpassword);
                    Database.saveData();
                } else {
                    System.out.println("The Username or password is not found,Please re-enter your username");
                    String AUsername = input.nextLine();
                    System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                    UserType Usercategory = UserType.valueOf(input.nextLine());
                    input.nextLine();
                    ResetPassword(AUsername, Usercategory);
                }
            }  if(TheUserType==UserType.RECEPTIONIST) {
                    boolean found = false;
                    List<Receptionist> guestList = Database.getReceptionists();
                    for (int i = 0; i < Database.getReceptionists().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter=i;
                            break;
                        }
                    }
                    if(found){
                        System.out.println("Please enter new password");
                        newpassword=input.nextLine();
                        System.out.println("confirm password");
                        String confirmpass=input.nextLine();
                        passwordconfirmation(newpassword,confirmpass);
                        Receptionist targetReceptionist = guestList.get(counter);
                        targetReceptionist.setPassword(newpassword);
                        Database.saveData();
                    }
                    else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername=input.nextLine();
                        System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                        UserType Usercategory= UserType.valueOf(input.nextLine());
                        input.nextLine();
                        ResetPassword(AUsername,Usercategory);
                    }}
                if(TheUserType==UserType.GUEST) {
                    boolean found = false;
                    List<Guest> guestList = Database.getGuests();
                    for (int i = 0; i < Database.getGuests().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter = i;
                            break;
                        }
                    }
                    if (found) {
                        System.out.println("Please enter new password");
                        newpassword = input.nextLine();
                        System.out.println("confirm password");
                        String confirmpass = input.nextLine();
                        passwordconfirmation(newpassword, confirmpass);
                        Guest targetGuest = guestList.get(counter);
                        targetGuest.setPassword(newpassword);
                        Database.saveData();
                    } else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername = input.nextLine();
                        System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                        UserType Usercategory = UserType.valueOf(input.nextLine());
                        input.nextLine();
                        ResetPassword(AUsername, Usercategory);
                    }
                }
    }
}

