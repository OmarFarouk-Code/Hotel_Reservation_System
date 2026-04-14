package hotel;

import hotel.model.enums.RoomType;
import hotel.model.enums.UserType;
import hotel.model.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Guest extends User {
    private double balance;
    private List<String> roomperefrences;
    private RoomType roomoptions;


    Scanner input =new Scanner(System.in);
    public void registerextention() {
            System.out.println("Please enter your Balance");
            balance = input.nextDouble();
            while (balance < 0) {
                System.out.println("Invalid Balance, Try again!");
                balance = input.nextDouble();
            }
            input.nextLine();//used to leave line
            List<String> roomperefrences = new ArrayList<String>();
            System.out.println("**room preferences**");
            System.out.println("Please enter your preferred floor");
            roomperefrences.add(input.nextLine());
            System.out.println("Please enter the preferred room type");
            System.out.println("1.Pool, 2.Sea view, 3.Garden");
            roomoptions=RoomType.valueOf(input.next().toUpperCase());
            roomperefrences.add(String.valueOf(roomoptions));
            System.out.println("Account has been successfully created Welcome "+UserName);


        }

    }






