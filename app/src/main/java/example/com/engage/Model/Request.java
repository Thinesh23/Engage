package example.com.engage.Model;

import java.util.List;

public class Request {
    private String PaymentState;
    private String PaymentMethod;
    private String FirstName;
    private String UserPhone;
    private String UserEmail;
    private String OrganizerPhone;
    private String OrganizerEmail;
    private String OrganizerCompany;
    private String Date;
    private String Time;
    private String Location;
    private String EventName;
    private String EventId;
    private String Status;

    public Request() {
    }

    public Request(String status, String eventId, String paymentMethod, String paymentState, String firstName, String userPhone, String userEmail, String organizerPhone, String organizerEmail, String organizerCompany, String date, String time, String location, String eventName) {
        Status = status;
        EventId = eventId;
        PaymentMethod = paymentMethod;
        PaymentState = paymentState;
        FirstName = firstName;
        UserPhone = userPhone;
        UserEmail = userEmail;
        OrganizerPhone = organizerPhone;
        OrganizerEmail = organizerEmail;
        OrganizerCompany = organizerCompany;
        Date = date;
        Time = time;
        Location = location;
        EventName = eventName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return PaymentState;
    }

    public void setPaymentState(String paymentState) {
        PaymentState = paymentState;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getOrganizerPhone() {
        return OrganizerPhone;
    }

    public void setOrganizerPhone(String organizerPhone) {
        OrganizerPhone = organizerPhone;
    }

    public String getOrganizerEmail() {
        return OrganizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        OrganizerEmail = organizerEmail;
    }

    public String getOrganizerCompany() {
        return OrganizerCompany;
    }

    public void setOrganizerCompany(String organizerCompany) {
        OrganizerCompany = organizerCompany;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }
}
