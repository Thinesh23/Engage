package example.com.engage.Model;

public class User {
    private String FirstName;
    private String LastName;
    private String Email;
    private String Password;
    private String Phone;
    private String UserId;
    private String IsStaff;
    private String IsOrganizer;
    private String secureCode;
    private String CompanyName;

    public User() {
    }

    public User(String firstname, String lastname, String password, String email, String secureCode, String organizer, String companyName) {
        FirstName = firstname;
        LastName = lastname;
        Password = password;
        Email = email;
        IsStaff = "false";
        IsOrganizer = organizer;
        this.secureCode = secureCode;
        CompanyName = companyName;
    }


    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
       CompanyName = companyName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
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
        this.Phone = phone;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

}
