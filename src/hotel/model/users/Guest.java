package hotel.model.users;
import hotel.core.Database;
import hotel.model.enums.*;
import hotel.model.enums.UserType;
import hotel.model.entities.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Guest extends User implements Serializable {

    private double balance;
    private RoomType roomoptions;
    private int failedLoginAttempts;
    private AccountStatus accountStatus;

    public Guest(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber , double balance , List<String> roomprefrences ) {
        super(userName, password, dateOfbirth, address , phoneNumber);
        this.balance = balance;
    }

    public Guest getGuest()
    {
       return this;
    }
    public String getUserName() {
        return UserName;
    };
    public String getpassword() {
        return password;
    };
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public RoomType getRoomoptions() {
        return roomoptions;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setRoomoptions(RoomType roomoptions) {
        this.roomoptions = roomoptions;
    }


    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    Scanner input =new Scanner(System.in);

    public void registerextention() {
            System.out.println("Please enter your Balance");
            balance = input.nextDouble();
            while (balance < 0) {
                System.out.println("Invalid Balance, Try again!");
                balance = input.nextDouble();
            }
            input.nextLine();//used to leave line
            List<String> roomperefrences = new ArrayList<String>();
            System.out.println("Account has been successfully created Welcome "+UserName);
        Database.saveData();


        }

    }






