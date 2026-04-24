package hotel.model.users;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.enums.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
public class Guest extends User
{
    private double balance;
    protected static int GuestId=100;
    protected int UniqueId;
    private List<String> roomPreferences;

    public Guest(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, double balance, int uniqueId, Scanner input) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address);
        this.balance = balance;
        this.UniqueId = uniqueId;
        this.input = input;
        this.roomPreferences = new ArrayList<>();
    }

    public Guest() {
        this.roomPreferences = new ArrayList<>();
    }

    public List<String> getRoomPreferences() {
        return roomPreferences;
    }

    public void setRoomPreferences(List<String> roomPreferences) {
        this.roomPreferences = roomPreferences;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static int getGuestId() {
        return GuestId;
    }

    public static void setGuestId(int guestId) {
        GuestId = guestId;
    }

    public int getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
    }

    public Scanner getInput() {
        return input;
    }

    public void setInput(Scanner input) {
        this.input = input;
    }

    private boolean phonecheck(String s) 
    {
        if (s == null || s.length() != 11) return false;

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void printreservationdetails(int idcounter,List<Reservation> Reservations){
        System.out.println("-----------------------------------------------");
        System.out.println("Your Reservation");
        System.out.print("Guest Username : "+ Reservations.get(idcounter).getGuest().getUserName());System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.print("Room Type : "+Reservations.get(idcounter).getRoom().getRoomType().getTypeName());System.out.println();
        System.out.println("Room floor : "+Reservations.get(idcounter).getRoom().getFloor());
        System.out.print("Room Number : "+Reservations.get(idcounter).getRoom().getRoomNumber());System.out.println();System.out.println("-----------------------------------------------");
        System.out.println("Check in :"+Reservations.get(idcounter).getCheckinDate());System.out.println("                  ");
        System.out.print("Check out : "+Reservations.get(idcounter).getCheckoutDate());System.out.println();System.out.println("-----------------------------------------------");
        System.out.println("Reservation Status: "+Reservations.get(idcounter).getStatus());System.out.println();
        System.out.println("Dining Package : "+Reservations.get(idcounter).getDiningpackage());System.out.println("-----------------------------------------------");
        System.out.println("*Room Amenities*");
        System.out.println(Reservations.get(idcounter).getRoom().getAmenities());
        System.out.println("-----------------------------------------------");

    }

    public void ViewReservationbyId() 
    {
        int limit =0;
        List<Reservation> reservations = Database.getReservations();

        while(limit<2) 
        {
            System.out.println("Can't find your reservation? Enter your ReservationID");
            if (!input.hasNextInt()) { 
                input.next();
                System.out.println("Invalid input! Please enter a numeric ID.");
                continue;
            }

            String phone;
            int id = input.nextInt();
            input.nextLine();
            if (id == 0) return;

            while (true) 
            {
                System.out.print("Enter your Phone Number : ");
                phone = input.nextLine();

                if ((phonecheck(phone))) {
                    break;
                } else {
                    System.out.println("Invalid input! Please enter (numbers only).");
                }
            }


            for (int i = 0; i < reservations.size(); i++) 
            {
                if (reservations.get(i).getReservationID() == id && reservations.get(i).getGuest().getPhoneNumber().equals(phone)) {
                    printreservationdetails(i, reservations);
                    return;
                }
            }
                limit = limit + 1;

            if (limit < 2) {
                System.out.println("Data not found. You have " + (2 - limit) + " attempts left.");
            } 
            else 
            {
                System.out.println("Too many failed attempts. Returning to main menu.");
            }
        }
    }


    public void register() 
    {
        System.out.println("Please enter a username ");
        UserName = input.nextLine().trim();

        System.out.println("Please enter a password");
        System.out.println("** Password rules **");
        System.out.println("Password must be more than 8 characters");
        System.out.println("Password must at least contain 1 number and 1 capital letter");
        password = input.nextLine();

        while(!passwordcheck(password))
        {
            System.out.println("You didn't Satisfy password rules,try again");
            password = input.nextLine().trim();
            passwordcheck(password);
        }

        System.out.println("Password has been created successfully");
        this.Typeofuser = UserType.GUEST;
        this.accountStatus = AccountStatus.ACTIVE;
        this.UniqueId = GuestId++;
        this.failedLoginAttempts = 0;

        while (true) {
            System.out.print("Please enter your Gender (Male/Female): ");
            String genderInput = input.nextLine().trim().toUpperCase();

            if (genderInput.equals("MALE") || genderInput.equals("M")) {
                this.theGender = Gender.MALE;
                break;
            } else if (genderInput.equals("FEMALE") || genderInput.equals("F")) {
                this.theGender = Gender.FEMALE;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Male' or 'Female'.");
            }
        }
        System.out.println("Please enter your date of birth YYYY-MM-DD");
        String userInput = input.nextLine().trim();
        if(!Datechecker(userInput)){
            System.out.println("Invalid date format, please re-enter it (YYYY-MM-DD)");
            userInput = input.nextLine().trim();
        }
        dateOfbirth = LocalDate.parse(userInput);
        System.out.println("Please enter your phone number");
        phoneNumber=input.nextLine().trim();
        System.out.println("Please enter your address");
        address=input.nextLine().trim();
        System.out.println("Your ID: "+getUniqueId());
        Database.getGuests().add(this);
        Database.saveData();

    }

    public void ViewReservation(String UserName, int UserId) 
    {
        List<Reservation> allReservations = Database.getReservations();
        boolean foundAny = false;
        for (int i = 0; i < allReservations.size(); i++) 
        {
            Reservation res = allReservations.get(i);
            // Check if both Username and ID match
            if (res.getGuest().getUserName().equals(UserName) && res.getGuest().getUniqueId() == UserId) {
                printreservationdetails(i, allReservations);
                foundAny = true;
            }
        }

        if (!foundAny) 
        {
            System.out.println("No reservations found for this Guest.");
            ViewReservationbyId();
        }
    }

}





