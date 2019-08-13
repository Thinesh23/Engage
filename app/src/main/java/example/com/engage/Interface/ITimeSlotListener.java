package example.com.engage.Interface;

import java.util.List;

import example.com.engage.Model.TimeSlot;

public interface ITimeSlotListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();

}
