import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import hotel.model.users.*;
import hotel.model.entities.*;
import hotel.model.enums.*;

public class Reservation
{
    private int reservationID;
    private Guest guest;
    private Room room;
    private LocalDate CheckinDate;
    private LocalDate CheckoutDate;
    private ReservationStatus status;
    private DiningPackage diningpackage;
    private List<Amenity> selectedAmenities;

    public Reservation(int id, Guest guest, Room room,LocalDate in , LocalDate out) {
        this.reservationID = id;
        this.guest = guest;
        this.room = room;
        this.CheckinDate = in;
        this.CheckoutDate = out;
        this.status = ReservationStatus.PENDING;
        this.selectedAmenities = new ArrayList<Amenity>();
    }

    public void confirmreservation() { status = ReservationStatus.CONFIRMED; }
    public void cancelreservation() { status = ReservationStatus.CANCELLED; }
    public void completereservation() { status = ReservationStatus.COMPLETED;}

    public int calcnights() {
        return (int) ChronoUnit.DAYS.between(CheckinDate , CheckoutDate);

    }

    public double calcamenitytotal() 
    {

        double total = 0;
        //lesa mesh aaref/fahem aamel eh hena
        return total;
    }

    public void updatediningpack(DiningPackage diningpackage)
    {
        this.diningpackage = diningpackage;
    }

}