package hotel.model.staff;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
public class  Admin extends Staff
{
    private List<Room> roomList = new ArrayList<>();


    Room createRoom(Room room)
    {
        roomList.add(room);
        return room;
    }
}
