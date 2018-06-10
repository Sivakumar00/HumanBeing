package siva.com.weengineers;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import siva.com.weengineers.Common.Common;
import siva.com.weengineers.Common.IGoogleAPI;

public class DriverTrackActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE=7000;
    private static final int PLAY_SERVICES_RES_REQUEST=7001;

     Button btnDest,btnDirection;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    DatabaseReference mLocation;

    private List<LatLng> polyLineList;
    private Marker carMarker;
    private float v;
    private double lat,lng;
    private Handler handler=new Handler();
    private LatLng startPosition,endPosition,currentPosition;
    private int index,next;
    private String destination;
    private PolylineOptions polylineOptions,blackPolylineOptions;
    private Polyline blackPolyLine,greyPolyline;
    String dest;
   private IGoogleAPI mService;



    private static int UPDATE_INTERVAL=5000;
    private static int FASTEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;

    DatabaseReference mDatabase;
    GeoFire geoFire;
    Marker mSource,mDestination;
    SupportMapFragment mapFragment;
    FirebaseAuth mAuth;
    String user_id;


    static String sources_lat,sources_long,dest_lat,dest_long;
    Runnable drawPathRunnable=new Runnable() {
        @Override
        public void run() {
            if(index<polyLineList.size()-1){
                index++;
                next=index+1;
            }
            if(index<polyLineList.size()-1){
                startPosition=polyLineList.get(index);
                endPosition=polyLineList.get(next);

            }
            final ValueAnimator valueAnimator=ValueAnimator.ofFloat(0,1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v=valueAnimator.getAnimatedFraction();
                    lng=v*endPosition.longitude+(1-v)*startPosition.longitude;
                    lat=v*endPosition.latitude+(1-v)*startPosition.latitude;
                    LatLng newPos=new LatLng(lat,lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f,0.5f);
                    carMarker.setRotation(getBearing(startPosition,newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(newPos)
                            .zoom(15.5f)
                            .build()
                    ));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 3000);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_track);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //init

      //  mDatabase= FirebaseDatabase.getInstance().getReference().child("Poll").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location")
        //
        mLocation=FirebaseDatabase.getInstance().getReference().child("Poll");
        btnDest=(Button)findViewById(R.id.dest);
        btnDirection=(Button)findViewById(R.id.direction);
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDirection();
            }
        });
        btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //double src_latitude= Double.parseDouble(dataSnapshot.child("source_lat").getValue().toString());
                        //double src_longitude= Double.parseDouble(dataSnapshot.child("source_long").getValue().toString());
                        //Log.e("LATLNG",""+src_latitude);
                        double des_latitude = Double.parseDouble(dataSnapshot.child("des_lat").getValue().toString());
                        double des_longitude = Double.parseDouble(dataSnapshot.child("des_lng").getValue().toString());

                        mSource = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(des_latitude, des_longitude))
                                .title("Destination"));
                        //move view to position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(des_latitude, des_longitude), 15.0f));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if(getIntent()!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            Log.e("USER_ID",user_id);
        }
        else
            Toast.makeText(getApplicationContext(),""+user_id,Toast.LENGTH_LONG).show();

        mDatabase=FirebaseDatabase.getInstance().getReference().child("Poll");
        //mCurrentLocation=FirebaseDatabase.getInstance().getReference().child("Poll").child(user_id).child("current");

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Snackbar.make(mapFragment.getView(),"Turn ON GPS Location..",Snackbar.LENGTH_LONG).show();
        }
        setUpLocation();
        mService= Common.getGoogleAPI();

    }

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat=Math.abs(startPosition.latitude-endPosition.latitude);
        double lng=Math.abs(startPosition.longitude-endPosition.longitude);

        if(startPosition.latitude<endPosition.latitude&&startPosition.longitude<endPosition.longitude){
            return (float)(Math.toDegrees(Math.atan(lng/lat)));
        }
        else if(startPosition.latitude>=endPosition.latitude&&startPosition.longitude<endPosition.longitude){
            return (float)((90-Math.toDegrees(Math.atan(lng/lat)))+90);
        }
        else if(startPosition.latitude>=endPosition.latitude&&startPosition.longitude>=endPosition.longitude){
            return (float)(Math.toDegrees(Math.atan(lng/lat))+180);
        }
        else if(startPosition.latitude<endPosition.latitude&&startPosition.longitude>=endPosition.longitude){
            return (float)((90-Math.toDegrees(Math.atan(lng/lat)))+270);
        }
        return -1;
    }

