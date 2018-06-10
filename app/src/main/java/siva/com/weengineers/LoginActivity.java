package siva.com.weengineers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.PrivateKey;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mLogin_btn;
    private SignInButton mGoogleBtn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private FirebaseAuth .AuthStateListener mAuthStateListener;
    private DatabaseReference mUserDatabase;
   // private static final int RC_SIGN_IN=1;
    //private GoogleApiClient mGoogleApiClient;
    //private static final String TAG="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
       // mGoogleBtn=(SignInButton)findViewById(R.id.googlebtn);

    /*    mToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("LOGIN");*/
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mLoginProgress=new ProgressDialog(this);
        mLoginEmail=(TextInputLayout)findViewById(R.id.login_email);
        mLoginPassword=(TextInputLayout)findViewById(R.id.login_password);
        mLogin_btn=(Button)findViewById(R.id.login_btn);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mLoginEmail.getEditText().getText().toString();
                String password=mLoginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password))
                {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please Wait...");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email,password);
                }
            }
        });


    }






    private void loginUser(String email, String password) {
    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                mLoginProgress.dismiss();
                String current_user_id=mAuth.getCurrentUser().getUid();
                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                });


            }
            else
            {   mLoginProgress.hide();
                Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
            }
        }
    });
    }
}
