package example.com.engage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.engage.Common.Common;
import example.com.engage.Model.Rating;
import example.com.engage.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder>adapter;

    String eventId="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/KGSkinnyLatte.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_show_comment);

        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");

        recyclerView = (RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null){
                    eventId = getIntent().getStringExtra(Common.INTENT_EVENT_ID);
                }
                if(!eventId.isEmpty() && eventId !=null){
                    Query query = ratingTbl.orderByChild("eventId").equalTo(eventId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());
                        }

                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(eventId);
                }
            }
        });

        //Thread to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if(getIntent()!=null){
                    eventId = getIntent().getStringExtra(Common.INTENT_EVENT_ID);
                }
                if(!eventId.isEmpty() && eventId !=null){
                    Query query = ratingTbl.orderByChild("eventId").equalTo(eventId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());
                        }

                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(eventId);
                }
            }
        });

    }

    private void loadComment(String eventId){
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}