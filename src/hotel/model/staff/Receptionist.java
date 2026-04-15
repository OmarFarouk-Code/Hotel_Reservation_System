package hotel.model.staff;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
import hotel.model.enums.ReservationStatus;
import hotel.model.users.*;
import hotel.model.entities.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.users.Guest;
public class Receptionist extends Staff
{
public void manageCheckIn(int reservationID)
{
    List<Reservation> reservation=Database.getReservations();
    List<Invoice> invoice=Database.getInvoices();
    for (int i=0;i< reservation.size();i++)
    {
        if(reservation.get(i).getReservationID()==(reservationID))
        {
            reservation.get(i).comfirmReservation();
            reservation.get(i).setStatus(ReservationStatus.CONFIRMED);
            Database.saveData();
            System.out.println("Reservation is confirmed");
            return;
        }
    }
    System.out.println("Reservation ID not found");
}

}
