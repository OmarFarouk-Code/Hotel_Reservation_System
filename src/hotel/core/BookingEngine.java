package hotel.core;
import hotel.model.entities.Amenity;
import hotel.model.entities.Room;
import hotel.model.entities.RoomType;
import hotel.model.enums.DiningPackage;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.RoomView;
import hotel.model.staff.Receptionist;
import hotel.model.users.Guest;
import hotel.model.bookings.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookingEngine 
{
    Database database;

    public List<Room> filterRooms(String roomType, RoomView roomview,double maxPrice)
    {
        if (roomview == null) {
            System.out.println("Search Blocked: RoomView is required.");
            return new ArrayList<>(); // Return an empty list so the GUI doesn't crash
        }
        ArrayList<Room> results=new ArrayList<>();
        List<Room> Rooms=Database.getRooms();

        for (int i=0;i<Rooms.size();i++)
        {
            Room room=Rooms.get(i);
            RoomType type=room.getRoomType();
            if(type.getTypeName().equalsIgnoreCase(roomType)&&type.getRoomView().equals(roomview)&&type.getPricePerNight()<=maxPrice)
            {
                results.add(room);
            }
        }
        return results;
    }

    public double validatePromocode(String code) 
    {
        List<PromoCode> promos = Database.getPromoCodes();

        for (int i = 0; i < Database.getPromoCodes().size(); i++) 
        {
            if (promos.get(i).getCode().equals(code)) 
            {
                if (promos.get(i).isActive()) 
                {
                    return 1 - (promos.get(i).getDiscountPercentage());
                } 
                else 
                {
                    System.out.println("Promo code is expired");
                    return 1;
                }
            }
        }
        System.out.println("Promo code is not found");
        return 1; 
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) 
    {    
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (checkIn.isAfter(checkOut)) 
        {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date.");
        }  
        List<Room> available = new ArrayList<>();

        if (Database.getRooms() != null) 
        {
            for (Room room : Database.getRooms()) 
            {
                boolean isBooked = false;
                if (Database.getReservations() != null) 
                {
                    for (Reservation res : Database.getReservations()) 
                    {
                        if (res.getRoom().getRoomNumber() == room.getRoomNumber()) 
                        {    
                            if (!(checkOut.isBefore(res.getCheckinDate()) || checkOut.isEqual(res.getCheckinDate()) || 
                                checkIn.isAfter(res.getCheckoutDate()) || checkIn.isEqual(res.getCheckoutDate()))) 
                            {
                                isBooked = true;
                                break;
                            }
                        }
                    }
                }
                if (!isBooked) 
                {
                    available.add(room);
                }
            }
        }
        return available;
    }

    public void viewAllRooms() 
    {
        System.out.println("--- All Hotel Rooms ---");

        if (Database.getRooms() == null || Database.getRooms().isEmpty())
        {
            System.out.println("No rooms are currently in the system.");
            return;
        }

        //Define the dates to check current availability (Today to Tomorrow)
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        //Get the list of rooms available for these dates
        List<Room> availableRoomsList = getAvailableRooms(today, tomorrow);

        for (Room room : Database.getRooms()) {
            // 3. Check if the current room is inside the available list
            boolean isCurrentlyAvailable = availableRoomsList.contains(room);

            System.out.println("Room " + room.getRoomNumber()
                    + " | Type: " + room.getRoomType().getTypeName()
                    + " | Available: " + (isCurrentlyAvailable ? "Yes" : "No")); 
        }
    }

    public List<Room> sortRooms(List<Room> rooms, boolean ascending) {
        if (rooms == null || rooms.isEmpty()) {
            return new ArrayList<>();
        }

        rooms.sort((r1, r2) -> {
            double price1 = r1.getRoomType().getEffectivePrice();
            double price2 = r2.getRoomType().getEffectivePrice();

            if (ascending) {
                return Double.compare(price1, price2);
            } else {
                return Double.compare(price2, price1);
            }
        });

        return rooms;
    }


    public List<String> viewAllDiningPackages() 
    {
        List<String> packages = new ArrayList<>();
        packages.add("Breakfast Only - Start your day right!");
        packages.add("Half Board - Breakfast and Dinner included.");
        packages.add("Full Board - All three meals covered.");
        packages.add("All Inclusive - Ultimate dining experience.");
        return packages;
    }


    public double calculateRoomCost(Room room, LocalDate checkIn, LocalDate checkOut) 
    {
        
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in.");
        }
    
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double pricePerNight = room.getRoomType().getEffectivePrice();
        
        RoomView view = room.getRoomType().getRoomView(); 
        double viewSurcharge = 0.0;
        
        if (view != null) 
        {
            switch (view) 
            {
                case SEA_VIEW:
                    viewSurcharge = 1000; 
                    break;
                case POOL:
                    viewSurcharge = 500; 
                    break;
                case GARDEN:
                    viewSurcharge = 0; 
                    break;
            }
        }
        
        return nights * (pricePerNight + viewSurcharge);
    }

    //NOT COMPLETE - MUST PRINT PRICES FOR PACKAGES SUGGESTED TOO
    public List<String> suggestPackages(Guest guest) 
    {
        List<String> suggestions = new ArrayList<>();

        
        if (guest == null || guest.getRoomPreferences() == null || guest.getRoomPreferences().isEmpty()) 
        {
            suggestions.add("Special Offer: Stay in our hotel for 3 nights with a standard BREAKFAST_ONLY package.");
            return suggestions;
        }
 
        boolean lovesFood = false;
        boolean wantsRelaxation = false;

        for (String pref : guest.getRoomPreferences()) {
            String lowerPref = pref.toLowerCase(); 
            
            
            if (lowerPref.contains("food") || lowerPref.contains("dining") || lowerPref.contains("eat")) {
                lovesFood = true;
            }
            
            if (lowerPref.contains("relax") || lowerPref.contains("sea") || lowerPref.contains("quiet")) {
                wantsRelaxation = true;
            }
        }

    
        if (lovesFood && wantsRelaxation) {
            suggestions.add("Perfect Match: Stay in our hotel for 5 nights in a SEA_VIEW room with an ALL_INCLUSIVE dining package!");
        } else if (lovesFood) {
            suggestions.add("Perfect Match: Stay in our hotel for 3 nights and enjoy unlimited meals with a FULL_BOARD dining package.");
        } else if (wantsRelaxation) {
            suggestions.add("Perfect Match: Stay in our hotel for 4 nights to relax with a HALF_BOARD dining package.");
        } else {
            suggestions.add("Perfect Match: Stay in our hotel for 3 nights with our BREAKFAST_ONLY package.");
        }

        return suggestions;
    }

    
    public Reservation createDraftReservation( Guest guest , Room room , LocalDate checkIn , LocalDate checkOut , DiningPackage diningPackage, int numChildren, int numAdults ) throws IllegalArgumentException
    {
        List <Receptionist> allReceptionists = Database.getReceptionists();

        if (allReceptionists == null || allReceptionists.isEmpty()) 
        {
            throw new IllegalStateException("No receptionists available to handle the reservation.");
        }
        Random random = new Random();
        int randomIndex = random.nextInt( allReceptionists.size() );
        Receptionist allocatedReceptionist = allReceptionists.get(randomIndex);

        if (guest == null || room == null || checkIn == null || checkOut == null || diningPackage == null || numChildren < 0 || numAdults < 0) {
            throw new IllegalArgumentException("All reservation details must be provided.");
        }
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in.");
        }

         if (!getAvailableRooms(checkIn, checkOut).contains(room)) {
            throw new IllegalArgumentException("Selected room is not available for the given dates.");
        }

        if ( (numChildren + numAdults) > room.getRoomType().getMaxCapacity()) {
            throw new IllegalArgumentException("Number of guests exceeds room capacity.");
        }

        int reservationID = Database.getReservations().size() + 1;
        Reservation reservation = new Reservation(reservationID, guest, room, checkIn, checkOut, diningPackage, numChildren, numAdults);
        Database.getReservations().add(reservation);
        Database.saveData();
        allocatedReceptionist.addDraftReservation(reservation); 
        
        return reservation;
       
    }


    public double calculateDiningCost(DiningPackage packageType, int nights) 
    {
        if (packageType == null || nights <= 0) 
        {
            return 0.0; 
        }

        double packagePricePerNight = 0.0;

    
        switch (packageType) 
        {
            case BREAKFAST_ONLY:
                packagePricePerNight = 0;
                break;
            case HALF_BOARD:
                packagePricePerNight = 300;
                break;
            case FULL_BOARD:
                packagePricePerNight = 500;
                break;
            case ALL_INCLUSIVE:
                packagePricePerNight = 700;
                break;
        }

        return packagePricePerNight * nights;
    }

    public boolean confirmReservation( int reservationID , PaymentMethod paymentMethod)
    {
        List<Reservation> reservations = Database.getReservations();
        for (Reservation res : reservations)
        {
            if (res.getReservationID() == reservationID)
            {
                if (paymentMethod == null )
                {
                    System.out.println("Payment method is required to confirm reservation.");
                    return false;
                }
                if (res.getStatus() != ReservationStatus.PENDING)
                {
                    System.out.println("Only pending reservations can be confirmed.");
                    return false;
                }
                res.confirmreservation();
                Database.saveData();
                System.out.println("Reservation " + reservationID + " has been confirmed with payment method: " + paymentMethod);
                return true;
            }
        }
        return false;
    }

    public Invoice generateInvoice(Reservation reservation , String promoCode)
    {

        Invoice invoice = new Invoice();
        invoice.setInvoiceID(Database.getInvoices().size() + 1);
        invoice.setReservation(reservation);
        invoice.setPaid(false);

        double roomCost = calculateRoomCost(reservation.getRoom(), reservation.getCheckinDate(), reservation.getCheckoutDate());
        double diningCost = calculateDiningCost(reservation.getDiningpackage(), (int)ChronoUnit.DAYS.between(reservation.getCheckinDate(), reservation.getCheckoutDate()));
        double amenityCost = calculateAmenityCost( reservation.getSelectedAmenities());
        
        double subtotal = roomCost + diningCost + amenityCost;
        double discountMultiplier = validatePromocode(promoCode); 
        double totalCost = subtotal * discountMultiplier;

        invoice.setTotalAmount(totalCost);
        invoice.setPaymentDate(LocalDate.now());
        invoice.setAppliedPromoCode(promoCode);
        invoice.setDiscountAmount((roomCost + diningCost + amenityCost) - totalCost);
        return invoice;
    }    

    public double calculateCancellationPenalty(int ReservationId,LocalDate cancelDate)
    {
        boolean found=false;
        List<Reservation> Reservations = Database.getReservations();
        List<Invoice>Invoices=Database.getInvoices();

        for(int i=0;i<Database.getReservations().size();i++) 
        {
            if (Reservations.get(i).getReservationID() == ReservationId)
            {
                found = true;
                long daysBetween = ChronoUnit.DAYS.between(cancelDate, Reservations.get(i).getCheckinDate());
                if (daysBetween >= 0 && daysBetween <= 3) 
                {
                    for (int j = 0; j < Database.getInvoices().size(); j++) 
                    {
                        if (Invoices.get(j).getReservation().getReservationID() == ReservationId) 
                        {
                            System.out.println("Unfortunately,30% of the total reservation amount will be deducted as cancellation fees due to late cancellation");
                            System.out.print("cancellation penalty : - "+(Invoices.get(j).getTotalAmount() * 0.30));
                            System.out.println("Refunded amount = "+(Invoices.get(j).getTotalAmount()*0.70));
                            return (Invoices.get(j).getTotalAmount() * 0.30);
                        }
                    }
                }
            }
        }

        if(found) 
        {
            System.out.println("There will be no cancellation penalty");
            System.out.println("Cancellation penalty : 0 ");
            return 0.0;
        }
        else 
        {
            System.out.println("This reservation Id is not found in the data base");
            return -1.0;
        }
    }


    public double calculateAmenityCost(List<Amenity> selectedAmenities)
    {
        double total=0.0;
        if (selectedAmenities==null||selectedAmenities.isEmpty())
        {
            return  total;
        }
        for (Amenity a :selectedAmenities)
        {
            total+=a.getAmenityPrice();
        }
        return total;
    }

    public double calculateTotalRevenue() 
    {
        double total = 0.0;
        // No parameters means we check EVERYTHING in the database
        for (Invoice inv : Database.getInvoices()) {
            if (inv.isPaid()) {
                total += inv.getTotalAmount();
            }
        }
        return total;
    }

    public double calcualteTotalRevenue(LocalDate startDate,LocalDate endDate) throws Exception
    {
        if(endDate.isBefore(startDate))
        {
            throw new Exception("Invalid Range: End date cannot be before Start date");
        }

        List <Invoice> invoices=Database.getInvoices();
        double revenue=0;
        for(Invoice i:invoices)
        {
            if (i.isPaid()) {
                LocalDate paymentDate=i.getPaymentDate();
                if(paymentDate.isEqual(startDate)|| paymentDate.isEqual(endDate)||(paymentDate.isBefore(endDate)&& paymentDate.isAfter(startDate)))
                {
                    revenue+=i.getTotalAmount();
                }
            }
        }
        return revenue;
    }

    public double calculateTotalReservationCost(Reservation reservation) {
        if (reservation == null || reservation.getRoom() == null) {
            System.err.println("Error: Invalid reservation data for cost calculation.");
            return 0.0;
        }
        // We use the dates directly from the reservation object
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                reservation.getCheckinDate(),
                reservation.getCheckoutDate()
        );

        // Standard hotel policy: a stay of 0 days (same-day checkout) is charged as 1 night
        if (nights <= 0) {
            nights = 1;
        }


        double roomPricePerNight = reservation.getRoom().getRoomType().getPricePerNight();
        double totalRoomCost = roomPricePerNight * nights;


        double diningCostPerNight = 0;
        if (reservation.getDiningpackage() != null) {
            switch (reservation.getDiningpackage()) {
                case BREAKFAST_ONLY : {
                    diningCostPerNight = 15.0;
                    break;
                }
                case HALF_BOARD     : {
                    diningCostPerNight = 35.0;
                    break;
                }
                case FULL_BOARD     : {
                    diningCostPerNight = 60.0;
                    break;
                }
                case ALL_INCLUSIVE  : {
                    diningCostPerNight = 100.0;
                    break;
                }
                default             : {
                    diningCostPerNight = 0.0;
                    break;
                }
            }
        }
        double totalDiningCost = diningCostPerNight * nights;


        double totalAmenitiesCost = 0;
        if (reservation.getSelectedAmenities() != null) {
            for (hotel.model.entities.Amenity amenity : reservation.getSelectedAmenities()) {
                totalAmenitiesCost += amenity.getAmenityPrice();
            }
        }


        double grandTotal = totalRoomCost + totalDiningCost + totalAmenitiesCost;


        return grandTotal;
    }

    public double calculateOccupancyPercentage() {
        java.util.List<hotel.model.entities.Room> allRooms = hotel.core.Database.getRooms();
        java.util.List<Reservation> allReservations = hotel.core.Database.getReservations();

        if (allRooms == null || allRooms.isEmpty()) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();

        java.util.Set<Integer> occupiedRoomNumbers = new java.util.HashSet<>();

        for (Reservation res : allReservations) {
            boolean isConfirmed = (res.getStatus() == ReservationStatus.CONFIRMED);
            boolean isCurrentlyStaying = !today.isBefore(res.getCheckinDate()) && today.isBefore(res.getCheckoutDate());

            if (isConfirmed && isCurrentlyStaying) {
                occupiedRoomNumbers.add(res.getRoom().getRoomNumber());
            }
        }

        double totalRooms = allRooms.size();
        double occupiedCount = occupiedRoomNumbers.size();

        return (occupiedCount / totalRooms) * 100.0;
    }
    public void processCancellation(int reservationId, LocalDate cancelDate) {

        Reservation reservation = null;

        // Find reservation
        for (Reservation r : Database.getReservations()) {
            if (r.getReservationID() == reservationId) {
                reservation = r;
                break;
            }
        }

        if (reservation == null) {
            System.out.println("Reservation not found!");
            return;
        }

        // Calculate penalty
        double penalty = calculateCancellationPenalty(reservationId, cancelDate);

        reservation.setCancellationPenalty(penalty);
        reservation.getCancellationPenalty();
    }



}