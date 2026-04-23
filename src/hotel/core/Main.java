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
                    case "1":  // --- MANAGE ROOMS ---
                        List <RoomType> roomtype=Database.getRoomTypes();
                        if (roomtype.isEmpty()) {
                            System.out.println("[No Room Types available]");
                        }
                        else
                        {
                            admin.DisplayRoomType();
                            System.out.print("Rooms: 1.Create 2.Read 3.Update 4.Delete: ");
                            String op = sc.nextLine();

                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter new Room Number: ");
                                int roomNum = Integer.parseInt(sc.nextLine()); // Use parseInt to avoid Scanner bugs!

                                System.out.print("Enter Floor Number: ");
                                int floorNum = Integer.parseInt(sc.nextLine());

                                System.out.print("Enter Room Type Name (From Available List): ");
                                String typeName = sc.nextLine();

                                // Fetch the actual RoomType from the database before assigning it
                                RoomType assignedType = admin.readRoomType(typeName);

                                admin.createRoom(new Room(roomNum, floorNum, assignedType));
                                System.out.println("Success! Room created.");
                            }
                            else if (op.equals("2")) {
                                System.out.print("Enter Room Number to read: ");
                                int roomNum = Integer.parseInt(sc.nextLine());
                                Room r = admin.readRoom(roomNum);
                                System.out.println("Found Room: " + r.getRoomNumber() + " on floor " + r.getFloor() + "\nRoomType: "+r.getRoomType().getTypeName());
                            }
                            else if (op.equals("4")) {
                                System.out.print("Enter Room Number to delete: ");
                                int roomNum = Integer.parseInt(sc.nextLine());
                                admin.deleteRoom(roomNum);
                            }
                        } catch (Exception e) {
                            // This will catch all those custom Exception messages we wrote earlier!
                            System.out.println("❌ ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "2": { // --- MANAGE ROOM TYPES ---

                        List<RoomType> list = Database.getRoomTypes();
                        if (list.isEmpty())
                        {
                            System.out.println("[No Room Types available]");
                        }
                            // Only printing the TypeName field
                        admin.DisplayRoomType();
                        System.out.print("Room Types: 1.Create 2.Read 3.Update 4.Delete: \n");
                        String op = sc.nextLine();

                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter Type Name (e.g., Suite, Single, Double): ");
                                String typeName = sc.nextLine();

                                System.out.print("Enter Base Price: ");
                                double basePrice = Double.parseDouble(sc.nextLine());

                                // Handling the Enum input
                                System.out.print("Enter Room View (SEA_VIEW, GARDEN_VIEW, CITY_VIEW): ");
                                String viewInput = sc.nextLine().toUpperCase();
                                RoomView view = RoomView.valueOf(viewInput);

                                System.out.print("Enter Description: ");
                                String description = sc.nextLine();

                                System.out.print("Enter Max Guests: ");
                                int maxGuests = Integer.parseInt(sc.nextLine());

                                // Note: Multiplier defaults to 1.0 for new types
                                admin.createRoomType(new RoomType(typeName, basePrice, view, description, 1.0, basePrice, maxGuests));
                                System.out.println("Success! Room Type '" + typeName + "' added.");
                            }
                            else if (op.equals("2")) {
                                System.out.print("Enter Room Type Name to search: ");
                                String typeName = sc.nextLine();
                                RoomType rt = admin.readRoomType(typeName);
                                System.out.println("Found: " + rt.getTypeName() + " | Price: $" + rt.getEffectivePrice() + " | View: " + rt.getRoomView());
                            }
                            else if (op.equals("3")) {
                                System.out.print("Enter Room Type Name to update: ");
                                String typeName = sc.nextLine();

                                // To update, we first read the existing one, then modify it
                                RoomType existing = admin.readRoomType(typeName);

                                System.out.print("Enter New Base Price (was " + existing.getBasePrice() + "): ");
                                existing.setBasePrice(Double.parseDouble(sc.nextLine()));

                                admin.updateRoomType(typeName, existing);
                                System.out.println("Update successful.");
                            }
                            else if (op.equals("4")) {
                                System.out.print("Enter Room Type Name to delete: ");
                                String typeName = sc.nextLine();
                                admin.deleteRoomType(typeName);
                                System.out.println("Room Type deleted.");
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("❌ ERROR: Invalid Enum value. Please use SEA_VIEW, GARDEN_VIEW, or CITY_VIEW.");
                        } catch (Exception e) {
                            System.out.println("❌ ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "3": { // --- MANAGE AMENITIES ---
                        // 1. Fetch the list immediately before printing
                        List<Amenity> currentAmenities = Database.getAmenities();
                        // 2. The Check & Loop
                        if (currentAmenities == null || currentAmenities.isEmpty()) {
                            System.out.println("[No amenities available in database]");
                        }
                        admin.DisplayAmenity();
                        System.out.print("Amenities: 1.Create 2.Read 3.Update 4.Delete:");
                        String op = sc.nextLine();
                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter Amenity Name: ");
                                String name = sc.nextLine();

                                System.out.print("Enter Description: ");
                                String desc = sc.nextLine();

                                System.out.print("Enter Price (0 if free): ");
                                double price = Double.parseDouble(sc.nextLine());

                                admin.createAmenity(new Amenity(name, desc, price));
                                System.out.println("Success! Amenity created.");
                            }
                            else if (op.equals("2"))
                            { // --- READ ---
                                System.out.print("Enter Amenity Name to search: ");
                                String name = sc.nextLine();

                                Amenity a = admin.readAmenity(name);
                                System.out.println("Found: " + a.getAmenityName() + " | Price: $" + a.getAmenityPrice() + " | Info: " + a.getDescription());
                            }
                            else if (op.equals("3"))
                            { // --- UPDATE ---
                                System.out.print("Enter Amenity Name to update: ");
                                String name = sc.nextLine();

                                // Fetch existing to show current values
                                Amenity existing = admin.readAmenity(name);

                                System.out.print("Enter New Description (was: " + existing.getDescription() + "): ");
                                existing.setDescription(sc.nextLine());

                                System.out.print("Enter New Price (was: " + existing.getAmenityPrice() + "): ");
                                existing.setAmenityPrice(Double.parseDouble(sc.nextLine()));

                                admin.updateAmenity(name, existing);
                                System.out.println("Update successful.");
                            }
                            else if (op.equals("4"))
                            {
                                System.out.print("Enter Amenity Name to delete: ");
                                String name = sc.nextLine();
                                admin.deleteAmenity(name);
                            }
                        } catch (Exception e) {
                            System.out.println("❌ ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "4": {
                        admin.DisplayRoomType();
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
                case "2": {
                    Scanner input = new Scanner(System.in);
                    System.out.print("Add a Comment: ");
                    String comment = input.nextLine();
                    System.out.print("Rating: ");
                    int rating = input.nextInt();
                    Review review = new Review(rating, comment);
                    System.out.print("ID: ");
                    rec.manageCheckOut(Integer.parseInt(sc.nextLine()), review);

                }break;
                case "3": rec.getDraftReservations().forEach(System.out::println); break;
                case "4": rec.viewAllGuests(); break;
                case "5": rec.viewAllReservations(); break;
                case "6": System.out.println("Logging out..."); return;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
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