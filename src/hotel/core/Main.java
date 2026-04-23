package hotel.core;

import hotel.model.bookings.Reservation;
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
        
        // Auto-seed dummy data if the database is completely empty
        if (Database.getAdmins().isEmpty() && Database.getReceptionists().isEmpty()) {
            System.out.println("[SYSTEM] Database is empty. Seeding default hotel data...");
            Database.initializeHotelData();
        }

        try {
            showMainMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    System.out.println("\n--- ADMIN LOGIN ---");
                    Admin tempAdmin = new Admin();
                    User loggedInAdmin = tempAdmin.Login(UserType.ADMIN);
                    if (loggedInAdmin != null) showAdminMenu((Admin) loggedInAdmin);
                    break;
                    
                case "2":
                    System.out.println("\n--- RECEPTIONIST LOGIN ---");
                    Receptionist tempRec = new Receptionist();
                    User loggedInRec = tempRec.Login(UserType.RECEPTIONIST);
                    if (loggedInRec != null) {
                        try {
                            showReceptionistMenu((Receptionist) loggedInRec);
                        } catch (Exception e) {
                            System.out.println("Menu Error: " + e.getMessage());
                        }
                    }
                    break;
                    
                case "3":
                    System.out.println("\n--- GUEST PORTAL ---");
                    System.out.println("1. Login   2. Register");
                    System.out.print("Selection: ");
                    String gChoice = sc.nextLine();
                    Guest tempGuest = new Guest();
                    
                    if (gChoice.equals("1")) {
                        User loggedInGuest = tempGuest.Login(UserType.GUEST);
                        if (loggedInGuest != null) {
                            ((Guest) loggedInGuest).setInput(sc); // Bind main scanner to guest
                            showGuestMenu((Guest) loggedInGuest);
                        }
                    } else if (gChoice.equals("2")) {
                        tempGuest.setInput(sc);
                        try {
                            tempGuest.register();
                            System.out.println("Registration complete! You can now log in.");
                        } catch (Exception e) {
                            System.out.println("Registration error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid selection.");
                    }
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
                    case "1": { 
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoom(new Room(101, 1, new RoomType()));
                        if (op.equals("2")) admin.readRoom(101);
                        if (op.equals("3")) admin.updateRoom(101, new Room());
                        if (op.equals("4")) admin.deleteRoom(101);
                        break;
                    }
                    case "2": { 
                        System.out.print("1.Create 2.Read 3.Update 4.Delete: ");
                        String op = sc.nextLine();
                        if (op.equals("1")) admin.createRoomType(new RoomType("Suite", 200, RoomView.SEA_VIEW, "Lux", 1.0, 150, 5));
                        if (op.equals("2")) admin.readRoomType("Suite");
                        if (op.equals("3")) admin.updateRoomType("Suite", new RoomType());
                        if (op.equals("4")) admin.deleteRoomType("Suite");
                        break;
                    }
                    case "3": { 
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
                    case "6": admin.viewAllGuests(); break;
                    case "7": admin.viewAllReservations(); break;
                    case "8": System.out.println("Logging out..."); return;
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
                case "1": System.out.print("ID: "); rec.manageCheckIn(Integer.parseInt(sc.nextLine())); break;
                case "2": System.out.print("ID: "); rec.manageCheckOut(Integer.parseInt(sc.nextLine())); break;
                case "3": rec.getDraftReservations().forEach(System.out::println); break;
                case "4": rec.viewAllGuests(); break;
                case "5": rec.viewAllReservations(); break;
                case "6": System.out.println("Logging out..."); return;
            }
        }
    }

    // --- 4. GUEST MENU ---
    private static void showGuestMenu(Guest guest) {
        while (true) {
            System.out.println("\n--- GUEST PORTAL ---");
            System.out.println("Logged in as: " + guest.getUserName()+"ID : "+guest.getUniqueId());
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
                    // 1. Automatically fetch reservations for THIS guest
                    List<Reservation> myActiveBookings = engine.getReservationsForGuest(guest);

                    if (myActiveBookings.isEmpty()) {
                        System.out.println("You have no active reservations to cancel.");
                    } else {
                        System.out.println("\n--- Your Active Bookings ---");
                        for (int i = 0; i < myActiveBookings.size(); i++) {
                            Reservation r = myActiveBookings.get(i);
                            System.out.println((i + 1) + ". Room " + r.getRoom().getRoomNumber() +
                                    " [" + r.getCheckinDate() + " to " + r.getCheckoutDate() + "]");
                        }

                        System.out.print("Enter the number of the booking to cancel (or 0 to exit): ");
                        try {
                            int choice1 = Integer.parseInt(sc.nextLine());
                            if (choice1 > 0 && choice1 <= myActiveBookings.size()) {
                                // 2. Pass the ID internally without the user ever typing it
                                Reservation selected = myActiveBookings.get(choice1 - 1);
                                engine.processCancellation(selected.getReservationID(), LocalDate.now());
                                System.out.println("Cancellation request submitted successfully.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                    break;
                }
                case "4": guest.ResetPassword(guest.getUserName(), UserType.GUEST); break;
                case "5": System.out.println("Logging out..."); return;
            }
        }
    }
}