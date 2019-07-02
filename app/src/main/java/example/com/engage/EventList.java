package example.com.engage;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;

import example.com.engage.Common.Common;
import example.com.engage.Database.Database;
import example.com.engage.Interface.ItemClickListener;
import example.com.engage.Model.Event;
import example.com.engage.Model.Order;
import example.com.engage.ViewHolder.EventViewHolder;

import com.andremion.counterfab.CounterFab;
import com.facebook.CallbackManager;
import com.facebook.share.Share;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference eventList;
    FirebaseStorage storage;
    StorageReference storageReference;

    DataSnapshot newSnap;

    MaterialEditText edtName, edtDescription, edtPrice, edtLocation, edtDate, edtTime;
    Button btnSelect, btnUpload, btnSetDate, btnSetTime;

    RadioButton rdiFree, rdiPaid;

    Calendar c;
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    Event newEvent;
    Uri saveUri;

    String categoryId = "";
    String booking;
    FirebaseRecyclerAdapter<Event,EventViewHolder> adapter;
    FirebaseRecyclerAdapter<Event,EventViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favourites
    Database localDB;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    FloatingActionButton fab;

    SwipeRefreshLayout swipeRefreshLayout;

    //Create Target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create Photo from Bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

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
        setContentView(R.layout.activity_event_list);

        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
        eventList = database.getReference("Event");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Local DB
        localDB = new Database(this);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        if (Common.currentUser.getIsOrganizer().equals("true") || Common.currentUser.getIsStaff().equals("true")) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddEventDialog();
                }
            });
        } else {
            fab.hide();
        }

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null){
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null){
                    if(Common.isConnectedToInternet(getBaseContext())){
                        loadListEvent(categoryId);
                    } else {
                        Toast.makeText(EventList.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null){
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null){
                    if(Common.isConnectedToInternet(getBaseContext())){
                        loadListEvent(categoryId);
                    } else {
                        Toast.makeText(EventList.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //Search
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your event");
                loadSuggest();
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest = new ArrayList<String>();
                        for (String search:suggestList) {
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if (!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_event);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }


    private void showAddEventDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventList.this);
        alertDialog.setTitle("Add new Event");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_event_layout = inflater.inflate(R.layout.add_new_event_layout, null);

        edtName = add_event_layout.findViewById(R.id.edtName);
        edtDescription = add_event_layout.findViewById(R.id.edtDescription);
        edtPrice = add_event_layout.findViewById(R.id.edtPrice);
        edtLocation = add_event_layout.findViewById(R.id.edtLocation);
        edtDate = add_event_layout.findViewById(R.id.edtDate);
        edtTime = add_event_layout.findViewById(R.id.edtTime);
        btnUpload = add_event_layout.findViewById(R.id.btnUpload);
        btnSelect = add_event_layout.findViewById(R.id.btnSelect);
        btnSetDate = add_event_layout.findViewById(R.id.btnSetDate);
        btnSetTime = add_event_layout.findViewById(R.id.btnSetTime);
        rdiFree = add_event_layout.findViewById(R.id.rdiFree);
        rdiPaid = add_event_layout.findViewById(R.id.rdiPaid);

        if (rdiFree.isChecked()){
            booking = "FREE";

        } else if (rdiPaid.isChecked()){
            booking = "PAID";
            edtPrice.clearFocus();
        }

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_event_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newEvent != null) {
                    eventList.push().setValue(newEvent);
                    Snackbar.make(swipeRefreshLayout, "New Event" + newEvent.getName() + " was added",
                            Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void setTime(){
        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        tpd = new TimePickerDialog(EventList.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                edtTime.setText(hourOfDay + ":" + minutes);
            }
        }, 0, 0,true);
        tpd.show();

    }

    private void setDate(){
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        dpd = new DatePickerDialog(EventList.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtDate.setText(year + "-" + (month+1) + "-" + dayOfMonth);
            }
        },year, month, day);
        dpd.show();
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            // set image name
            String imageName = UUID.randomUUID().toString();
            // set folder
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            // uploading image to folder
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(EventList.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            // set value for new Category if image uploaded
                            // and we can get download link 'uri'
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newEvent = new Event();
                                    //set value for newEvent if image uploaded
                                    newEvent.setUserContact(Common.currentUser.getPhone());
                                    newEvent.setDescription(edtDescription.getText().toString());
                                    newEvent.setBooking(booking);
                                    newEvent.setDate(edtDate.getText().toString());
                                    newEvent.setTime(edtTime.getText().toString());
                                    newEvent.setLocation(edtLocation.getText().toString());
                                    newEvent.setImage(uri.toString());
                                    newEvent.setMenuId(categoryId);
                                    newEvent.setName(edtName.getText().toString());
                                    if (booking == "FREE") {
                                        newEvent.setPrice("0");
                                    } else if (booking == "PAID") {
                                        newEvent.setPrice(edtPrice.getText().toString());
                                    }

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(EventList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // don't worry about this error
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected !");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        newEvent = adapter.getItem(item.getOrder());
        if (Common.currentUser.getPhone() == newEvent.getUserContact() || Common.currentUser.getIsStaff().equals("true")) {
            String title = item.getTitle().toString();
            if (item.getTitle().equals(Common.UPDATE)) {
                // method to show action when select update this method take to param(key,item)
                showUpdateEventDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            } else if (item.getTitle().equals(Common.DELETE)) {
                // method to show action when select delete this method take param(key)
                deleteEvent(adapter.getRef(item.getOrder()).getKey());
            }
        } else {
            Toast.makeText(EventList.this, "You are not authorized to do this action", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void deleteEvent(String key) {
        eventList.child(key).removeValue();
    }

    private void showUpdateEventDialog(final String key, final Event item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventList.this);
        alertDialog.setTitle("Edit Event");
        alertDialog.setMessage("Please fill full information");

        // inflating layout
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_event_layout = inflater.inflate(R.layout.add_new_event_layout, null);

        edtName = add_new_event_layout.findViewById(R.id.edtName);
        edtDescription = add_new_event_layout.findViewById(R.id.edtDescription);
        edtPrice = add_new_event_layout.findViewById(R.id.edtPrice);
        edtLocation = add_new_event_layout.findViewById(R.id.edtLocation);
        edtDate = add_new_event_layout.findViewById(R.id.edtDate);
        edtTime = add_new_event_layout.findViewById(R.id.edtTime);
        btnUpload = add_new_event_layout.findViewById(R.id.btnUpload);
        btnSelect = add_new_event_layout.findViewById(R.id.btnSelect);
        btnSetDate = add_new_event_layout.findViewById(R.id.btnSetDate);
        btnSetTime = add_new_event_layout.findViewById(R.id.btnSetTime);
        rdiFree = add_new_event_layout.findViewById(R.id.rdiFree);
        rdiPaid = add_new_event_layout.findViewById(R.id.rdiPaid);

        if (rdiFree.isChecked()){
            booking = "FREE";
            edtPrice.setFocusable(false);
            edtPrice.setText("0");
        } else if (rdiPaid.isChecked()){
            booking = "PAID";
            edtPrice.setFocusable(true);
        }

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        //set default
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        booking = item.getBooking();
        if (booking == "FREE"){
            rdiFree.setChecked(true);
            rdiPaid.setChecked(false);
        }
        else if (booking == "PAID"){
            rdiFree.setChecked(false);
            rdiPaid.setChecked(true);
        }
        edtPrice.setText(item.getPrice());
        edtLocation.setText(item.getLocation());
        edtDate.setText(item.getDate());
        edtTime.setText(item.getTime());


        // set layout and icon for dialog
        alertDialog.setView(add_new_event_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        // set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                // Here, pushing new category to Firebase Database

                item.setName(edtName.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setLocation(edtLocation.getText().toString());
                item.setUserContact(Common.currentUser.getPhone());
                item.setBooking(booking);
                item.setDate(edtDate.getText().toString());
                item.setTime(edtTime.getText().toString());
                item.setDescription(edtDescription.getText().toString());

                eventList.child(key).setValue(item);
                //eventList.push().setValue(newEvent);
                Snackbar.make(swipeRefreshLayout, "Event " + item.getName() + " was edited",
                        Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Event item) {
        if(saveUri!=null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //set value for newCategory if image upload and we can get download link
                            mDialog.dismiss();
                            Toast.makeText(EventList.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(EventList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Don'r worry about this error
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded"+progress+"%");
                        }
                    });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {
        //create query by name
        Query searchByName = eventList.orderByChild("name").equalTo(text.toString());
        //Create options with query
        FirebaseRecyclerOptions<Event> eventOptions = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(searchByName,Event.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(eventOptions) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position, @NonNull Event model) {
                viewHolder.event_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.event_image);

                final Event local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent eventDetail = new Intent(EventList.this, EventDetail.class);
                        eventDetail.putExtra("eventId", searchAdapter.getRef(position).getKey());
                        startActivity(eventDetail);
                    }
                });
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_item,parent,false);
                return new EventViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest(){
        eventList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Event item = postSnapshot.getValue(Event.class);
                    suggestList.add(item.getName());
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListEvent(String categoryId){

        //Create query by category Id
        Query searchByName = eventList.orderByChild("menuId").equalTo(categoryId);
        //Create options with query
        FirebaseRecyclerOptions<Event> eventOptions = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(searchByName,Event.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(eventOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final EventViewHolder viewHolder, final int position, @NonNull final Event model) {
                viewHolder.event_name.setText(model.getName());
                viewHolder.event_booking.setText(String.format("%s",model.getBooking().toString()));
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.event_image);

/*                //Quick cart
                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExists =new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                        if(!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));
                        } else {
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                        }

                        Toast.makeText(EventList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });*/


/*
                //Add Favourites
                if(localDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
*/

                //Click to share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //Click to change state of Favourites
/*                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public String toString() {
                        return "$classname{}";
                    }

                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                            localDB.addToFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(EventList.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(EventList.this, ""+model.getName()+" was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

                final Event local = model;
               viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent eventDetail = new Intent (EventList.this,EventDetail.class);
                        eventDetail.putExtra("eventId",adapter.getRef(position).getKey());
                        startActivity(eventDetail);
                    }
                });
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_item,parent,false);
                return new EventViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }
}
