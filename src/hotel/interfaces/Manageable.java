package hotel.interfaces;
import hotel.model.entities.*;
//Interface defining management operations for Rooms, Amenities, and Room Types.

public interface Manageable 
{
    // --- Room Management ---
    public void createRoom( Room room ) throws Exception;
    public Room readRoom( int roomNumber ) throws Exception;
    public void updateRoom( int roomNumber , Room updatedRoom ) throws Exception;
    public void deleteRoom( int roomNumber) throws Exception;
    // --- Amenity Management ---
    public Amenity readAmenity(String name) throws Exception ;
    public void createAmenity( Amenity amenity) throws Exception;
    public void updateAmenity(String name , Amenity updatedAmenity) throws Exception;
    public void deleteAmenity(String name) throws Exception;
    // --- RoomType Management ---
    public void createRoomType( RoomType type) throws Exception;
    public RoomType readRoomType(String typeName) throws Exception;
    public void updateRoomType(String typeName , RoomType updatedType) throws Exception;
    public void deleteRoomType(String typeName) throws Exception;
}