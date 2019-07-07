package example.com.engage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import example.com.engage.Common.Common;
import example.com.engage.Model.Request;
import example.com.engage.ViewHolder.BookingHistoryViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BookingHistory extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,BookingHistoryViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    Request currentRequest;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/KGSkinnyLatte.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_booking_history);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadBookings(Common.currentUser.getPhone());
    }

    private void loadBookings(String phone){
        Query getOrderByUser = requests.orderByChild("userPhone").equalTo(phone);
        FirebaseRecyclerOptions<Request> orderOptions = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, BookingHistoryViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BookingHistoryViewHolder viewHolder, int position, @NonNull Request model) {
                //viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txt_booking_date.setText(model.getDate());
                viewHolder.txt_booking_location.setText(model.getLocation());
                viewHolder.txt_booking_status.setText(model.getStatus());
                viewHolder.txt_booking_name.setText(model.getEventName());
                viewHolder.txt_booking_time.setText(model.getTime());

            }

            @NonNull
            @Override
            public BookingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_booking_history,parent,false);
                return new BookingHistoryViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
