package hotel.core;
import hotel.model.entities.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*; // Import this for File IO

public class Database 
{
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
    private static final String FILE_NAME = "hotel_data.dat";

    // Call this whenever you change something (Create, Update, Delete)
    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(rooms);
            oos.writeObject(amenities);
            // Add other lists here
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Call this ONCE when your program first starts
    public static void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // Nothing to load yet

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            rooms = (List<Room>) ois.readObject();
            amenities = (List<Amenity>) ois.readObject();
            // Load other lists in the SAME ORDER you saved them
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    
