package example.com.engage.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import example.com.engage.R;

public class ShowBookingViewHolder extends RecyclerView.ViewHolder{
    public TextView txtUserPhone,txtUserEmail, txtUserName, txtPaymentState;
    public ShowBookingViewHolder(View itemView) {
        super(itemView);
        txtUserEmail = (TextView)itemView.findViewById(R.id.txtUserEmail);
        txtUserPhone = (TextView)itemView.findViewById(R.id.txtUserPhone);
        txtUserName = (TextView)itemView.findViewById(R.id.txtUserName);
        txtPaymentState = (TextView)itemView.findViewById(R.id.txtPaymentState);
    }
}