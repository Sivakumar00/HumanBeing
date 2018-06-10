package siva.com.weengineers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static siva.com.weengineers.R.*;

public class BlogActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
 private DatabaseReference mDatabase,mDatabaseUsers,mDatabaseLike;
    private FirebaseAuth mAuth;
    private Boolean mProcessLike=false;
    private int like_count=0;

    //private FirebaseAuth mAuth;
  //  private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_blog);

        mAuth=FirebaseAuth.getInstance();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mBlogList=(RecyclerView)findViewById(id.blog_list);
        mBlogList.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
      // layoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
       // mBlogList.setLayoutManager(new LinearLayoutManager(this));
       // LinearLayoutManager.setStackFromEnd(true);
        //toolbar

        Toolbar toolbar=(Toolbar)findViewById(id.blog_toolbar);
        toolbar.setTitle("News Feeds");
        setSupportActionBar(toolbar);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                layout.blog_row,
                 BlogViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

               final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);



                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleBlogIntent=new Intent(BlogActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);
                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    mProcessLike=true;

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;
                                        } else {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                            mProcessLike = false;

                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    }
                });

            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        View mView;
        private int likes_count;
        ImageButton mLikeBtn;
        //TextView LikeCount;
        public BlogViewHolder(View itemView) {
            super(itemView);
       mView=itemView;
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
            mLikeBtn=(ImageButton)mView.findViewById(id.like_btn);
        }

        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(drawable.like_blue);
                    }else {
                        mLikeBtn.setImageResource(drawable.like_grey);
                        // likes_count= (int) dataSnapshot.child(post_key).getChildrenCount();
                        //likes_count= (int) dataSnapshot.child(post_key).getChildrenCount();
                        // likes_count--;
                        // LikeCount.setText(likes_count);

                    }}
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title){
            TextView post_title=(TextView)mView.findViewById(id.post_title);
            post_title.setText(title);

        }
        public void setDesc(String desc){
            TextView post_desc=(TextView)mView.findViewById(id.post_desc);
            post_desc.setText(desc);
        }
        public void setUsername(String username){
            TextView post_username=(TextView)mView.findViewById(id.post_username);
            post_username.setText(username);
        }
        public void setImage(final Context ctx,final String image){
            final ImageView post_image=(ImageView)mView.findViewById(id.post_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== id.action_add){
            Intent intent=new Intent(BlogActivity.this,PostActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
