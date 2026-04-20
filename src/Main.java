import java.util.Scanner;



public class Main {

        static Scanner scanner = new Scanner(System.in);

        public static void main(String[] args) {

            while (true) {
                System.out.println("\n=== HOTEL SYSTEM ===");
                System.out.println("1. Guest");
                System.out.println("2. Receptionist");
                System.out.println("3. Admin");
                System.out.println("0. Exit");

                System.out.print("Choose role: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        guestMenu();
                        break;
                    case 2:
                        receptionistMenu();
                        break;
                    case 3:
                        adminMenu();
                        break;
                    case 0:
                        System.out.println("Goodbye ");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }


        public static void guestMenu() {
            while (true) {
                System.out.println("\n--- GUEST MENU ---");
                System.out.println("1. View Reservations");
                System.out.println("2. Pay Invoice");
                System.out.println("3. Checkout");
                System.out.println("0. Back");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Viewing reservations...");
                        // call guest.viewReservation()
                        break;

                    case 2:
                        System.out.print("Enter Invoice ID: ");
                        int invoiceId = scanner.nextInt();
                        // guest.payInvoice(invoiceId, method)
                        System.out.println("Invoice paid.");
                        break;

                    case 3:
                        System.out.print("Enter Reservation ID: ");
                        int resId = scanner.nextInt();
                        // guest.checkout(resId)
                        System.out.println("Checked out.");
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }


        public static void receptionistMenu() {
            while (true) {
                System.out.println("\n--- RECEPTIONIST MENU ---");
                System.out.println("1. Check-In");
                System.out.println("2. Check-Out");
                System.out.println("0. Back");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter Reservation ID: ");
                        int checkInId = scanner.nextInt();
                        // receptionist.manageCheckIn(checkInId)
                        System.out.println("Checked in successfully.");
                        break;

                    case 2:
                        System.out.print("Enter Reservation ID: ");
                        int checkOutId = scanner.nextInt();
                        // receptionist.manageCheckOut(checkOutId)
                        System.out.println("Checked out successfully.");
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }


        public static void adminMenu() {
            while (true) {
                System.out.println("\n--- ADMIN MENU ---");
                System.out.println("1. Create Room");
                System.out.println("2. View Rooms");
                System.out.println("3. Delete Room");
                System.out.println("0. Back");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Creating room...");
                        // admin.createRoom(...)
                        break;

                    case 2:
                        System.out.println("Viewing rooms...");
                        // admin.readRoom(...)
                        break;

                    case 3:
                        System.out.println("Deleting room...");
                        // admin.deleteRoom(...)
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }
    }

