package siva.com.weengineers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog mRegProgress,mEmailverification;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailverification=new ProgressDialog(this);
        mToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        mRegProgress=new ProgressDialog(this);

        mDisplayName=(TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail=(TextInputLayout)findViewById(R.id.login_email);
        mPassword=(TextInputLayout)findViewById(R.id.reg_password);
        mCreateBtn=(Button)findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name=mDisplayName.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(display_name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                   // mRegProgress.setTitle("Registering User");
                    //mRegProgress.setMessage("Please wait");
                    //mRegProgress.setCanceledOnTouchOutside(false);
                    //mRegProgress.show();

                    register_user(display_name,email,password);
                }

            }
        });
    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful())
            {

                FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                String uid=current_user.getUid();
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                 //user.sendEmailVerification();
                //mEmailverification.setTitle("Check your email and verify it");
                //mEmailverification.setMessage("Verifying...");
               // mEmailverification.show();
                Boolean emailVerfied=user.isEmailVerified();
                Log.e("Success", String.valueOf(emailVerfied));


                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
              /*  HashMap<String,String> userMap=new HashMap<>();
                userMap.put("name",display_name);
                userMap.put("status","Hi there I'm using We Engineers..!");
                userMap.put("branch","BE / B.Tech");
                userMap.put("study","Not Mentioned");
                userMap.put("work","VIP");
                userMap.put("image","default");
                userMap.put("thumb_image","default");*/
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "Hi..I am an Engineer..!");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);
                    Toast.makeText(getApplicationContext(),"Registered Successfully..!",Toast.LENGTH_LONG).show();

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //  mRegProgress.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }

                    });

                }
                else{
                mRegProgress.hide();
                Toast.makeText(RegisterActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
            }
            }
        });

    }
}
