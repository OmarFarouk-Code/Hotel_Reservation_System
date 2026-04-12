package Files.Classes;
import java.util.ArrayList; // This is the dynamic array class
import java.util.List;
public class Room
{
    private int roomNumber;
    private int floor;
    private RoomType roomType;
    private List<Amenity> amenities;

    public Room() 
    {
        amenities = new ArrayList<>();
    }

    public Room( int roomNumber, int floor, RoomType roomType ) 
    {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        amenities = new ArrayList<>();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
    }

    public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);
    }

}
