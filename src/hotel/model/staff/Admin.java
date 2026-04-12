package hotel.model.staff;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
import hotel.model.users.*;
public class  Admin extends Staff
{
    private List<Room> roomList = new ArrayList<>();
    private List<Amenity> amenityList = new ArrayList<>();
   // ArrayList<Room> roomList = Database.getReservations();



    //room functions
    public void createRoom(Room room)
    {
        roomList.add(room);
    }

    Room readRoom(int roomNumber)
    {
        for (int i=0;i<roomList.size();i++)
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
    public void deleteRoom(int roomNumber)
    {
        for (int i=0;i<roomList.size();i++)
        {
            if (roomList.get(i).getRoomNumber()==roomNumber)
            {
                roomList.remove(i);
                System.out.println("Room " + roomNumber + " deleted successfully.");
                return;
            }
        }
        System.out.println("Room not found");
    }




    //Amenity Functions
    public void createAmenity(Amenity amenity)
    {
        amenityList.add(amenity);
    }

    public Amenity readAmenity(String name)
    {
        for (int i=0;i<amenityList.size();i++)
        {
            Amenity amenity=amenityList.get(i);
            if(amenity.getAmenityName().equals(name))
            {
                return amenityList.get(i);
            }
        }
        System.out.println("Amenity not found");
        return null;
    }
    public void updateAmenity(String name,Amenity updatedAmenity)
    {
        for (int i=0;i<amenityList.size();i++)
        {
            if (amenityList.get(i).getAmenityName().equals(name))
            {
              amenityList.set(i,updatedAmenity);
              System.out.println("Amenity " + name + " updated successfully.");
              return;
            }
        }
        System.out.println("Amenity not found");
    }
    public void deleteAmenity(String name)
    {
        for (int i=0;i< amenityList.size();i++)
        {
            if (amenityList.get(i).getAmenityName().equals(name))
            {
                amenityList.remove(i);
                System.out.println("Amenity" + name+ " deleted successfully.");
                return;
            }
        }
        System.out.println("Amenity not found");
    }
}


