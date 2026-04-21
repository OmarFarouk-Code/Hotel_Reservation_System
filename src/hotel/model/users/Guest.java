package hotel.model.users;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.enums.*;
import hotel.model.entities.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
public class Guest extends User
{
    private double balance;
    protected static int GuestId=100;
    protected int UniqueId;

    public Guest(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, double balance, int uniqueId, Scanner input) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address);
        this.balance = balance;
        UniqueId = uniqueId;
        this.input = input;
    }

    public Guest() {

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

    public void GuestHomepage() {
        int choice = 0;
        System.out.println("Welcome to the Guest menu");
        while (choice != 2 && choice != 1 && choice != 3) {
            System.out.println("Please enter number: 1.Regiester | 2.Login");
            choice = input.nextInt();
            input.nextLine();
            if (choice == 1) {
                register();
            }
            if(choice==2) {
                Login();
            }
            }
        }

        public void GuestMainMenu(){
        System.out.println("Please choose what do you need to do ");
        //to be continued

        }








    public boolean passwordcheck(String Password) {
        boolean uppercase = false;
        boolean number = false;
        boolean both = false;
        for (char x : Password.toCharArray()) {
            if (Character.isUpperCase(x)) uppercase = true;//check that has capital letter
            if (Character.isDigit(x)) number = true;//check that there ia a number

            if (uppercase && number) {
                both = true;
                break;
            }
        }
        if (both) {
            if (Password.length() >= 8) {
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





