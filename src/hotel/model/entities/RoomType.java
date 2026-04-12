package Files.Classes;
public class RoomType 
{
    private String typeName;
    private double pricePerNight;
    private String description;
    private double seasonMultiplier;

    public RoomType() {}

    public RoomType(String typeName, double pricePerNight, String description, double seasonMultiplier) 
    {
        this.typeName = typeName;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.seasonMultiplier = seasonMultiplier;
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
