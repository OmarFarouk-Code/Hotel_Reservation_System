package hotel.model.Reservations;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int reservationID;
    private Guest guest;
    private Room room;
    private LocalDate CheckinDate;
    private LocalDate CheckoutDate;
    private Reservationstatus status;
    private DiningPackage diningpackage;
    private List<Amenity> selectedAmenities = new ArrayList<>();
    public Reservation(int id, Guest guest, Room room,LocalDate in , LocalDate out) {
        this.reservationID = id;
        this.guest = guest;
        this.room = room;
        this.CheckinDate = in;
        this.CheckoutDate = out;
        this.status = ReservationStatus.PENDING;

    }
    public void confirmreservation() { status = Reservationstatus.CONFIRMED; }
    public void cancelreservation() { status = Reservationstatus.CANCELLED; }
    public void completereservation() { status = Reservationstatus.COMPLETE;}

    public int calcnights() {
        return (int) ChronoUnit.DAYS.between(CheckinDate , CheckoutDate);

    }
    public double calcamenitytotal() {
        double total = 0;
        lesa mesh aaref/fahem aamel eh hena
        return total;
    }
    public void updatediningpack(DinigPackage dp){this.diningpackage = dp;}

}
}
