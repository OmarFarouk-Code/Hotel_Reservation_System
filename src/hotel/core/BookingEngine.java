package hotel.core;

import hotel.core.Database;
import hotel.interfaces.*;
import hotel.model.*;
import hotel.model.bookings.PromoCode;
import hotel.model.entities.Room;
import hotel.model.entities.RoomType;
import hotel.model.enums.RoomView;
import hotel.model.staff.Receptionist;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingEngine {

    Database database;

    public List<Room> filterRooms(String roomType, RoomView roomView, double maxPrice) {
        if (roomView == null) {
            System.out.println("Search Blocked: RoomView is required.");
            return new ArrayList<>();
        }

        ArrayList<Room> results = new ArrayList<>();
        List<Room> rooms = Database.getRooms();

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            RoomType type = room.getRoomType();

            if (type.getTypeName().equalsIgnoreCase(roomType)
                    && type.getRoomView().equals(roomView)
                    && type.getPricePerNight() <= maxPrice) {
                results.add(room);
            }
        }

        return results;
    }

    public double validatePromoCode(String code) {
        List<PromoCode> promos = Database.getPromoCodes();

        for (int i = 0; i < promos.size(); i++) {
            if (promos.get(i).getCode().equals(code)) {
                if (promos.get(i).isActive()) {
                    return 1 - promos.get(i).getDiscountPercentage();
                } else {
                    System.out.println("Promo code is expired.");
                    return 1;
                }
            }
        }

        System.out.println("Promo code not found.");
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

        if (Database.rooms != null) {
            for (Room room : Database.rooms) {
                if (room.isAvailable) {
                    available.add(room);
                }
            }
        }

        return available;
    }

    public void viewAllRooms() {
        System.out.println("--- All Hotel Rooms ---");

        if (Database.rooms == null || Database.rooms.isEmpty()) {
            System.out.println("No rooms are currently in the system.");
            return;
        }

        for (Room room : Database.rooms) {
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

    public List<String> suggestPackages() {
        List<String> packages = new ArrayList<>();
        packages.add("Breakfast Only - Start your day right!");
        packages.add("Half Board - Breakfast and Dinner included.");
        packages.add("Full Board - All three meals covered.");
        packages.add("All Inclusive - Ultimate dining experience.");
        return packages;
    }

    public double calculateRoomCost(Room room, LocalDate checkIn, LocalDate checkOut) {
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

        return nights * pricePerNight;
    }
}