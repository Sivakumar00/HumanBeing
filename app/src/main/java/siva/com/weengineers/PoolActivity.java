package siva.com.weengineers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class PoolActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mPool,mRequest,mPoolUser;
    String current_user=null;
    String list_user_id;
    DatabaseReference mDatabase;
    RecyclerView mPoolList;
    FloatingActionButton add;
    RelativeLayout layout;
    String destination;
    String source;
    String fare;
    String vehicle;
    String phone;
    String name;
    ProgressDialog pDialog;
    Toolbar mToolbar;
    String phone_call;
    PlaceAutocompleteFragment places;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);


        //init
        String list_user_id;
        mToolbar=(Toolbar)findViewById(R.id.pool_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Vehicle Pool available");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        places = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.places_sources);
        layout=(RelativeLayout)findViewById(R.id.layout);
        add=(FloatingActionButton)findViewById(R.id.add_pool);
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mRequest=FirebaseDatabase.getInstance().getReference().child("Request");

        mPoolList=(RecyclerView)findViewById(R.id.pool_list);
        mPoolList.setHasFixedSize(true);
        mPoolList.setLayoutManager(new LinearLayoutManager(this));
        mPool= FirebaseDatabase.getInstance().getReference().child("Poll");
        mPool.keepSynced(true);
        mPoolUser=FirebaseDatabase.getInstance().getReference().child("Poll").child(mAuth.getCurrentUser().getUid());

        current_user=mAuth.getCurrentUser().getUid();
        pDialog=new ProgressDialog(getApplicationContext());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PoolAdd.class));
            }
        });
        LinearLayoutManager linearVertical = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mPoolList.setLayoutManager(linearVertical);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mPoolList.getContext(),
                linearVertical.getOrientation()
        );
        mPoolList.addItemDecoration(mDividerItemDecoration);

    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Pool, UsersViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Pool, UsersViewHolder>(

                Pool.class,
                R.layout.layout_poll_list,
                UsersViewHolder.class,
                mPool
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder friendsViewHolder, final Pool friends, int i) {
                //if(current_user!=getRef(i).getKey())
                list_user_id = getRef(i).getKey();
                Log.e("USER", list_user_id);
                Log.e("Current", current_user);

                    mPool.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                source=dataSnapshot.child("source").getValue().toString();
                                destination = dataSnapshot.child("destination").getValue().toString();
                                vehicle = dataSnapshot.child("vehicle").getValue().toString();
                                friendsViewHolder.setDestination(source,destination);
                                friendsViewHolder.setVehicle(vehicle);

                                phone_call = dataSnapshot.child("phone").getValue().toString();
                                fare=dataSnapshot.child("fare").getValue().toString();
                                friendsViewHolder.setUserFareView(fare);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (list_user_id != current_user) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                                friendsViewHolder.setName(userName);
                                friendsViewHolder.setUserImage(userThumb, getApplicationContext());

                                friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        CharSequence options[] = new CharSequence[]{"Call", "Map location", "Join the Ride"};

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(PoolActivity.this);

                                        builder.setTitle("Select Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //Click Event for each item.
                                                if (i == 0) {
                                                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_call));
                                                    // callIntent.setData(Uri.parse("tel:"+uri));
                                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(callIntent);
                                                }
                                                if (i == 1) {
                                                    Intent intent = new Intent(getApplicationContext(), DriverTrackActivity.class);
                                                    intent.putExtra("user_id", list_user_id);
                                                    startActivity(intent);
                                                }
                                                if (i == 2) {
                                                    if (!current_user.equals(list_user_id)) {
                                                        mRequest.child(list_user_id).child(current_user).child("status").setValue("request");
                                                       // mRequest.child(current_user).child(list_user_id).child("status").setValue("response");
                                                        Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            }

        };

        if(current_user !=list_user_id)
                    mPoolList.setAdapter(friendsRecyclerViewAdapter);


        }





    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView userNameView,userStatusView,userVehicleView,userFareView;
        ImageView image;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){

            userNameView= (TextView) mView.findViewById(R.id.txtName);

            userNameView.setText(name);

        }
        public void setDestination(String source,String dest) {
            userStatusView= (TextView) mView.findViewById(R.id.txtDest);

            userStatusView.setText(source+" to "+dest);
        }
        public void setVehicle(String status) {
            image=(ImageView)mView.findViewById(R.id.vheicle);
            if(status=="Car")
               image.setImageResource(R.drawable.car_option);
            else if(status=="Van")
                image.setImageResource(R.drawable.van_option);
            else
                image.setImageResource(R.drawable.bike_option);
        }
        public void setUserFareView(String fare){
            userFareView=(TextView)mView.findViewById(R.id.txtfare);
            userFareView.setText("Fare: Rs "+fare);
        }

        public void setUserImage(final String thumb_image, final Context ctx){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.pool_pic);

            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.car_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.car_response){
            Intent intent=new Intent(PoolActivity.this,PoolResponse.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

}


