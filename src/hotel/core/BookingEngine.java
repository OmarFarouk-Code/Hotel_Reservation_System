package hotel.core;

import hotel.core.Database;
import hotel.interfaces.*;
import hotel.model.*;
import hotel.model.bookings.PromoCode;
import hotel.model.bookings.Reservation;
import hotel.model.entities.Room;
import hotel.model.entities.RoomType;
import hotel.model.enums.DiningPackage;
import hotel.model.enums.RoomView;
import hotel.model.staff.Receptionist;
import hotel.model.users.Guest;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
            List<RoomType>roomtype=Database.getRoomTypes();
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
        public double validatePromocode(String code) {
            List<PromoCode> promos = Database.getPromoCodes();
            for (int i = 0; i < Database.getPromoCodes().size(); i++) {
                if (promos.get(i).getCode().equals(code)) {
                    if (promos.get(i).isActive()) {
                        return 1 - (promos.get(i).getDiscountPercentage());

                    } else {
                        System.out.println("Promo code is expired");
                        return 1;

                    }
                }
            }

                System.out.println("Promo code is not found");
                return 1;


        }

        public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
            
            if (checkIn == null || checkOut == null) {
                throw new IllegalArgumentException("Dates cannot be null.");
            }
            if (checkIn.isAfter(checkOut)) {
                throw new IllegalArgumentException("Check-in date cannot be after check-out date.");
            }

            
            List<Room> available = new ArrayList<>();
            if (Database.getRooms() != null) {
                for (Room room : Database.getRooms()) {
                    boolean isBooked = false;
                    
                    
                    if (Database.getReservations() != null) {
                        for (Reservation res : Database.getReservations()) {
        
                            if (res.getRoom().getRoomNumber() == room.getRoomNumber()) {
                                
                                if (!(checkOut.isBefore(res.getCheckinDate()) || checkIn.isAfter(res.getCheckoutDate()))) {
                                    isBooked = true;
                                    break;
                                }
                            }
                        }
                    }
                    
            
                    if (!isBooked) {
                        available.add(room);
                    }
                }
            }
            return available;
        }

        //NOT COMPLETE
    public void viewAllRooms() {
        System.out.println("--- All Hotel Rooms ---");

        if (Database.getRooms() == null || Database.getRooms().isEmpty()) {
            System.out.println("No rooms are currently in the system.");
            return;
        }

        for (Room room : Database.getRooms()) {
            System.out.println("Room " + room.getRoomNumber()
                    + " | Type: " + room.getRoomType().getTypeName()
                    + " | Available: " + room.isAvailable);
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
        
        if (view != null) {
            switch (view) {
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



    public double calculateDiningCost(DiningPackage packageType, int nights) 
    {
        if (packageType == null || nights <= 0) {
            return 0.0; 
        }

        double packagePricePerNight = 0.0;

    
        switch (packageType) {
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

    //NOT COMPLETE - MUST PRINT PRICES FOR PACKAGES SUGGESTED TOO
    public List<String> suggestPackages(Guest guest) 
    {
        List<String> suggestions = new ArrayList<>();

        
        if (guest == null || guest.getRoompreferences() == null || guest.getRoompreferences().isEmpty()) 
        {
            suggestions.add("Special Offer: Stay in our hotel for 3 nights with a standard BREAKFAST_ONLY package.");
            return suggestions;
        }
 
        boolean lovesFood = false;
        boolean wantsRelaxation = false;

        for (String pref : guest.getRoompreferences()) {
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

    
    public Reservation createDraftReservation( Guest guest , Room room , LocalDate checkIn , LocalDate checkOut , DiningPackage diningPackage, int numChildren, int numAdults , Receptionist receptionist ) throws IllegalArgumentException
    {
        if (guest == null || room == null || checkIn == null || checkOut == null || diningPackage == null || receptionist == null || numChildren < 0 || numAdults < 0) {
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
        receptionist.addDraftReservation(reservation); 
        
        return reservation;
       
    }











}