package hotel.model.enums;

public enum DiningPackage 
{
   private final double priceAddedperNight;
   
   BREAKFAST_ONLY(0),
   HALF_BOARD(500),
   FULL_BOARD(1000),
   ALL_INCLUSIVE(1500),

   DiningPackage(double priceAddedperNight) {
       this.priceAddedperNight = priceAddedperNight;
   }
   
   public double getPriceAddedperNight() {
       return priceAddedperNight;
   }
}
