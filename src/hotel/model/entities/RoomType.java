package hotel.model.entities;
import hotel.model.enums.*;
public class RoomType 
{
    private String typeName;
    private RoomView roomView;
    private double pricePerNight;
    private String description;
    private double seasonMultiplier;
    private int maxCapacity;

    public RoomType() {}

    public RoomType(String typeName, RoomView roomView, double pricePerNight, String description, double seasonMultiplier , int maxCapacity ) 
    {
        this.typeName = typeName;
        this.roomView = roomView;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.seasonMultiplier = seasonMultiplier;
        this.maxCapacity = maxCapacity;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSeasonMultiplier() {
        return seasonMultiplier;
    }

    public void setSeasonMultiplier(double seasonMultiplier) {
        this.seasonMultiplier = seasonMultiplier;
    }

    private RoomView getRoomView() {
        return roomView;
    }

    private void setRoomView(RoomView roomView) {
        this.roomView = roomView;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public double getEffectivePrice() {
        return pricePerNight * seasonMultiplier;
    }
}