//display location

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            return;
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double src_latitude= Double.parseDouble(dataSnapshot.child("source_lat").getValue().toString());
                    double src_longitude= Double.parseDouble(dataSnapshot.child("source_long").getValue().toString());
                    //Log.e("LATLNG",""+src_latitude);
                    //double des_latitude = Double.parseDouble(dataSnapshot.child("des_lat").getValue().toString());
                    //double des_longitude = Double.parseDouble(dataSnapshot.child("des_lng").getValue().toString());

                    mSource = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(src_latitude, src_longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title("Starting Place"));
                    //move view to position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(src_latitude, src_longitude), 15.0f));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    //google docs
    private void getDirection() {

        mLocation.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sources_lat=dataSnapshot.child("source_lat").getValue().toString();
                sources_long=dataSnapshot.child("source_long").getValue().toString();
                dest_lat=dataSnapshot.child("des_lat").getValue().toString();
                dest_long=dataSnapshot.child("des_lng").getValue().toString();
                dest=dataSnapshot.child("destination").getValue().toString().replace(" ","+");

                Toast.makeText(getApplicationContext(),""+dest,Toast.LENGTH_LONG).show();
               currentPosition=new LatLng(Double.parseDouble(sources_lat),Double.parseDouble(sources_long));
                String requestApi=null;
                try{
                    Log.e("LAT",sources_lat+""+sources_long+""+dest);
                    requestApi="https://maps.googleapis.com/maps/api/directions/json?"+"mode=driving&"
                            +"transit_routing_preference=less_driving&"
                            +"origin="+sources_lat+","+sources_long+"&"
                            +"destination="+dest+"&"+"key=AIzaSyC87JjtHbBYvNQVDgthdw_Xj_SaZdU67sk";
                    Log.d("SIVA",requestApi);
                    mService.getPath(requestApi).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject=new JSONObject(response.body().toString());
                                JSONArray jsonArray=jsonObject.getJSONArray("routes");
                                for( int i=0;i<jsonArray.length();i++){
                                    JSONObject route=jsonArray.getJSONObject(i);
                                    JSONObject poly=route.getJSONObject("overview_polyline");
                                    String polyline=poly.getString("points");
                                    polyLineList=decodePoly(polyline);
                                }
                                //adjusting bounds.source: google docs +stack overflow
                                LatLngBounds.Builder builder=new LatLngBounds.Builder();
                                for (LatLng latlng:polyLineList){
                                    builder.include(latlng);
                                }
                                LatLngBounds bounds=builder.build();
                                CameraUpdate mCameraUpdate=CameraUpdateFactory.newLatLngBounds(bounds,2);
                                mMap.animateCamera(mCameraUpdate);
                                polylineOptions=new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline=mMap.addPolyline(polylineOptions);

                                blackPolylineOptions=new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyLine=mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                                        .title("PickUp Location"));

                                //Animation
                                ValueAnimator polyLineAnimator=ValueAnimator.ofInt(0,100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points=greyPolyline.getPoints();
                                        int percentValue=(int)valueAnimator.getAnimatedValue();
                                        int size=points.size();
                                        int newPoints=(int)(size*(percentValue/100.0f));
                                        List<LatLng> p=points.subList(0,newPoints);
                                        blackPolyLine.setPoints(p);

                                    }
                                });
                                polyLineAnimator.start();

                                carMarker=mMap.addMarker(new MarkerOptions().position(currentPosition).flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                handler=new Handler();
                                index=-1;
                                next=1;
                                handler.postDelayed(drawPathRunnable,3000);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DriverTrackActivity.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }
    //github decode poly-json parser
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            //runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else{
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
        }
    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }

    private void createLocationRequest() {
        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!=ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        //mCurrentLocation.child("current_lat").setValue(mLastLocation.getLatitude());
        //mCurrentLocation.child("current_lng").setValue(mLastLocation.getLongitude());
        displayLocation();
    }
}



