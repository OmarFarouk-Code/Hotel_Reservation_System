package hotel.model.users;
import java.util.List;
import hotel.core.Database;
import hotel.model.bookings.*;
import java.time.LocalDate;


public abstract class Staff extends User
{
    private int workingHours;

    public Staff() {};

    public Staff(String userName, String password, LocalDate dateOfbirth, String address , String phoneNumber , int workingHours) {
       super(userName, password, dateOfbirth, address , phoneNumber);  
       this.workingHours = workingHours;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public void viewAllGuests() 
    {
        List<Guest> guests = Database.getGuests();
        for (Guest guest : guests) 
        {
            System.out.println(guest.getUserName());
        }
    }

    public void viewAllReservations()
    {
        List<Reservation> reservations = Database.getReservations();
        for (Reservation reservation : reservations) 
        {
            System.out.println("Username : " + reservation.getGuest().getUserName());
            System.out.println("Reservation ID : " + reservation.getReservationID());
        }
    }
}

