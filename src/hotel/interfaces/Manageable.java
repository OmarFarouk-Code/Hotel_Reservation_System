package hotel.interfaces;
import hotel.model.entities.*;
//Interface defining management operations for Rooms, Amenities, and Room Types.

public interface Manageable 
{
    // --- Room Management ---
    public void createRoom( Room room );
    public Room readRoom( int roomNumber );
    public void updateRoom( int roomNumber , Room updatedRoom );
    public void deleteRoom( int roomNumber);
    // --- Amenity Management ---
    public Amenity readAmenity(String name);
    public void createAmenity( Amenity amenity);
    public void updateAmenity(String name , Amenity updatedAmenity);
    public void deleteAmenity(String name);
    // --- RoomType Management ---
    public void createRoomType( RoomType type);
    public RoomType readRoomType(String typeName);
    public void updateRoomType(String typeName , RoomType updatedType);
    public void deleteRoomType(String typeName);
}