    package hotel.core;

    import hotel.model.*;
    import hotel.interfaces.*;
    import hotel.core.Database;
    import hotel.model.entities.Room;
    import hotel.model.entities.RoomType;
    import hotel.model.enums.RoomView;

    import java.util.ArrayList;
    import java.util.List;

    public  class BookingEngine
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

    }