package siva.com.weengineers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class PoolAdd extends AppCompatActivity implements OnItemSelectedListener {

    Button submit;
    EditText edtName,edtPhone,edtVehicle,edtFare;
    PlaceAutocompleteFragment places,sources;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mLocation;
    String current_user,source_name;
    Toolbar mToolbar;
    RelativeLayout layout;
    String destination;
    String vehicle;
    GeoFire geoFire;
    double src_latitude,src_longitude,des_latitude,des_longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_add);
        //init
        mAuth=FirebaseAuth.getInstance();
       // mLocation=FirebaseDatabase.getInstance().getReference().child("Poll").child(current_user);
       // geoFire=new GeoFire(mLocation);
        current_user=mAuth.getCurrentUser().getUid();
        layout=(RelativeLayout)findViewById(R.id.layout);
        mToolbar=(Toolbar)findViewById(R.id.pool_add_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Vehicle Pool available");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        places = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.places_target);
        sources = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.places_sources);
        layout=(RelativeLayout)findViewById(R.id.layout);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Poll");
        edtFare=(EditText)findViewById(R.id.fare);
        submit=(Button)findViewById(R.id.btnSubmit);

        edtPhone=(EditText)findViewById(R.id.edtPhone);

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination=place.getName().toString();
                LatLng destLatlng=place.getLatLng();
                des_latitude=destLatlng.latitude;
                des_longitude=destLatlng.longitude;
               // Toast.makeText(getApplicationContext(),""+destination,Toast.LENGTH_LONG).show();
               // mDatabase.child(current_user).child("dest").setValue(destination);
            }

            @Override
            public void onError(Status status) {

            }
        });
        sources.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                source_name=place.getName().toString();
                LatLng sourceLatlng=place.getLatLng();
                src_latitude=sourceLatlng.latitude;
                src_longitude=sourceLatlng.longitude;
               //Toast.makeText(getApplicationContext(),""+longitude,Toast.LENGTH_LONG).show();
             /*   geoFire.setLocation(current_user, new GeoLocation(sourceLatlng.latitude, sourceLatlng.longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        Toast.makeText(getApplicationContext(),"Location registered",Toast.LENGTH_LONG).show();

                    }

                });*/

                Toast.makeText(getApplicationContext(),""+source_name,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Status status) {

            }
        });

        //drop down list

        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        dropdown.setOnItemSelectedListener(this);
        String[] items = new String[]{"Car","Bike","Van"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String name=edtName.getText().toString();
                String phone=edtPhone.getText().toString();

                String fareRate=edtFare.getText().toString();




                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(layout, "Please Enter phone", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mDatabase.child(current_user).child("fare").setValue(fareRate);
               // mDatabase.child(current_user).child("name").setValue(name);
                mDatabase.child(current_user).child("phone").setValue(phone);
                mDatabase.child(current_user).child("vehicle").setValue(vehicle);
                mDatabase.child(current_user).child("destination").setValue(destination);
                mDatabase.child(current_user).child("source").setValue(source_name);
                mDatabase.child(current_user).child("source_lat").setValue(src_latitude);
                mDatabase.child(current_user).child("source_long").setValue(src_longitude);
                mDatabase.child(current_user).child("des_lat").setValue(des_latitude);
                mDatabase.child(current_user).child("des_lng").setValue(des_longitude);
                Snackbar.make(layout,"Successfully added",Snackbar.LENGTH_LONG).show();

                Intent intent=new Intent(getApplicationContext(),PoolActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     // vehicle=parent.getSelectedItem().toString();
        vehicle = parent.getItemAtPosition(position).toString();
        Log.e("VEHIcle",vehicle);
        Toast.makeText(getApplicationContext(),""+vehicle,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"Select the vehicle",Toast.LENGTH_LONG).show();
    }
}
