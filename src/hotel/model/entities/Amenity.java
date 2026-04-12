package Files.Classes;
public class Amenity 
{
    private String amenityName;
    private String description;
    private double amenityPrice;

    public Amenity() {}

    public Amenity(String amenityName, String description, double amenityPrice) 
    {
        this.amenityName = amenityName;
        this.description = description;
        this.amenityPrice = amenityPrice;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmenityPrice() {
        return amenityPrice;
    }

    public void setAmenityPrice(double amenityPrice) {
        this.amenityPrice = amenityPrice;
    }

}
