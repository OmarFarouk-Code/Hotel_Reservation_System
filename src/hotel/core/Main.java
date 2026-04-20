package hotel.core;

import java.util.Scanner;

public class Main {
    static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        int choice=0;
        while(choice!=2&&choice!=1&&choice!=3){
        System.out.println("Please enter number: 1.Guest | 2.Admin | Receptionist");
        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
            if (choice == 1) {

            } if(choice==2){

            }
            if(choice==3){


            }
        }
        }
    }
}