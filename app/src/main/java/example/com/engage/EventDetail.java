package example.com.engage;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import example.com.engage.Common.Common;
import example.com.engage.Database.Database;
import example.com.engage.Model.Event;
import example.com.engage.Model.Order;
import example.com.engage.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.Queue;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventDetail extends AppCompatActivity implements RatingDialogListener{

    TextView event_name, event_price, event_description, event_userContact, event_location, event_time, event_date, event_userEmail, event_userCompany;
    ImageView event_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating,btnBooking,btnMessage,btnProfile;
    RatingBar ratingBar;
    Button btnShowComment;

    String eventId="";
    String userId="";

    FirebaseDatabase database;
    DatabaseReference event;
    DatabaseReference ratingTbl;

    Event currentEvent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Note: add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/KGSkinnyLatte.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_event_detail);

        btnShowComment = (Button)findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetail.this,ShowComment.class);
                intent.putExtra(Common.INTENT_EVENT_ID,eventId);
                startActivity(intent);
            }
        });

        btnProfile = (FloatingActionButton) findViewById(R.id.btn_show_booking);
        if(Common.currentUser.getPhone().equals(Common.currentEvent.getUserContact())){
            btnProfile.show();
            btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventDetail.this,ShowBooking.class);
                    intent.putExtra(Common.INTENT_EVENT_ID,eventId);
                    startActivity(intent);
                }
            });
        } else {
            btnProfile.hide();
        }
        btnBooking = (FloatingActionButton) findViewById(R.id.btn_booking);
        if(Common.currentUser.getPhone().equals(Common.currentEvent.getUserContact())){
            btnBooking.hide();
        } else {
            btnBooking.show();
            btnBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventDetail.this,BookingActivity.class);
                    intent.putExtra(Common.INTENT_EVENT_ID,eventId);
                    startActivity(intent);
                }
            });
        }



        database = FirebaseDatabase.getInstance();
        event = database.getReference("Event");
        ratingTbl = database.getReference("Rating");

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);

        btnBooking = (FloatingActionButton) findViewById(R.id.btn_booking);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        event_description = (TextView) findViewById(R.id.event_description);
        event_name = (TextView) findViewById(R.id.event_name);
        event_price = (TextView) findViewById(R.id.event_price);
        event_date = (TextView) findViewById(R.id.event_date);
        event_location = (TextView) findViewById(R.id.event_location);
        event_time = (TextView) findViewById(R.id.event_time);
        event_userContact = (TextView) findViewById(R.id.event_contact);
        event_userEmail = (TextView) findViewById(R.id.event_email);
        event_userCompany = (TextView) findViewById(R.id.event_company);

        event_image = (ImageView) findViewById(R.id.img_event);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null){
            eventId = getIntent().getStringExtra("eventId");
        }
        if (!eventId.isEmpty()){

            if(Common.isConnectedToInternet(getApplicationContext())) {
                getDetailEvent(eventId);
                getRatingEvent(eventId);
            }
            else{
                Toast.makeText(EventDetail.this,"Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void showRatingDialog(){
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Event")
                .setDescription("Please give your rating and feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your feedback here...")
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnimation)
                .create(EventDetail.this)
                .show();
    }

    private void getDetailEvent(String eventId){
        event.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentEvent = dataSnapshot.getValue(Event.class);
                if (currentEvent != null) {
                    Picasso.with(getBaseContext()).load(currentEvent.getImage()).into(event_image);

                    collapsingToolbarLayout.setTitle(currentEvent.getName());
                    if (currentEvent.getBooking().equals("FREE")){
                        event_price.setVisibility(View.INVISIBLE);
                    } else if (currentEvent.getBooking().equals("PAID")) {
                        event_price.setText("RM " + currentEvent.getPrice());
                    }
                    event_name.setText(currentEvent.getName());
                    event_description.setText(currentEvent.getDescription());
                    event_date.setText(currentEvent.getDate());
                    event_time.setText(currentEvent.getTime());
                    event_location.setText(currentEvent.getLocation());
                    event_userContact.setText(currentEvent.getUserContact());
                    event_userEmail.setText(currentEvent.getUserEmail());
                    event_userCompany.setText(currentEvent.getCompanyName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRatingEvent(String eventId){
        com.google.firebase.database.Query eventRating = ratingTbl.orderByChild("eventId").equalTo(eventId);
        eventRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count != 0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                eventId,
                String.valueOf(value),
                comments);

        //fix user can rate multiple times
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EventDetail.this,"Thank you for your feedback !!!",Toast.LENGTH_SHORT).show();
                    }
                });
        /*
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){

                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else{
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }

                Toast.makeText(FoodDetail.this,"Thank you for your feedback !!!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
