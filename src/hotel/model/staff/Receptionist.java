package hotel.model.staff;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
import hotel.model.enums.Gender;
import hotel.model.enums.ReservationStatus;
import hotel.model.users.*;
import hotel.model.entities.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;

public class Receptionist extends Staff {
    private List<Reservation> draftReservations;

    public Receptionist(String name, String password, LocalDate dateOfBirth, String address, String phoneNumber, int workingHours) 
    {
        super(name, password, dateOfBirth, address, phoneNumber, workingHours);
        this.draftReservations = new ArrayList<>();
    }

    public List<Reservation> getDraftReservations() {
        return draftReservations;
    }

    public void addDraftReservation(Reservation reservation) {
        this.draftReservations.add(reservation);
    }

    public void removeDraftReservation(Reservation reservation) {
        this.draftReservations.remove(reservation);
    }


    public void manageCheckIn(int reservationID) {
        List<Reservation> reservation = Database.getReservations();
        List<Invoice> invoice = Database.getInvoices();
        for (int i = 0; i < reservation.size(); i++) {
            if (reservation.get(i).getReservationID() == (reservationID)) {
                reservation.get(i).confirmreservation();
                reservation.get(i).setStatus(ReservationStatus.CONFIRMED);
                Database.saveData();
                System.out.println("Reservation is confirmed");
                return;
            }
        }
        System.out.println("Reservation ID not found");

    }

    public void manageCheckOut(int reservationID) {
        List<Reservation> reservations = Database.getReservations();
        List<Invoice> invoices = Database.getInvoices();
        Invoice targetInvoice= null;
        for (Invoice inv : invoices) {
            if (inv.getReservation().getReservationID() == reservationID) {
                targetInvoice = inv;
                break;
            }
        }
        if (targetInvoice == null) {
            System.out.println("Reservation iD not found");
            return;
        }
        else if(targetInvoice.isPaid()==false)
        {
            System.out.println("Check-out Denied: Balance must be $0.00.");
            targetInvoice=null;
            return;
        }
        else
        {
            targetInvoice.getReservation().setStatus(ReservationStatus.COMPLETED);
            System.out.println("Check-out complete..... ");
            //should make room available
        }
        Database.saveData();
    }
}