package Files.Classes;
import java.util.ArrayList;
import java.util.List;
public class  Admin extends Staff
{
    private List<Room> roomList = new ArrayList<>();


    Room createRoom(Room room)
    {
        roomList.add(room);
        return room;
    }
}
