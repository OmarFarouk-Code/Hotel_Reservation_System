package hotel.core;
import hotel.model.bookings.PromoCode;
import hotel.model.entities.*;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.DiningPackage;
import hotel.model.enums.Gender;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.RoomView;
import hotel.model.enums.UserType;
import hotel.model.staff.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*; // Import this for File IO
import java.time.LocalDate;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.users.Guest;

public class Database
{
    private static List<Guest> guests = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<Invoice> invoices = new ArrayList<>();
    private static List<RoomType> roomTypes = new ArrayList<>();
    private static List<Amenity> amenities = new ArrayList<>();
    private static List<Admin> admins=new ArrayList<>();
    private static List<Receptionist> receptionists=new ArrayList<>();
    private static List<PromoCode> promoCodes = new ArrayList<>();

    public static List<PromoCode> getPromoCodes() {
        return promoCodes;
    }

    public static void setPromoCodes(List<PromoCode> promoCodes) {
        Database.promoCodes = promoCodes;
    }


    private Database() {}

    // --- Basic Getters ---
    public static List<Guest> getGuests() { return guests; }
    public static List<Room> getRooms() { return rooms; }
    public static List<Reservation> getReservations() { return reservations; }
    public static List<Invoice> getInvoices() { return invoices; }
    public static List<RoomType> getRoomTypes() { return roomTypes; }
    public static List<Amenity> getAmenities() { return amenities; }
    public static List<Admin> getAdmins(){return admins;}
    public static List<Receptionist> getReceptionists(){return receptionists;}
    private static final String FILE_NAME = "hotel_data.dat";

    // Call this whenever you change something (Create, Update, Delete)
    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            // Create an array or a custom object to hold EVERYTHING
            Object[] allData = {guests, rooms, reservations, invoices, roomTypes, amenities,admins,receptionists,promoCodes};
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
                invoices = (List<Invoice>) allData[3];
                roomTypes = (List<RoomType>) allData[4];
                amenities = (List<Amenity>) allData[5];
                admins = (List<Admin>) allData[6];
                receptionists = (List<Receptionist>) allData[7];
                promoCodes=(List<PromoCode>) allData[8];

