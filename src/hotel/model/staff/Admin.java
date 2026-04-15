package hotel.model.staff;
import java.util.ArrayList;
import java.util.List;
import hotel.core.Database;
import hotel.model.entities.*;
import hotel.model.bookings.Invoice;
import hotel.model.users.*;
import java.time.LocalDate;
import hotel.interfaces.*;
public class  Admin extends Staff implements Manageable
{

    private List<Room> roomList;
    private List<Amenity> amenityList;
    private List<RoomType> roomTypeList;
    private List<Invoice> invoicesList;
    // ArrayList<Room> roomList = Database.getReservations();

    /*public Admin(String userName, String password, LocalDate dateOfbirth, String address)
    {
        super(userName, password, dateOfbirth, address);
    }*/
    public Admin()
    {

    }


    //room functions
    public void createRoom(Room room)
    {
        this.roomList = Database.getRooms();
        roomList.add(room);
        Database.saveData();
    }

    public Room readRoom(int roomNumber)
    {
        this.roomList = Database.getRooms();
        for (int i = 0; i < roomList.size(); i++)
        {
            Room room = roomList.get(i);
            if (room.getRoomNumber() == roomNumber)
            {
                return room;
            }
        }
        System.out.println("Room " + roomNumber + " not found.");
        return null;
    }

    public void updateRoom(int roomNumber, Room updatedRoom)
    {
        this.roomList = Database.getRooms();
        for (int i = 0; i < roomList.size(); i++)
        {
            if (roomList.get(i).getRoomNumber() == roomNumber)
            {
                roomList.set(i, updatedRoom);
                Database.saveData();
                System.out.println("Room " + roomNumber + " updated successfully.");
                return;
            }
        }
        System.out.println("Update failed: Room " + roomNumber + " does not exist.");
    }

    public void deleteRoom(int roomNumber)
    {
        this.roomList = Database.getRooms();
        for (int i = 0; i < roomList.size(); i++)
        {
            if (roomList.get(i).getRoomNumber() == roomNumber)
            {
                roomList.remove(i);
                Database.saveData();
                System.out.println("Room " + roomNumber + " deleted successfully.");
                return;
            }
        }
        System.out.println("Room not found");
    }


    //Amenity Functions
    public void createAmenity(Amenity amenity)
    {
        this.amenityList = Database.getAmenities();
        amenityList.add(amenity);
        Database.saveData();
    }

    public Amenity readAmenity(String name) {
        this.amenityList = Database.getAmenities();
        for (int i = 0; i < amenityList.size(); i++)
        {
            if (amenityList.get(i).getAmenityName().equals(name))
            {
                return amenityList.get(i);
            }
        }
        System.out.println("Amenity not found");
        return null;
    }

    public void updateAmenity(String name, Amenity updatedAmenity) {
        this.amenityList = Database.getAmenities();
        for (int i = 0; i < amenityList.size(); i++)
        {
            if (amenityList.get(i).getAmenityName().equals(name))
            {
                amenityList.set(i, updatedAmenity);
                Database.saveData();
                System.out.println("Amenity " + name + " updated successfully.");
                return;
            }
        }
        System.out.println("Amenity not found");
    }

    public void deleteAmenity(String name) {
        this.amenityList = Database.getAmenities();
        for (int i = 0; i < amenityList.size(); i++)
        {
            if (amenityList.get(i).getAmenityName().equals(name))
            {
                amenityList.remove(i);
                Database.saveData();
                System.out.println("Amenity " + name + " deleted successfully.");
                return;
            }
        }
        System.out.println("Amenity not found");
    }


    //RoomType
    public void createRoomType(RoomType roomtype) {
        this.roomTypeList = Database.getRoomTypes();
        roomTypeList.add(roomtype);
        Database.saveData();
    }

    public RoomType readRoomType(String typeName)
    {
        this.roomTypeList = Database.getRoomTypes();
        for (int i = 0; i < roomTypeList.size(); i++)
        {
            if (roomTypeList.get(i).getTypeName().equals(typeName))
            {
                return roomTypeList.get(i);
            }
        }
        System.out.println("RoomType not found");
        return null;
    }

    public void updateRoomType(String typeName, RoomType updatedType)
    {
        this.roomTypeList = Database.getRoomTypes();
        for (int i = 0; i < roomTypeList.size(); i++)
        {
            if (roomTypeList.get(i).getTypeName().equals(typeName))
            {
                roomTypeList.set(i, updatedType);
                Database.saveData();
                System.out.println("RoomType " + typeName + " updated successfully.");
                return;
            }
        }
        System.out.println("TypeName not found");
    }

    public void deleteRoomType(String typeName)
    {
        this.roomTypeList = Database.getRoomTypes();
        for (int i = 0; i < roomTypeList.size(); i++)
        {
            if (roomTypeList.get(i).getTypeName().equals(typeName)) {
                roomTypeList.remove(i);
                Database.saveData();
                System.out.println("RoomType " + typeName + " deleted successfully.");
                return;
            }
        }
        System.out.println("RoomType not found");
    }


    public void setSeasonalMultiplier(String roomType,double multiplier)
    {
        this.roomTypeList = Database.getRoomTypes();
        if (multiplier < 0) { System.out.println("Error: Multiplier cannot be negative"); return; }
        for(int i=0;i<roomTypeList.size();i++)
        {
            if(roomTypeList.get(i).getTypeName().equals(roomType))
            {
                double oldPrice=roomTypeList.get(i).getBasePrice();
                double newPrice=multiplier*oldPrice;
                roomTypeList.get(i).setPricePerNight(newPrice);
                Database.saveData();
                System.out.println("Seasonal Update for " + roomType + ":");
                System.out.println("Old Price: $" + oldPrice + " | New Price: $" +  newPrice+ " (x" + multiplier + ")");
                return;
            }
        }
    }

    public void generateFinancialReport(int days) {
        this.invoicesList=Database.getInvoices();
        double total = 0;
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        System.out.println("\n--- Financial Report (Last " + days + " Days) ---");
        for (int i=0;i< invoicesList.size();i++) {
            // Assumes Invoices class has getDate() and getAmount()
            if (invoicesList.get(i).getPaymentDate().isAfter(cutoffDate)||invoicesList.get(i).getPaymentDate().isEqual(cutoffDate))
            {
                total += invoicesList.get(i).getTotalAmount();
                System.out.println("ID: " + invoicesList.get(i).getInvoiceID() + " | Date: " + invoicesList.get(i).getPaymentDate() + " | Amount: $" + invoicesList.get(i).getTotalAmount());
            }
        }
        System.out.println("----------------------------------------------");
        System.out.println("TOTAL REVENUE: $" + total);
        System.out.println("----------------------------------------------\n");
    }
}
