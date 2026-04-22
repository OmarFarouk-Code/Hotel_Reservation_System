package hotel.model.users;
import hotel.core.Database;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.UserType;
import hotel.model.staff.Admin;
import hotel.model.staff.Receptionist;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
public abstract class User implements Serializable
{
    private transient Scanner input = new Scanner(System.in);

    protected String UserName;
    protected String password;
    private UserType Typeofuser=null;
    protected Gender theGender;
    protected String newpassword;
    private int failedLoginAttempts =0;
    private AccountStatus accountStatus;
    protected LocalDate dateOfbirth;
    protected String phoneNumber;
    protected String address;

    public User() {}

    public User(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address) {
        UserName = userName;
        this.password = password;
        Typeofuser = typeofuser;
        this.theGender = theGender;
        this.newpassword = newpassword;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountStatus = accountStatus;
        this.dateOfbirth = dateOfbirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getTypeofuser() {
        return Typeofuser;
    }

    public void setTypeofuser(UserType typeofuser) {
        Typeofuser = typeofuser;
    }

    public Gender getTheGender() {
        return theGender;
    }

    public void setTheGender(Gender theGender) {
        this.theGender = theGender;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getDateOfbirth() {
        return dateOfbirth;
    }

    public void setDateOfbirth(LocalDate dateOfbirth) {
        this.dateOfbirth = dateOfbirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static boolean Datechecker(String dateStr) {
        try {
            LocalDate.parse(dateStr); // Default YYYY-MM-DD
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void Login()

    {
        while(failedLoginAttempts<5){

            if (accountStatus == AccountStatus.LOCKED) {
                System.out.println("This account is locked. Please contact an administrator.");
                return;
            }
            while (true) {
                System.out.print("Please enter your Role (Receptionist, Admin, Guest: ");
                String roleInput = input.nextLine().trim().toUpperCase();


                if (roleInput.equals("RECEPTIONIST") || roleInput.equals("R")) {
                    Typeofuser=UserType.RECEPTIONIST;
                    break;
                } else if (roleInput.equals("ADMIN") || roleInput.equals("A")) {
                    Typeofuser=UserType.ADMIN;
                    break;
                }else if(roleInput.equals("GUEST")|| roleInput.equals("G")){
                    Typeofuser=UserType.GUEST;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter Receptionist, Admin , Guest.");
                }
            }
            System.out.print("Enter Username: ");
            String currentName = input.nextLine();
            System.out.print("Enter Password: ");
            String currentPass = input.nextLine();
        if(Typeofuser== UserType.GUEST){
        boolean found=false;
        List<Guest> guestList = Database.getGuests();
        for(int i=0; i<Database.getGuests().size();i++) {
        if(guestList.get(i).getUserName().equals(currentName) && guestList.get(i).getPassword().equals(currentPass)){
            found=true;
            break;
        }
        }
        if(found && accountStatus==AccountStatus.ACTIVE) {
            System.out.println("Access Granted,Welcome " + currentName);
            //homepage for guest
            break;
        }else {
            System.out.println("Access Denied,Please try again !");
            failedLoginAttempts=failedLoginAttempts+1;
            Database.saveData();

        }
    }if(Typeofuser==UserType.RECEPTIONIST) {
            boolean found = false;
            List<Receptionist> guestList = Database.getReceptionists();
            for (int i = 0; i < Database.getReceptionists().size(); i++) {
                if (guestList.get(i).getUserName().equals(currentName) && guestList.get(i).getPassword().equals(currentPass)) {
                    found = true;
                    break;
                }
            }
            if (found && accountStatus==AccountStatus.ACTIVE) {
                System.out.println("Access Granted,Welcome " + currentName);
                //Homepage of receptionist
                break;
            }  else {
                System.out.println("Access Denied,Please try again !");
                failedLoginAttempts=failedLoginAttempts+1;
                Database.saveData();

            }
        }
            if(Typeofuser==UserType.ADMIN) {
                boolean found = false;
                List<Admin> guestList = Database.getAdmins();
                for (int i = 0; i < Database.getAdmins().size(); i++) {
                    if (guestList.get(i).getUserName().equals(currentName) && guestList.get(i).getPassword().equals(currentPass)) {
                        found = true;
                        break;
                    }
                }
                if (found && accountStatus==AccountStatus.ACTIVE) {
                    System.out.println("Access Granted,Welcome " + currentName);
                    //Homepage of admin
                    break;
                } else {
                    System.out.println("Access Denied,Please try again !");
                    failedLoginAttempts=failedLoginAttempts+1;
                    Database.saveData();

                    }
                }
            }

        if(failedLoginAttempts>=5){
            System.out.println("Too much unsuccessful login attempts, account is Locked");
            accountStatus=AccountStatus.LOCKED;
        Database.saveData();
        }

        }
        public void passwordconfirmation(String Pass1,String Pass2){
        while(!(Pass1.equals(Pass2))) {
            System.out.println("Password don't match,Please re-enter your password");
            Pass1=input.nextLine();
            System.out.println("Please confirm Password");
            Pass2=input.nextLine();
            }
        newpassword=Pass1;
        }
        public void ResetPassword(String theUserName,UserType TheUserType){
            String CurrentPassword;
             int counter=0;
            System.out.println("Please re-enter your current password");
            CurrentPassword=input.nextLine();
            if(TheUserType==UserType.ADMIN) {
                boolean found = false;
                List<Admin> guestList = Database.getAdmins();
                for (int i = 0; i < Database.getAdmins().size(); i++) {
                    if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                        found = true;
                        counter = i;
                        break;
                    }
                }
                if (found) {
                    System.out.println("Please enter new password");
                    newpassword = input.nextLine();
                    System.out.println("confirm password");
                    String confirmpass = input.nextLine();
                    passwordconfirmation(newpassword, confirmpass);
                    Admin targetAdmin = guestList.get(counter);
                    targetAdmin.setPassword(newpassword);
                    Database.saveData();
                } else {
                    System.out.println("The Username or password is not found,Please re-enter your username");
                    String AUsername = input.nextLine();
                    System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                    UserType Usercategory = UserType.valueOf(input.nextLine());
                    input.nextLine();
                    ResetPassword(AUsername, Usercategory);
                }
            }  if(TheUserType==UserType.RECEPTIONIST) {
                    boolean found = false;
                    List<Receptionist> guestList = Database.getReceptionists();
                    for (int i = 0; i < Database.getReceptionists().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter=i;
                            break;
                        }
                    }
                    if(found){
                        System.out.println("Please enter new password");
                        newpassword=input.nextLine();
                        System.out.println("confirm password");
                        String confirmpass=input.nextLine();
                        passwordconfirmation(newpassword,confirmpass);
                        Receptionist targetReceptionist = guestList.get(counter);
                        targetReceptionist.setPassword(newpassword);
                        Database.saveData();
                    }
                    else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername=input.nextLine();
                        System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                        UserType Usercategory= UserType.valueOf(input.nextLine());
                        input.nextLine();
                        ResetPassword(AUsername,Usercategory);
                    }}
                if(TheUserType==UserType.GUEST) {
                    boolean found = false;
                    List<Guest> guestList = Database.getGuests();
                    for (int i = 0; i < Database.getGuests().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter = i;
                            break;
                        }
                    }
                    if (found) {
                        System.out.println("Please enter new password");
                        newpassword = input.nextLine();
                        System.out.println("confirm password");
                        String confirmpass = input.nextLine();
                        passwordconfirmation(newpassword, confirmpass);
                        Guest targetGuest = guestList.get(counter);
                        targetGuest.setPassword(newpassword);
                        Database.saveData();
                    } else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername = input.nextLine();
                        System.out.println("Please enter your roll 1.Guest 2.Admin 3.Receptionist");
                        UserType Usercategory = UserType.valueOf(input.nextLine());
                        input.nextLine();
                        ResetPassword(AUsername, Usercategory);
                    }
                }
    }
}

