package hotel.model.staff;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.Review;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.UserType;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.entities.Room;

public class Receptionist extends Staff {
    private List<Reservation> draftReservations;

    public Receptionist() {
        this.draftReservations = new ArrayList<>();
    }

    public Receptionist(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, int workingHours ) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address, workingHours);
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
        for (int i = 0; i < reservation.size(); i++) {
            if (reservation.get(i).getReservationID() == (reservationID)) {
                reservation.get(i).confirmreservation();
                Database.saveData();
                System.out.println("Reservation is confirmed");
                return;
            }
        }
        System.out.println("Reservation ID not found");

    }

    public void manageCheckOut(int reservationID , Review review) throws Exception {
        List<Invoice> invoices = Database.getInvoices();
        Invoice targetInvoice= null;
        for (Invoice inv : invoices) {
            if (inv.getReservation()!=null&&inv.getReservation().getReservationID() == reservationID ) {
                targetInvoice = inv;
                break;
            }
        }
        if (review != null) {

        }
        if (targetInvoice == null) {
            throw new Exception("Checkout Failed: No invoice found for ID " + reservationID);
        }
        else if(targetInvoice.isPaid()==false)
        {
            throw new Exception("Checkout Denied: Invoice must be settled before checkout.");
        }

            targetInvoice.getReservation().setStatus(ReservationStatus.COMPLETED);
            targetInvoice.getReservation().getRoom().addReview(review);
            System.out.println("Check-out complete..... ");

            Database.saveData();

            //should make room available


    }
}