package hotel.model.users;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.enums.*;
import hotel.model.entities.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Guest extends User implements Serializable
{
    private double balance;
    protected static int GuestId=100;
    protected int UniqueId;
    private List<String> roomprefrences;//has a problem
    private RoomType roomoptions;
    private int failedLoginAttempts;
    private AccountStatus accountStatus;
    private LocalDate dateOfbirth;
    private String address;
    private String phoneNumber;
    public Guest(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber , double balance , List<String> roomprefrences ) {
        super(userName, password, dateOfbirth, address , phoneNumber);
        this.balance = balance;
        this.roomprefrences = roomprefrences;
    }

    public int getUniqueId() {
        return UniqueId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
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

    public List<String> getRoompreferences() {
        return roomprefrences;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setRoompreferences(List<String> roomperefrences) {
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

    private transient Scanner input = new Scanner(System.in);
    public void printreservationdetails(int idcounter,List<Reservation> Reservations){
        System.out.println("Your Reservation");
        System.out.print("Guest Username : "+ Reservations.get(idcounter).getGuest().getUserName());System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.print("Room Type : "+Reservations.get(idcounter).getRoom().getRoomType());System.out.println();
        System.out.println("Room floor : "+Reservations.get(idcounter).getRoom().getFloor());System.out.println();
        System.out.print("Room Number : "+Reservations.get(idcounter).getRoom().getRoomNumber());System.out.println();System.out.println("-----------------------------------------------");
        System.out.println("Check in :"+Reservations.get(idcounter).getCheckinDate());System.out.println("                  ");
        System.out.print("Check out : "+Reservations.get(idcounter).getCheckoutDate());System.out.println();System.out.println("-----------------------------------------------");
        System.out.println("Reservation Status: "+Reservations.get(idcounter).getStatus());System.out.println();
        System.out.print("Dining Package : "+Reservations.get(idcounter).getDiningpackage());System.out.println("-----------------------------------------------");
        System.out.println("*Room Amenities*");System.out.println();System.out.println("-----------------------------------------------");
        System.out.println(Reservations.get(idcounter).getSelectedAmenities());

    }
    public void ViewReservationbyId() {
        System.out.println("Can't find your reservation? Enter your ReservationID");
        boolean found = false;
        while (!found) {
            System.out.println("Enter Reservation ID (or 0 to cancel):");
            int id = input.nextInt();
            input.nextLine();
            if (id == 0) return;

            List<Reservation> reservations = Database.getReservations();
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).getReservationID() == id) {
                    printreservationdetails(i, reservations);
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("ID not found.");
        }

        }



        public void register() {
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
            System.out.println("Please enter your phone number");
            phoneNumber=input.nextLine();
            System.out.println("Please enter your address");
            address=input.nextLine();
            Database.getGuests().add(this);
            Database.saveData();

        }



        public void ViewReservation(String UserName, int UserId) {
            List<Reservation> allReservations = Database.getReservations();
            boolean foundAny = false;
            for (int i = 0; i < allReservations.size(); i++) {
                Reservation res = allReservations.get(i);
                // Check if both Username and ID match
                if (res.getGuest().getUserName().equals(UserName) && res.getGuest().getUniqueId() == UserId) {
                    printreservationdetails(i, allReservations);
                    foundAny = true;
                }
            }

            if (!foundAny) {
                System.out.println("No reservations found for this Guest.");
                ViewReservationbyId();
            }
        }
}





