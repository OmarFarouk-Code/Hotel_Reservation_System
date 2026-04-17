package hotel.model.bookings;
import hotel.model.entities.*;
import hotel.interfaces.*;
import hotel.model.enums.PaymentMethod;
import hotel.interfaces.*;
import hotel.model.users.Guest;
import java.time.LocalDate;

public class Invoice implements Payable
{
    int invoiceID;
    Reservation reservation;
    boolean isPaid;
    double totalAmount;
    PaymentMethod paymentMethod;
    LocalDate paymentDate;
    String appliedPromoCode;
    double discountAmount;

    public Invoice() {
    }

    public Invoice(int invoiceID, Reservation reservation, boolean isPaid, double totalAmount, PaymentMethod paymentMethod, LocalDate paymentDate, String appliedPromoCode, double discountAmount) {
        this.invoiceID = invoiceID;
        this.reservation = reservation;
        this.isPaid = isPaid;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.appliedPromoCode = appliedPromoCode;
        this.discountAmount = discountAmount;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAppliedPromoCode(String appliedPromoCode) {
        this.appliedPromoCode = appliedPromoCode;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public void pay(Guest guest, PaymentMethod method) {

    }

    @Override
    public double getTotal() {
        return 0;
    }
}
