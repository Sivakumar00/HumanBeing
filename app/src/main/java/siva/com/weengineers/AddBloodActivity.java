package siva.com.weengineers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBloodActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText edtBlood,edtAddress,edtPhone;
    String name,address,phone,blood;
    DatabaseReference mDatabase,mUsers;
    String current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blood);
        edtAddress=(EditText)findViewById(R.id.edtAddress);
        edtBlood=(EditText)findViewById(R.id.edtBlood);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        current_user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("blood_donor");
        mUsers=FirebaseDatabase.getInstance().getReference().child("Users");
         btnSubmit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         name=dataSnapshot.child("name").getValue().toString();
                        mDatabase.child(current_user).child("name").setValue(name);

                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });

                 address=edtAddress.getText().toString();
                 phone=edtPhone.getText().toString();
                 blood=edtBlood.getText().toString();

                 mDatabase.child(current_user).child("address").setValue(address);
                 mDatabase.child(current_user).child("phone").setValue(phone);
                 mDatabase.child(current_user).child("blood").setValue(blood);

             }
         });


    }
}
