package Files.Classes;
import java.util.ArrayList; // This is the dynamic array class
import java.util.List;
public class Room
{
int roomNumber;
int floor;
boolean isAvailable;
RoomType roomType;
private List<Amenity> books;

public Room() {
    // Initialize it in the constructor so it's not null
        this.books = new ArrayList<>();
}
}