                System.out.println("Data loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("Load failed. Data might be corrupted. Starting fresh.");
            e.printStackTrace();
        }
    }

        public static void initializeHotelData() {
        System.out.println("[SEEDER] Initializing massive Egyptian dummy data...");
        Random rand = new Random();

        // 1. Clear existing lists to prevent duplicates on multiple runs
        Database.getAmenities().clear();
        Database.getRoomTypes().clear();
        Database.getRooms().clear();
        Database.getGuests().clear();
        Database.getAdmins().clear();
        Database.getReceptionists().clear();
        Database.getPromoCodes().clear();
        Database.getReservations().clear();
        Database.getInvoices().clear();

        // 2. Generate Amenities
        Amenity airportTransfer = new Amenity("Airport Shuttle", "Direct transfer to Cairo International Airport (CAI)", 800.0);
        Amenity snorkeling = new Amenity("Snorkeling Gear", "Full day rental for Red Sea diving", 350.0);
        Amenity nileCruise = new Amenity("Felucca Ride", "1-hour traditional sailboat ride on the Nile", 450.0);
        Amenity spa = new Amenity("Pharaonic Spa", "Full body massage and Cleopatra milk bath", 1200.0);
        Amenity gym = new Amenity("Gym Access", "24/7 access to the fitness center", 150.0);
        Amenity wifi = new Amenity("Premium WiFi", "High-speed internet access", 100.0);
        
        Database.getAmenities().add(airportTransfer);
        Database.getAmenities().add(snorkeling);
        Database.getAmenities().add(nileCruise);
        Database.getAmenities().add(spa);
        Database.getAmenities().add(gym);
        Database.getAmenities().add(wifi);

        // 3. Generate Room Types
        RoomType standardGarden = new RoomType("Standard Oasis", 2000.0, RoomView.GARDEN, "Comfortable room overlooking the lush hotel gardens.", 1.0, 2000.0);
        standardGarden.setMaxCapacity(2);
        
        RoomType standardPool = new RoomType("Poolside Classic", 2500.0, RoomView.POOL, "Direct access to the central swimming pool.", 1.0, 2500.0);
        standardPool.setMaxCapacity(2);
        
        RoomType seaViewDeluxe = new RoomType("Red Sea Deluxe", 4000.0, RoomView.SEA_VIEW, "Spacious room with a panoramic view of the Red Sea.", 1.2, 3333.33);
        seaViewDeluxe.setMaxCapacity(3);
        
        RoomType nileSuite = new RoomType("Pharaoh Suite", 8500.0, RoomView.SEA_VIEW, "Luxury suite featuring premium furnishings and a wide balcony.", 1.5, 5666.67);
        nileSuite.setMaxCapacity(4);

        Database.getRoomTypes().add(standardGarden);
        Database.getRoomTypes().add(standardPool);
        Database.getRoomTypes().add(seaViewDeluxe);
        Database.getRoomTypes().add(nileSuite);

        // 4. Generate Massive Rooms (100 Rooms across 5 Floors)
        RoomType[] types = {standardGarden, standardPool, seaViewDeluxe, nileSuite};
        for (int floor = 1; floor <= 5; floor++) {
            for (int r = 1; r <= 20; r++) {
                int roomNumber = (floor * 100) + r;
                // Distribute room types: Mostly standard, some deluxe, few suites
                RoomType type;
                if (r <= 10) type = types[0];
                else if (r <= 15) type = types[1];
                else if (r <= 18) type = types[2];
                else type = types[3];

                Room room = new Room(roomNumber, floor, type);
                
                // Add random base amenities
                room.addAmenity(wifi);
                if (type.getTypeName().contains("Suite") || type.getTypeName().contains("Deluxe")) {
                    room.addAmenity(gym);
                }
                Database.getRooms().add(room);
            }
        }

        // 5. Generate Promo Codes
        Database.getPromoCodes().add(new PromoCode("EGYPT2026", 0.15, LocalDate.now().plusMonths(6)));
        Database.getPromoCodes().add(new PromoCode("ASU_STUDENT", 0.20, LocalDate.now().plusMonths(12)));
        Database.getPromoCodes().add(new PromoCode("NILE_BREEZE", 0.10, LocalDate.now().plusMonths(1)));

        // 6. Generate Massive Guests
        String[] maleNames = {"Ahmed", "Mohamed", "Mahmoud", "Youssef", "Kareem", "Tarek", "Mostafa", "Sharif", "Hassan", "Amr"};
        String[] femaleNames = {"Fatma", "Aya", "Nada", "Salma", "Hla", "Mariam", "Nour", "Mona", "Heba", "Laila"};
        String[] lastNames = {"Hassan", "Ali", "Ibrahim", "Fawzy", "El-Sayed", "Tawfik", "Abdelrahman", "Mansour", "Gaber", "Aly"};
        String[] cities = {"Nasr City, Cairo", "New Cairo, Cairo", "Smouha, Alexandria", "Zamalek, Cairo", "Maadi, Cairo", "El Gouna, Red Sea", "Hurghada", "Luxor City", "Aswan"};

        int guestIdCounter = 1000;
        for (int i = 0; i < 50; i++) { // Generate 50 dummy guests
            boolean isMale = rand.nextBoolean();
            String fName = isMale ? maleNames[rand.nextInt(maleNames.length)] : femaleNames[rand.nextInt(femaleNames.length)];
            String lName = lastNames[rand.nextInt(lastNames.length)];
            String username = (fName + "_" + lName + i).toLowerCase();
            String phone = "+201" + (rand.nextInt(3)) + String.format("%08d", rand.nextInt(100000000));
            String city = cities[rand.nextInt(cities.length)];
            LocalDate dob = LocalDate.of(1970 + rand.nextInt(40), 1 + rand.nextInt(12), 1 + rand.nextInt(28));
            
            Guest guest = new Guest(username, "Pass@1234", UserType.GUEST, isMale ? Gender.MALE : Gender.FEMALE, 
                    null, 0, AccountStatus.ACTIVE, dob, phone, city, 
                    10000.0 + (rand.nextDouble() * 50000), // Random balance between 10k and 60k
                    guestIdCounter++, null);
            
            // Adding a specific profile for testing
            if (i == 0) {
                guest.setUserName("prof_economics");
                guest.setAddress("Heliopolis, Cairo");
            }
            Database.getGuests().add(guest);
        }

        // 7. Generate Staff
        Admin admin1 = new Admin("admin_root", "Admin@2026", UserType.ADMIN, Gender.MALE, null, 0, AccountStatus.ACTIVE, 
                LocalDate.of(1985, 4, 12), "+201011111111", "New Cairo, Egypt", 8, Database.getRooms());
        Database.getAdmins().add(admin1);

        Receptionist rec1 = new Receptionist("frontdesk_aya", "Desk@2026", UserType.RECEPTIONIST, Gender.FEMALE, null, 0, AccountStatus.ACTIVE, LocalDate.of(1998, 9, 21), "+201222222222", "Nasr City, Cairo", 8);
        Database.getReceptionists().add(rec1);

        // 8. Generate Active/Past Reservations & Invoices
        BookingEngine engine = new BookingEngine();
        int resId = 1;
        
        for (int i = 0; i < 15; i++) { // Generate 15 reservations
            Guest guest = Database.getGuests().get(rand.nextInt(Database.getGuests().size()));
            Room room = Database.getRooms().get(rand.nextInt(Database.getRooms().size()));
            
            // Random dates spanning from last month to next month
            LocalDate checkIn = LocalDate.now().plusDays(rand.nextInt(30) - 15);
            LocalDate checkOut = checkIn.plusDays(1 + rand.nextInt(7));
            
            DiningPackage[] dPackages = DiningPackage.values();
            DiningPackage dp = dPackages[rand.nextInt(dPackages.length)];
            
            Reservation res = new Reservation(resId++, guest, room, checkIn, checkOut, dp, rand.nextInt(2), 1 + rand.nextInt(2));
            
            // Add some amenities randomly
            if (rand.nextBoolean()) res.getSelectedAmenities().add(airportTransfer);
            if (rand.nextBoolean()) res.getSelectedAmenities().add(spa);

            // Determine status based on dates
            if (checkOut.isBefore(LocalDate.now())) {
                res.setStatus(ReservationStatus.COMPLETED);
            } else if (checkIn.isBefore(LocalDate.now()) && checkOut.isAfter(LocalDate.now())) {
                res.setStatus(ReservationStatus.CONFIRMED);
            } else {
                res.setStatus(rand.nextBoolean() ? ReservationStatus.CONFIRMED : ReservationStatus.PENDING);
            }
            
            Database.getReservations().add(res);

            // Generate an Invoice for confirmed/completed reservations
            if (res.getStatus() == ReservationStatus.COMPLETED || res.getStatus() == ReservationStatus.CONFIRMED) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(Database.getInvoices().size() + 1);
                inv.setReservation(res);
                inv.setPaymentMethod(PaymentMethod.CREDIT_CARD);
                inv.setPaymentDate(checkIn.minusDays(1)); // Paid the day before check-in
                inv.setPaid(true);
                
                // Use BookingEngine to calculate real totals for the dummy data
                double rCost = engine.calculateRoomCost(room, checkIn, checkOut);
                double dCost = engine.calculateDiningCost(dp, res.calcnights());
                double aCost = engine.calculateAmenityCost(res.getSelectedAmenities());
                
                inv.setTotalAmount(rCost + dCost + aCost);
                inv.setAppliedPromoCode("NONE");
                inv.setDiscountAmount(0.0);
                
                Database.getInvoices().add(inv);
            }
        }

        // Save everything to the .dat file
        Database.saveData();
        System.out.println("[SEEDER] Initialization complete! " + Database.getRooms().size() + " rooms and " + Database.getGuests().size() + " guests loaded.");
    }


}



