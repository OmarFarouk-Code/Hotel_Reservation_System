package hotel.model.users;
import hotel.model.enums.UserType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class User implements Serializable  
{
    private String UserName;
    private String password;
    private LocalDate dateOfbirth;
    private String address;
    private String phoneNumber;
    private UserType Typeofuser=null;

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
        }
    }









}
