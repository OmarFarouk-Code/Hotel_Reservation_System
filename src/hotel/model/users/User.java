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
    private LocalDate dateOfbirth;
    private String address;
    private String phoneNumber;
    private UserType Typeofuser=null;
    private Gender theGender;
    protected String newpassword;
    public User() {}

    public User(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber) {
        this.UserName = userName;
        this.password = password;
        this.dateOfbirth = dateOfbirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

     public UserType getTypeofuser() {
        return Typeofuser;
    }

    Scanner input =new Scanner(System.in);

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

    public LocalDate getDateOfbirth() {
        return dateOfbirth;
    }

    public void setDateOfbirth(LocalDate dateOfbirth) {
        this.dateOfbirth = dateOfbirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public static boolean Datechecker(String dateStr) {
        try {
            LocalDate.parse(dateStr); // Default YYYY-MM-DD
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void registerextention()
    {

    }

    public boolean passwordcheck(String Password) {
        boolean uppercase = false;
        boolean number = false;
        boolean both = false;
        for (char x : password.toCharArray()) {
            if (Character.isUpperCase(x)) uppercase = true;//check that has capital letter
            if (Character.isDigit(x)) number = true;//check that there ia a number

            if (uppercase && number) {
                both = true;
                break;
            }
        }
        if (both) {
            if (password.length() >= 8) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
    public void register() {
        System.out.println("Please choose whether you are Guest,Staff");
       Typeofuser = UserType.valueOf(input.next().toUpperCase());//handling errors
        input.nextLine();//avoid skipping input
        System.out.println("Please enter a username ");
        UserName = input.nextLine();
        System.out.println("Please enter a password");
        System.out.println("** Password rules **");
        System.out.println("Password must be more than 8 characters");
        System.out.println("Password must at least contain 1 number and 1 capital letter");
        password = input.nextLine();
        while(!passwordcheck(password)){
            System.out.println("You didn't Satisfy password rules,try again");
            password = input.nextLine();
            passwordcheck(password);
        }
        System.out.println("Password has been created successfully");
        System.out.println("Please enter your Gender");
        theGender=Gender.valueOf(input.next().toUpperCase());
        System.out.println("Please enter your date of birth YYYY-MM-DD");
        String userInput = input.nextLine();
        if(!Datechecker(userInput)){
            System.out.println("Invalid date format, please re-enter it (YYYY-MM-DD)");
            userInput = input.nextLine();
        }
        dateOfbirth = LocalDate.parse(userInput);
        input.nextLine();//used to leave line
        System.out.println("Please enter your address");
        address=input.nextLine();

        if (Typeofuser == UserType.GUEST) {
            registerextention();
            Database.saveData();
        }
        Database.saveData();
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





















