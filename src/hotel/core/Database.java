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
    public static List<Invoices> getInvoices() { return invoices; }
    public static List<RoomType> getRoomTypes() { return roomTypes; }
    public static List<Amenity> getAmenities() { return amenities; }
    private static final String FILE_NAME = "hotel_data.dat";

    // Call this whenever you change something (Create, Update, Delete)
    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            // Create an array or a custom object to hold EVERYTHING
            Object[] allData = {guests, rooms, reservations, invoices, roomTypes, amenities};
            oos.writeObject(allData);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Call this ONCE when your program first starts
    @SuppressWarnings("unchecked")
    public static void loadData() {
        File file = new File(FILE_NAME);

        // If file doesn't exist or is empty, we keep the empty lists initialized above
        if (!file.exists() || file.length() == 0) {
            System.out.println("No data found, starting with fresh lists.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();

            if (obj instanceof Object[]) {
                Object[] allData = (Object[]) obj;

                // Restore in the EXACT order they were saved in saveData()
                guests = (List<Guest>) allData[0];
                rooms = (List<Room>) allData[1];
                reservations = (List<Reservation>) allData[2];
                invoices = (List<Invoices>) allData[3];
                roomTypes = (List<RoomType>) allData[4];
                amenities = (List<Amenity>) allData[5];

                System.out.println("Data loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("Load failed. Data might be corrupted. Starting fresh.");
            e.printStackTrace();
        }
    }
}



