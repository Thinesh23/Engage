package example.com.engage.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import example.com.engage.R;

public class BookingHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_booking_name, txt_booking_time, txt_booking_date, txt_booking_location,txt_booking_status;
    public BookingHistoryViewHolder(View itemView) {
        super(itemView);
        txt_booking_status = (TextView) itemView.findViewById(R.id.txt_booking_status);
        txt_booking_name = (TextView) itemView.findViewById(R.id.txt_event_name);
        txt_booking_time = (TextView) itemView.findViewById(R.id.txt_booking_time);
        txt_booking_date = (TextView) itemView.findViewById(R.id.txt_booking_date);
        txt_booking_location = (TextView) itemView.findViewById(R.id.txt_location);
    }
}
