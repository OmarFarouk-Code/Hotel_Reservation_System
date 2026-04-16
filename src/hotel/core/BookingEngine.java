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
    }