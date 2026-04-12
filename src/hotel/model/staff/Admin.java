package hotel.model.staff;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
public class  Admin extends Staff
{
    private List<Room> roomList = new ArrayList<>();


    void createRoom(Room room)
    {
        roomList.add(room);
    }
    void createAmenity(Amenity amenity)
    {
        roomList.add(amenity);
    }

    Room readRoom(int roomNumber)
    {
        for (int i=0;i<room.size();i++)
        {
            Room room=roomList.get(i);
            if (room.getRoomNumber()==roomNumber)
            {
                return room;
            }
        }
        System.out.println("Room " + roomNumber + " not found.");
        return null;
    }
    public void updateRoom(int roomNumber, Room updatedRoom)
    {
        for (int i = 0; i < roomList.size(); i++)
        {
            if (roomList.get(i).getRoomNumber() == roomNumber)
            {
                roomList.set(i, updatedRoom);
                System.out.println("Room " + roomNumber + " updated successfully.");
                return;
            }
        }
        System.out.println("Update failed: Room " + roomNumber + " does not exist.");
    }
}
