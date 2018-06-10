package siva.com.weengineers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.R.attr.data;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST=1;
    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private Uri mImageUri=null;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase,mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mProgress=new ProgressDialog(this);
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        //toolbar

        Toolbar toolbar=(Toolbar)findViewById(R.id.post_toolbar);
        toolbar.setTitle("New Post");
        setSupportActionBar(toolbar);

        //id matching
        mPostTitle=(EditText)findViewById(R.id.titlefield);
        mPostDesc=(EditText)findViewById(R.id.descfield);
        mSubmitBtn=(Button)findViewById(R.id.submitbtn);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });



        mSelectImage=(ImageButton) findViewById(R.id.ImageSelect);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(PostActivity.this);*/

                Intent galleryIntent=new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent,"select Image"),GALLERY_REQUEST);
            }
        });

    }

    private void startPosting() {
        mProgress.setMessage("Uploading...");
        mProgress.setTitle("Please wait");
        mProgress.setCanceledOnTouchOutside(false);


        final String title_val=mPostTitle.getText().toString().trim();
        final String desc_val=mPostDesc.getText().toString();
        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&mImageUri!=null){
            mProgress.show();
            StorageReference filepath=mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost=mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PostActivity.this,"Successfully Posted",Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(PostActivity.this,BlogActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mProgress.dismiss();
                }

            });
        } }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
           mSelectImage.setImageURI(mImageUri);
        }

    }
}
