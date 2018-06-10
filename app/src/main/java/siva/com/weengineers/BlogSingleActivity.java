package siva.com.weengineers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {
        private String mPost_key=null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button mSingleRemoveBtn;
    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle,mBlogSingleDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        mAuth=FirebaseAuth.getInstance();
        mSingleRemoveBtn=(Button)findViewById(R.id.remove_btn);
      //  mBlogSingleDesc=(TextView)findViewById(R.id.blog_single_desc);
        mBlogSingleImage=(ImageView)findViewById(R.id.blog_single_image);
        //mBlogSingleTitle=(TextView)findViewById(R.id.blog_single_title);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mPost_key=getIntent().getExtras().getString("blog_id");
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               // String post_title= (String) dataSnapshot.child("title").getValue();
              //  String post_desc=(String)dataSnapshot.child("desc").getValue();
                final String post_image=(String)dataSnapshot.child("image").getValue();
                String post_uid=(String )dataSnapshot.child("uid").getValue();

             //   mBlogSingleTitle.setText(post_title);
               // mBlogSingleDesc.setText(post_desc);
                Picasso.with(BlogSingleActivity.this).load(post_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.add_btn).into(mBlogSingleImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(BlogSingleActivity.this).load(post_image).into(mBlogSingleImage);
                    }
                });

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).removeValue();
                Intent mainIntent = new Intent(BlogSingleActivity.this,BlogActivity.class);

                startActivity(mainIntent);
                finish();
            }
        });

    }
}
