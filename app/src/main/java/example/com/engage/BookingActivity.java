package example.com.engage;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import example.com.engage.Common.Common;
import example.com.engage.Common.Config;
import example.com.engage.Model.Event;
import example.com.engage.Model.Request;

public class BookingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests;
    DatabaseReference event;

    String eventId="";

    Event currentEvent;
    Request currentRequest;

    private static final int PAYPAL_REQUEST_CODE = 9999;

    TextView txt_booking_time, txt_booking_date, txt_person_name, txt_payment, txt_event_name,txt_company_name,txt_contact_no,txt_location,txt_contact_mail, txt_user_no, txt_user_mail;
    Button btnConfirm;
    ImageView event_image;

    //Paypal payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //use SandBox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confim);

        txt_booking_time = (TextView) findViewById(R.id.txt_booking_time);
        txt_booking_date = (TextView) findViewById(R.id.txt_booking_date);
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
        requests = database.getReference("Requests");

        //init paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

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

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_payment.getText().equals("Not Required")){
                    confirmBooking();
                } else {
                    confirmPayment();
                }

            }
        });
    }

    private void confirmBooking(){
        Query query = requests.orderByChild("eventId").equalTo(eventId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(data.getValue(Request.class).getUserPhone().equals(Common.currentUser.getPhone())){
                            Toast.makeText(BookingActivity.this, "You've already booked this event", Toast.LENGTH_SHORT).show();
                        } else {
                            Request request = new Request (
                                    "Active",
                                    eventId,
                                    "Free",
                                    "Not Required",
                                    Common.currentUser.getFirstName(),
                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getEmail(),
                                    txt_contact_no.getText().toString(),
                                    txt_contact_mail.getText().toString(),
                                    txt_company_name.getText().toString(),
                                    txt_booking_date.getText().toString(),
                                    txt_booking_time.getText().toString(),
                                    txt_location.getText().toString(),
                                    txt_event_name.getText().toString());

                            //Submit to Firebase
                            String order_number = String.valueOf(System.currentTimeMillis());
                            requests.child(order_number)
                                    .setValue(request);

                            Toast.makeText(BookingActivity.this, "Thank you, booking confirmed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                } else {
                    Request request = new Request (
                            "Active",
                            eventId,
                            "Free",
                            "Not Required",
                            Common.currentUser.getFirstName(),
                            Common.currentUser.getPhone(),
                            Common.currentUser.getEmail(),
                            txt_contact_no.getText().toString(),
                            txt_contact_mail.getText().toString(),
                            txt_company_name.getText().toString(),
                            txt_booking_date.getText().toString(),
                            txt_booking_time.getText().toString(),
                            txt_location.getText().toString(),
                            txt_event_name.getText().toString());

                    //Submit to Firebase
                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request);

                    Toast.makeText(BookingActivity.this, "Thank you, booking confirmed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void confirmPayment(){
        String formatAmount = txt_payment.getText().toString()
                .replace("RM","")
                .replace(",","")
                .replace(" ","");
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                "MYR",
                "Event App Order",
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        final JSONObject jsonObject = new JSONObject(paymentDetail);
                        final String response = jsonObject.getJSONObject("response").getString("state");

                        Query query = requests.orderByChild("eventId").equalTo(eventId);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.getValue(Request.class).getUserPhone().equals(Common.currentUser.getPhone())) {
                                            Toast.makeText(BookingActivity.this, "You've already booked this event", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Request request = new Request(
                                                    "Active",
                                                    eventId,
                                                    "Paypal",
                                                    response,
                                                    Common.currentUser.getFirstName(),
                                                    Common.currentUser.getPhone(),
                                                    Common.currentUser.getEmail(),
                                                    txt_contact_no.getText().toString(),
                                                    txt_contact_mail.getText().toString(),
                                                    txt_company_name.getText().toString(),
                                                    txt_booking_date.getText().toString(),
                                                    txt_booking_time.getText().toString(),
                                                    txt_location.getText().toString(),
                                                    txt_event_name.getText().toString());

                                            //Submit to Firebase
                                            String order_number = String.valueOf(System.currentTimeMillis());
                                            requests.child(order_number)
                                                    .setValue(request);

                                            Toast.makeText(BookingActivity.this, "Thank you, booking confirmed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                } else {
                                    Request request = new Request (
                                            "Active",
                                            eventId,
                                            "Free",
                                            "Not Required",
                                            Common.currentUser.getFirstName(),
                                            Common.currentUser.getPhone(),
                                            Common.currentUser.getEmail(),
                                            txt_contact_no.getText().toString(),
                                            txt_contact_mail.getText().toString(),
                                            txt_company_name.getText().toString(),
                                            txt_booking_date.getText().toString(),
                                            txt_booking_time.getText().toString(),
                                            txt_location.getText().toString(),
                                            txt_event_name.getText().toString());

                                    //Submit to Firebase
                                    String order_number = String.valueOf(System.currentTimeMillis());
                                    requests.child(order_number)
                                            .setValue(request);

                                    Toast.makeText(BookingActivity.this, "Thank you, booking confirmed", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(BookingActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(BookingActivity.this, "Invalid Payment", Toast.LENGTH_SHORT).show();
                }
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
                        txt_payment.setText("RM "+currentEvent.getPrice());
                    }
                    txt_booking_time.setText(currentEvent.getTime());
                    txt_booking_date.setText(currentEvent.getDate());
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
