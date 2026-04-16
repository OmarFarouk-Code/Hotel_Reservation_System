package hotel.model.Reservations;

public class invoice implements Payable {
        private Reservation reservation;
        private double totalAmount;
        private double discountAmount;
        private boolean isPaid;

        public invoice(Reservation r, double total) {
            this.reservation = r;
            this.totalAmount = total;
        }

        @Override
        public void pay(Guest guest, PaymentMethod method) {
            double amount = getTotal();
            if (guest.getBalance() >= amount) {
                guest.setBalance(guest.getBalance() - amount);
                isPaid = true;
            }
        }

        @Override
        public double getTotal() {
            return totalAmount - discountAmount;
        }
    }

}
