package Files.Interfaces;

public interface Payable 
{
    public void pay( Guest guest , PaymentMethod method );
    public double getTotal();   
} 