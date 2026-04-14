package hotel.model.entities;

import java.io.Serializable;

public class RoomType implements Serializable
{
    private String typeName;
    private double pricePerNight;
    private String description;
    private double seasonMultiplier;
    private double basePrice;
    private int maxCapacity;


    public RoomType() {}

    public RoomType(String typeName, double pricePerNight, String description, double seasonMultiplier,double basePrice)
    {
        this.typeName = typeName;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.seasonMultiplier = seasonMultiplier;
        this.basePrice=basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
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

    public double getEffectivePrice() {
        return pricePerNight * seasonMultiplier;
    }
}
