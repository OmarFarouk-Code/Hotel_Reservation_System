package hotel.interfaces;
import hotel.model.enums.*;
import hotel.model.users.*;
public interface Payable 
{
    public void pay( Guest guest , PaymentMethod method );
    public double getTotal();   
}
