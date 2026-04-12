package hotel.model.users;
import java.time.LocalDate;

public abstract class User 
{
    private String userName;
    private String password;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    
    public User() {}

    private User(String userName, String password, LocalDate dateOfBirth, String phoneNumber) 
    {
        this.userName = userName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
