package siva.com.weengineers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private CircleImageView mDisplayImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar=(Toolbar)findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Community");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mDisplayImage=(CircleImageView)findViewById(R.id.settings_image);
        mUsersList=(RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
     protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {
            usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumb_image(),getApplicationContext());
                final String user_id=getRef(position).getKey();
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
                }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView userNameView= (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }
        public void setUserStatus(String status) {
            TextView userStatusView= (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public void setUserImage(final String thumb_image, final Context ctx){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            //Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        //    if(thumb_image.equals("default")){
                //Picasso.with(ctx).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

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
    }


