package hotel.model.users;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
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
    protected static int GuestId=100;
    protected int UniqueId;
    private List<String> roomprefrences;//has a problem
    private RoomType roomoptions;
    private int failedLoginAttempts;
    private AccountStatus accountStatus;
    public Guest(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber , double balance , List<String> roomprefrences ) {
        super(userName, password, dateOfbirth, address , phoneNumber);
        this.balance = balance;
        this.roomprefrences = roomprefrences;
    }

    public int getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
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









    public void registerextention() {
            System.out.println("Please enter your Balance");
            balance = input.nextDouble();
            while (balance < 0) {
                System.out.println("Invalid Balance, Try again!");
                balance = input.nextDouble();
            }
            UniqueId=GuestId+1;
            GuestId=GuestId+1;
            System.out.println("Your ID: "+UniqueId);
            System.out.println("Account has been successfully created Welcome "+UserName);


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





