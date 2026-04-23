package hotel.core;

import hotel.model.entities.*;
import hotel.model.enums.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import hotel.model.bookings.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                                System.out.print("Enter Type Name: ");
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
                            else if (op.equals("3"))
                            {
                                System.out.print("Enter Room Type Name to update: ");
                                String typeName = sc.nextLine();

                                // To update, we first read the existing one, then modify it
                                RoomType existing = admin.readRoomType(typeName);
                                if(existing!=null)
                                {
                                    System.out.print("Enter New Base Price (was " + existing.getBasePrice() + "): ");
                                    existing.setBasePrice(Double.parseDouble(sc.nextLine()));
                                    System.out.print("Enter New Room View (was " + existing.getRoomView() + "): ");
                                    existing.setRoomView(RoomView.valueOf(sc.nextLine()));
                                    System.out.print("Enter New Room Description (was " + existing.getDescription() + "): ");
                                    existing.setDescription(sc.nextLine());
                                    System.out.print("Enter New Room's Max Capacity (was " + existing.getMaxCapacity() + "): ");
                                    existing.setMaxCapacity(Integer.parseInt(sc.nextLine()));
                                    admin.updateRoomType(typeName, existing);
                                    System.out.println("Update successful.");
                                }
                                else
                                    throw new Exception("Error: Room type not found.");
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
            System.out.println("Logged in as: " + guest.getUserName()+"  ID : "+guest.getUniqueId());
            System.out.println("1. Search & Book Room (Engine)");
            System.out.println("2. View My Reservations");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View & Pay Invoices");
            System.out.println("5. Reset Password");
            System.out.println("6. Logout");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        System.out.println("\n--- Make a Reservation ---");
                        System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
                        LocalDate checkIn = LocalDate.parse(sc.nextLine(), formatter);
                        System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
                        LocalDate checkOut = LocalDate.parse(sc.nextLine(), formatter);

                       
                        List<Room> availableRooms = engine.getAvailableRooms(checkIn, checkOut);
                        if (availableRooms.isEmpty()) {
                            System.out.println("Sorry, no rooms available for those dates.");
                            break;
                        }

                       
                        System.out.println("\n--- Available Rooms ---");
                        for (int i = 0; i < availableRooms.size(); i++) {
                            Room r = availableRooms.get(i);
                            RoomType rt = r.getRoomType();
                           
                            System.out.println("- Room " + r.getRoomNumber() +
                                               " | View: " + rt.getRoomView() +
                                               " | Type: " + rt.getTypeName() +
                                               " | Price: $" + rt.getBasePrice());
                        }
                       
                        System.out.print("\nSelect a Room Number to book (or 0 to cancel): ");
                        int roomChoice = Integer.parseInt(sc.nextLine());
                        if (roomChoice == 0) break;
                       
                        Room selectedRoom = null;
                        for (Room r : availableRooms) {
                            if (r.getRoomNumber() == roomChoice) {
                                selectedRoom = r;
                                break;
                            }
                        }
                       
                        if (selectedRoom == null) {
                            System.out.println("Invalid room selection.");
                            break;
                        }

                       
                        System.out.print("Number of Adults: ");
                        int adults = Integer.parseInt(sc.nextLine());
                        System.out.print("Number of Children: ");
                        int children = Integer.parseInt(sc.nextLine());

                       
                        System.out.println("\n--- Dining Packages ---");
                        List<String> suggestions = engine.suggestPackages(guest);
                        for(String s : suggestions) {
                            System.out.println(s);
                        }
                       
                        System.out.println("Available: [BREAKFAST_ONLY, HALF_BOARD, FULL_BOARD, ALL_INCLUSIVE]");
                        System.out.print("Enter your chosen Dining Package: ");
                        DiningPackage selectedDining = DiningPackage.valueOf(sc.nextLine().toUpperCase());

                       
                        System.out.print("Enter a promo code (or press Enter to skip): ");
                        String promoCode = sc.nextLine();
                       
                       
                        Reservation draft = engine.createDraftReservation(guest, selectedRoom, checkIn, checkOut, selectedDining, children, adults);
                       
                        Invoice invoice = engine.generateInvoice(draft, promoCode);
                        System.out.println("\n--- Reservation Draft Created ---");
                        System.out.println("Draft Reservation ID: " + draft.getReservationID());
                        System.out.println("Total Amount Due: $" + invoice.getTotalAmount());

                       
                        System.out.print("Would you like to pay now from your account balance to confirm? (Y/N): ");
                        String payNow = sc.nextLine().toUpperCase();
                       
                        if (payNow.equals("Y")) {
                            if (guest.getBalance() >= invoice.getTotalAmount()) {
                               
                                guest.setBalance(guest.getBalance() - invoice.getTotalAmount());
                               
                                boolean isConfirmed = engine.confirmReservation(draft.getReservationID(), PaymentMethod.ONLINE);
                               
                                if (isConfirmed) {
                                    System.out.println("Payment successful! Your new balance is: $" + guest.getBalance());
                                }
                            } else {
                                System.out.println("Insufficient balance. Reservation remains PENDING. Please pay at check-in.");
                            }
                        } else {
                            System.out.println("Reservation saved as PENDING. Please pay at check-in.");
                        }

                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println("Error: Invalid date format. Please use yyyy-MM-dd.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Input error: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("An unexpected error occurred: " + e.getMessage());
                    }
                    break;
                }
                case "2": {

                    guest.ViewReservation(guest.getUserName(), guest.getUniqueId());
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
                case "4" : {
                    BookingEngine.viewAndPayInvoices(guest, sc);
                    break;
                }
                case "5": {
                    guest.ResetPassword(guest.getUserName(), UserType.GUEST); 
                    break;
                }
                case "6": {
                    System.out.println("Logging out..."); 
                    return;
                }
            }
        }
    }
}