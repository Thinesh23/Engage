package example.com.engage;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;


import example.com.engage.Common.Common;
import example.com.engage.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;


public class SignUp extends AppCompatActivity {
    MaterialEditText edtPhone, edtName, edtPassword, edtSecureCode;
    Button btnSignUp;
    RadioButton rdiOrganizer;
    RadioButton rdiUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtSecureCode = (MaterialEditText) findViewById(R.id.edtSecureCode);
        rdiOrganizer = (RadioButton) findViewById(R.id.rdiOrganizer);
        rdiUser= (RadioButton) findViewById(R.id.rdiUser);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(Common.isConnectedToInternet(getBaseContext())) {


                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please Wait");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone number already exist", Toast.LENGTH_SHORT).show();
                            } else {

                                if(rdiOrganizer.isChecked()) {

                                    mDialog.dismiss();
                                    User user = new User(edtName.getText().toString(),
                                            edtPassword.getText().toString(),
                                            edtSecureCode.getText().toString(),
                                            "true");
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Account Registered", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else if (rdiUser.isChecked()){

                                    mDialog.dismiss();
                                    User user = new User(edtName.getText().toString(),
                                            edtPassword.getText().toString(),
                                            edtSecureCode.getText().toString(),
                                            "false");
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Account Registered", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(SignUp.this,"Please select account type",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignUp.this,"Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

}