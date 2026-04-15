package hotel.model.users;
import hotel.model.enums.*;
import hotel.model.enums.UserType;
import hotel.model.entities.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Guest extends User implements Serializable
{

    private double balance;
    private List<String> roomprefrences;
    private RoomType roomoptions;
    private int failedLoginAttempts;
    private AccountStatus accountStatus;
    private Guest guest;
    public Guest(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber , double balance , List<String> roomprefrences ) {
        super(userName, password, dateOfbirth, address , phoneNumber);
        this.balance = balance;
        this.roomprefrences = new ArrayList<String>();
    }

    public Guest getGuest()
    {
        return this;
    }


    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public RoomType getRoomoptions() {
        return roomoptions;
    }

    public List<String> getRoomperefrences() {
        return roomprefrences;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setRoomperefrences(List<String> roomperefrences) {
        this.roomprefrences = roomperefrences;
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
            System.out.println("Account has been successfully created Welcome "+UserName);


        }

    }






