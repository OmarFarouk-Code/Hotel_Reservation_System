package hotel.core;
import java.util.ArrayList;
import java.util.List;

public class Database 
{
    
    private static final Random random = new Random();

    private static List<Guest> guests = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<Invoices> invoices = new ArrayList<>();
    private static List<RoomType> roomTypes = new ArrayList<>();
    private static List<Amenity> amenities = new ArrayList<>();

    private Database() {}

    // --- Basic Getters ---
    public static List<Guest> getGuests() { return guests; }
    public static List<Room> getRooms() { return rooms; }
    public static List<Reservation> getReservations() { return reservations; }
    public static List<Invoice> getInvoices() { return invoices; }
    public static List<RoomType> getRoomTypes() { return roomTypes; }
    public static List<Amenity> getAmenities() { return amenities; }
}

    
