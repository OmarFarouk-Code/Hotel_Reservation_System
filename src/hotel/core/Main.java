package hotel.core;

import hotel.model.entities.*;
import hotel.model.enums.*;
import hotel.model.staff.*;
import hotel.model.users.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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
                case "1":
                    handleAdminLogin();
                    break;
                case "2":
                    handleReceptionistLogin();
                    break;
                case "3":
                    handleGuestPortal();
                    break;
                case "4":
                    System.out.println("Current Occupancy: " + engine.calculateOccupancyPercentage() + "%");
                    break;
                case "5":
                    Database.saveData();
                    System.out.println("Data saved successfully. Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
    }

    // --- AUTHENTICATION FLOWS ---

    private static void handleAdminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        for (Admin admin : Database.getAdmins()) {
            if (admin.getUserName().equals(username) && admin.getPassword().equals(password)) {
                if (admin.getAccountStatus() == AccountStatus.ACTIVE) {
                    System.out.println("Login Successful! Welcome, " + admin.getUserName() + ".");
                    showAdminMenu(admin);
                    return;
                } else {
                    System.out.println("Access Denied: Account is locked.");
                    return;
                }
            }
        }
        System.out.println("Invalid credentials.");
    }

    private static void handleReceptionistLogin() {
        System.out.println("\n--- RECEPTIONIST LOGIN ---");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        for (Receptionist rec : Database.getReceptionists()) {
            if (rec.getUserName().equals(username) && rec.getPassword().equals(password)) {
                if (rec.getAccountStatus() == AccountStatus.ACTIVE) {
                    System.out.println("Login Successful! Welcome, " + rec.getUserName() + ".");
                    try {
                        showReceptionistMenu(rec);
                    } catch (Exception e) {
                        System.out.println("Error loading menu: " + e.getMessage());
                    }
                    return;
                } else {
                    System.out.println("Access Denied: Account is locked.");
                    return;
                }
            }
        }
        System.out.println("Invalid credentials.");
    }

    private static void handleGuestPortal() {
        System.out.println("\n--- GUEST PORTAL ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Back to Main Menu");
        System.out.print("Selection: ");

        String choice = sc.nextLine();
        if (choice.equals("1")) {
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            for (Guest guest : Database.getGuests()) {
                if (guest.getUserName() != null && guest.getUserName().equals(username) && guest.getPassword().equals(password)) {
                    if (guest.getAccountStatus() == AccountStatus.ACTIVE) {
                        System.out.println("Login Successful! Welcome, " + guest.getUserName() + ".");
                        guest.setInput(sc); // Ensure input scanner is bound for guest methods
                        showGuestMenu(guest);
                        return;
                    } else {
                        System.out.println("Access Denied: Account is locked.");
                        return;
                    }
                }
            }
            System.out.println("Invalid credentials.");
        } else if (choice.equals("2")) {
            Guest newGuest = new Guest();
            newGuest.setInput(sc);
            try {
                newGuest.register();
                System.out.println("Registration complete! Please log in from the Guest Portal.");
            } catch (Exception e) {
                System.out.println("Registration error: " + e.getMessage());
            }
        }
    }


    // --- 2. ADMIN MENU ---
    private static void showAdminMenu(Admin admin) {
        while (true) {
            System.out.println("\n--- ADMIN MANAGEMENT PANEL ---");
            System.out.println("Logged in as: " + admin.getUserName());
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
                    case "1": { // Room CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoom(new Room(101, 1, new RoomType()));
                        if (op.equals("2")) admin.readRoom(101);
                        if (op.equals("3")) admin.updateRoom(101, new Room());
                        if (op.equals("4")) admin.deleteRoom(101);
                        break;
                    }
                    case "2": { // RoomType CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoomType(new RoomType("Suite", 200, RoomView.SEA_VIEW, "Lux", 1.0, 150, 5));
                        if (op.equals("2")) admin.readRoomType("Suite");
                        if (op.equals("3")) admin.updateRoomType("Suite", new RoomType());
                        if (op.equals("4")) admin.deleteRoomType("Suite");
                        break;
                    }
                    case "3": { // Amenity CRUD
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createAmenity(new Amenity("WiFi", "High Speed", 50));
                        if (op.equals("2")) admin.readAmenity("WiFi");
                        if (op.equals("3")) admin.updateAmenity("WiFi", new Amenity());
                        if (op.equals("4")) admin.deleteAmenity("WiFi");
                        break;
                    }
                    case "4": {
                        System.out.print("Type: "); String t = sc.nextLine();
                        System.out.print("Multiplier: "); double m = Double.parseDouble(sc.nextLine());
                        admin.setSeasonalMultiplier(t, m);
                        break;
                    }
                    case "5": {
                        System.out.print("Report days: ");
                        admin.generateFinancialReport(Integer.parseInt(sc.nextLine()));
                        break;
                    }
                    case "6": {
                        admin.viewAllGuests();
                        break;
                    }
                    case "7": {
                        admin.viewAllReservations();
                        break;
                    }
                    case "8": {
                        System.out.println("Logging out Admin...");
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // --- 3. RECEPTIONIST MENU ---
    private static void showReceptionistMenu(Receptionist rec) throws Exception {
        while (true) {
            System.out.println("\n--- RECEPTIONIST FRONT DESK ---");
            System.out.println("Logged in as: " + rec.getUserName());
            System.out.println("1. Manage Check-In       2. Manage Check-Out");
            System.out.println("3. View Drafts           4. View Guests");
            System.out.println("5. View Reservations     6. Logout");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    System.out.print("ID: "); rec.manageCheckIn(Integer.parseInt(sc.nextLine()));
                    break;
                }
                case "2": {
                    System.out.print("ID: "); rec.manageCheckOut(Integer.parseInt(sc.nextLine()));
                    break;
                }
                case "3": {
                    rec.getDraftReservations().forEach(System.out::println);
                    break;
                }
                case "4": {
                    rec.viewAllGuests();
                    break;
                }
                case "5": {
                    rec.viewAllReservations();
                    break;
                }
                case "6": {
                    System.out.println("Logging out Receptionist...");
                    return;
                }
            }
        }
    }

    // --- 4. GUEST MENU ---
    private static void showGuestMenu(Guest guest) {
        while (true) {
            System.out.println("\n--- GUEST PORTAL ---");
            System.out.println("Logged in as: " + guest.getUserName());
            System.out.println("1. Search & Book Room (Engine)");
            System.out.println("2. View My Reservations");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. Reset Password");
            System.out.println("5. Logout");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    System.out.print("Type: "); String t = sc.nextLine();
                    List<Room> rs = engine.filterRooms(t, RoomView.SEA_VIEW, 10000);
                    if (!rs.isEmpty()) {
                        System.out.println("Booking first available...");
                        engine.createDraftReservation(guest, rs.get(0), LocalDate.now(), LocalDate.now().plusDays(1), DiningPackage.ALL_INCLUSIVE, 0, 2);
                        System.out.print("Promo Code? "); String code = sc.nextLine();
                        engine.validatePromocode(code);
                    }
                    break;
                }
                case "2": {
                    System.out.print("Confirm Name: "); String n = sc.nextLine();
                    System.out.print("Confirm Unique ID: "); int id = Integer.parseInt(sc.nextLine());
                    guest.ViewReservation(n, id);
                    break;
                }
                case "3": {
                    System.out.print("Reservation ID: "); engine.processCancellation(Integer.parseInt(sc.nextLine()), LocalDate.now());
                    break;
                }
                case "4": {
                    guest.ResetPassword(guest.getUserName(), UserType.GUEST);
                    break;
                }
                case "5": {
                    System.out.println("Logging out Guest...");
                    return;
                }
            }
        }
    }
}

