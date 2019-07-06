package example.com.engage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import example.com.engage.Common.Common;
import example.com.engage.Model.Event;

public class BookingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference booking;
    DatabaseReference event;

    String eventId="";

    Event currentEvent;

    TextView txt_booking_time, txt_person_name, txt_payment, txt_event_name,txt_company_name,txt_contact_no,txt_location,txt_contact_mail, txt_user_no, txt_user_mail;
    Button btnConfirm;
    ImageView event_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confim);

        txt_booking_time = (TextView) findViewById(R.id.txt_booking_time);
        txt_person_name = (TextView) findViewById(R.id.txt_person_name);
        txt_payment = (TextView) findViewById(R.id.txt_payment);
        txt_event_name = (TextView) findViewById(R.id.txt_event_name);
        txt_company_name = (TextView) findViewById(R.id.txt_company_name);
        txt_contact_no = (TextView) findViewById(R.id.txt_contact_no);
        txt_contact_mail = (TextView) findViewById(R.id.txt_contact_mail);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_user_no = (TextView) findViewById(R.id.txt_user_no);
        txt_user_mail = (TextView) findViewById(R.id.txt_user_mail);

        event_image = (ImageView) findViewById(R.id.img_event);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        database = FirebaseDatabase.getInstance();
        event = database.getReference("Event");

        if(getIntent() != null){
            eventId = getIntent().getStringExtra("eventId");
        }
        if (!eventId.isEmpty()){

            if(Common.isConnectedToInternet(getApplicationContext())) {
                getDetailBooking(eventId);
            }
            else{
                Toast.makeText(BookingActivity.this,"Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getDetailBooking(String eventId){
        event.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentEvent = dataSnapshot.getValue(Event.class);
                if (currentEvent != null) {
                    Picasso.with(getBaseContext()).load(currentEvent.getImage()).into(event_image);

                    if(currentEvent.getBooking().equals("FREE")){
                        txt_payment.setText("Not Required");
                    } else {
                        txt_payment.setText("Required");
                    }
                    txt_booking_time.setText(currentEvent.getTime() + " at " + currentEvent.getDate());
                    txt_company_name.setText(currentEvent.getCompanyName());
                    txt_location.setText(currentEvent.getLocation());
                    txt_contact_no.setText(currentEvent.getUserContact());
                    txt_event_name.setText(currentEvent.getName());
                    txt_contact_mail.setText(currentEvent.getUserEmail());
                    txt_person_name.setText(Common.currentUser.getFirstName() + " " + Common.currentUser.getLastName());
                    txt_user_mail.setText(Common.currentUser.getEmail());
                    txt_user_no.setText(Common.currentUser.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
