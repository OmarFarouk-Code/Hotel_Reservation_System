package hotel.core;

import hotel.model.entities.*;
import hotel.model.enums.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import hotel.model.bookings.*;
import java.time.LocalDate;
import java.util.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static BookingEngine engine = new BookingEngine();

    public static void main(String[] args) {
        Database.loadData();
        showMainMenu();
    }

    // --- 1. MAIN MENU ---
    public static void showMainMenu() {
        while (true) {
            System.out.println("\n========== AIN SHAMS HOTEL SYSTEM ==========");
            System.out.println("1. Admin Portal      2. Receptionist Portal");
            System.out.println("3. Guest Portal      4. Global Occupancy Stats");
            System.out.println("5. Save and Exit");
            System.out.print("Selection: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> showAdminMenu(new Admin()); // Uses default Admin instance
                case "2" -> showReceptionistMenu(new Receptionist("Staff", "123", LocalDate.now(), "Cairo", "010", 8));
                case "3" -> showGuestMenu(new Guest());
                case "4" -> System.out.println("Current Occupancy: " + engine.calculateOccupancyPercentage() + "%");
                case "5" -> { Database.saveData(); System.exit(0); }
                default -> System.out.println("Invalid selection.");
            }
        }
    }

    // --- 2. ADMIN MENU (Utilizes all Manageable & Staff methods) ---
    private static void showAdminMenu(Admin admin) {
        while (true) {
            System.out.println("\n--- ADMIN MANAGEMENT PANEL ---");
            System.out.println("1. Create/Read/Update/Delete Room");
            System.out.println("2. Create/Read/Update/Delete Room Type");
            System.out.println("3. Create/Read/Update/Delete Amenity");
            System.out.println("4. Set Seasonal Multiplier    5. Financial Report");
            System.out.println("6. View All Guests            7. View All Reservations");
            System.out.println("8. Logout");
            System.out.print("Action: ");

            try {
                String choice = sc.nextLine();
                switch (choice) {
                    case "1" -> { // Room CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoom(new Room(101, 1, new RoomType()));
                        if (op.equals("2")) admin.readRoom(101);
                        if (op.equals("3")) admin.updateRoom(101, new Room());
                        if (op.equals("4")) admin.deleteRoom(101);
                    }
                    case "2" -> { // RoomType CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoomType(new RoomType("Suite", 200, RoomView.SEA_VIEW, "Lux", 1.0, 150));
                        if (op.equals("2")) admin.readRoomType("Suite");
                        if (op.equals("3")) admin.updateRoomType("Suite", new RoomType());
                        if (op.equals("4")) admin.deleteRoomType("Suite");
                    }
                    case "3" -> { // Amenity CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createAmenity(new Amenity("WiFi", "High Speed", 50));
                        if (op.equals("2")) admin.readAmenity("WiFi");
                        if (op.equals("3")) admin.updateAmenity("WiFi", new Amenity());
                        if (op.equals("4")) admin.deleteAmenity("WiFi");
                    }
                    case "4" -> {
                        System.out.print("Type: "); String t = sc.nextLine();
                        System.out.print("Multiplier: "); double m = Double.parseDouble(sc.nextLine());
                        admin.setSeasonalMultiplier(t, m);
                    }
                    case "5" -> {
                        System.out.print("Report days: ");
                        admin.generateFinancialReport(Integer.parseInt(sc.nextLine()));
                    }
                    case "6" -> admin.viewAllGuests();
                    case "7" -> admin.viewAllReservations();
                    case "8" -> { return; }
                }
            } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        }
    }

    //  3. RECEPTIONIST MENU (Utilizes check-in/out & draft logic)
    private static void showReceptionistMenu(Receptionist rec) {
        while (true) {
            System.out.println("\n--- RECEPTIONIST FRONT DESK ---");
            System.out.println("1. Manage Check-In       2. Manage Check-Out");
            System.out.println("3. View Drafts           4. View Guests");
            System.out.println("5. View Reservations     6. Logout");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> { System.out.print("ID: "); rec.manageCheckIn(Integer.parseInt(sc.nextLine())); }
                case "2" -> { System.out.print("ID: "); rec.manageCheckOut(Integer.parseInt(sc.nextLine())); }
                case "3" -> rec.getDraftReservations().forEach(System.out::println);
                case "4" -> rec.viewAllGuests();
                case "5" -> rec.viewAllReservations();
                case "6" -> { return; }
            }
        }
    }

    //  4. GUEST MENU (Utilizes BookingEngine & Account logic)
    private static void showGuestMenu(Guest guest) {
        while (true) {
            System.out.println("\n--- GUEST PORTAL ---");
            System.out.println("1. Guest Homepage (Registration)");
            System.out.println("2. Search & Book Room (Engine)");
            System.out.println("3. View My Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Reset Password");
            System.out.println("6. Logout");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> guest.GuestHomepage();
                case "2" -> {
                    System.out.print("Type: "); String t = sc.nextLine();
                    List<Room> rs = engine.filterRooms(t, RoomView.SEA_VIEW, 10000);
                    if(!rs.isEmpty()) {
                        System.out.println("Booking first available...");
                        engine.createDraftReservation(guest, rs.get(0), LocalDate.now(), LocalDate.now().plusDays(1), DiningPackage.ALL_INCLUSIVE, 0, 2);
                        System.out.print("Promo Code? "); String code = sc.nextLine();
                        engine.validatePromocode(code);
                    }
                }
                case "3" -> { System.out.print("Name: "); String n = sc.nextLine(); System.out.print("ID: "); int id = Integer.parseInt(sc.nextLine()); guest.ViewReservation(n, id); }
                case "4" -> { System.out.print("ID: "); engine.processCancellation(Integer.parseInt(sc.nextLine()), LocalDate.now()); }
                case "5" -> guest.ResetPassword("User", UserType.GUEST);
                case "6" -> { return; }
            }
        }
    }
}

