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
import android.widget.RadioGroup;
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
    MaterialEditText edtPhone, edtFirstName, edtLastName, edtEmail, edtPassword, edtSecureCode, edtCompanyName;
    Button btnSignUp;
    RadioGroup rdiUserType;
    String userOrganizer="";
    long userId=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtFirstName = (MaterialEditText) findViewById(R.id.edtFirstName);
        edtLastName = (MaterialEditText) findViewById(R.id.edtLastName);
        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtSecureCode = (MaterialEditText) findViewById(R.id.edtSecureCode);
        edtCompanyName = (MaterialEditText) findViewById(R.id.edtCompanyName);
        rdiUserType = (RadioGroup) findViewById(R.id.user_type);

        rdiUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdiOrganizer){
                    edtCompanyName.setEnabled(true);
                    userOrganizer="true";
                } else if (checkedId == R.id.rdiUser){
                    userOrganizer="false";
                    edtCompanyName.setText("Null");
                    edtCompanyName.setEnabled(false);
                }
            }
        });


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
                            } else if(!edtFirstName.getText().toString().isEmpty() ||
                                    !edtLastName.getText().toString().isEmpty() ||
                                    !edtCompanyName.getText().toString().isEmpty() ||
                                    !edtEmail.getText().toString().isEmpty() ||
                                    !edtPhone.getText().toString().isEmpty() ||
                                    !edtPassword.getText().toString().isEmpty() ||
                                    !edtSecureCode.getText().toString().isEmpty()){

                                mDialog.dismiss();
                                User user = new User(edtFirstName.getText().toString(),
                                        edtLastName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtEmail.getText().toString(),
                                        edtSecureCode.getText().toString(),
                                        userOrganizer,
                                        edtCompanyName.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "User Account Registered", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(SignUp.this, "Please fill up all the details", Toast.LENGTH_SHORT).show();
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