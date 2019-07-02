package example.com.engage.Model;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String IsOrganizer;
    private String secureCode;
    private String homeAddress;
    private Double rewardCash;

    public User() {
    }

    public User(String name, String password, String secureCode, String organizer) {
        Name = name;
        Password = password;
        IsStaff = "false";
        IsOrganizer = organizer;
        this.secureCode = secureCode;
    }

    public Double getRewardCash() {
        return rewardCash;
    }

    public void setRewardCash(Double rewardCash) {
        this.rewardCash = rewardCash;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getIsOrganizer() {
        return IsOrganizer;
    }

    public void setIsOrganizer(String isOrganizer) {
        IsOrganizer = isOrganizer;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
