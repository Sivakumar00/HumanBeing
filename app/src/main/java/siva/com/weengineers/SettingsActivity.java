package siva.com.weengineers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;

import static android.R.attr.bitmap;
import static android.R.attr.elegantTextHeight;
import static java.lang.Math.random;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private TextView mStudy;
    private TextView mWork;
    private TextView mBranch;
    private TextView mFinger;
    private Button mStatusbtn,mImagebtn;
    private ImageView imgSecure;
    private static final int GALLERY_PICK=1;

    //storage

    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
     /*  mDisplayImage=(CircleImageView)findViewById(R.id.settings_image);
        mName=(TextView)findViewById(R.id.settings_display_name);
        mStatus=(TextView)findViewById(R.id.settings_status);
        mStudy=(TextView)findViewById(R.id.settings_study_right);
        mWork=(TextView)findViewById(R.id.settings_work_right);
        mStatusbtn=(Button)findViewById(R.id.settings_status_btn);
        mBranch=(TextView)findViewById(R.id.settings_branch_right);
        mImagebtn=(Button)findViewById(R.id.settings_image_btn);*/
        mImageStorage= FirebaseStorage.getInstance().getReference();
        Paper.init(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //Fierprint API only available on from Android 6.0 (M)
           mFinger.setVisibility(View.INVISIBLE);
       }
        if(Paper.book().read("finger")!=null){
            mFinger.setText("FingerPrint Authentication is ON");
        }
       mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        imgSecure=(ImageView)findViewById(R.id.security);
        mName = (TextView) findViewById(R.id.settings_name);

        mStatus = (TextView) findViewById(R.id.settings_status);
        mStatusbtn = (Button) findViewById(R.id.settings_status_btn);
        mImagebtn = (Button) findViewById(R.id.settings_image_btn);


        imgSecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AuthChooseActivity.class);
                startActivity(intent);
            }
        });
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                /* String name = dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                String study=dataSnapshot.child("study").getValue().toString();
                String work=dataSnapshot.child("work").getValue().toString();
                String branch=dataSnapshot.child("branch").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                mStudy.setText(study);
                mWork.setText(work);

                mBranch.setText(branch);*/

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default")){
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        }
                    });
                }


        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value=mStatus.getText().toString();
                //String branch_value=mBranch.getText().toString();
                //String study_value=mStudy.getText().toString();
                //String work_value=mWork.getText().toString();
                Intent status_intent=new Intent(SettingsActivity.this,StatusActivity.class);
                status_intent.putExtra("status_value",status_value);
                //status_intent.putExtra("branch_value",branch_value);
               // status_intent.putExtra("study_value",study_value);
             //   status_intent.putExtra("work_value",work_value);
                startActivity(status_intent);
            }
        });
    mImagebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)

                    .start(SettingsActivity.this);

          /* Intent galleryIntent=new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent,"select Image"),GALLERY_PICK);*/
        }
    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      /*  if(requestCode==GALLERY_PICK&&resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);
        }*/
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog=new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Processing");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_filePath=new File(resultUri.getPath());
                String current_user_id=mCurrentUser.getUid();

                Bitmap thumb_bitmap= null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    final byte[] thumb_byte=baos.toByteArray();



                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id+".jpg");
                final StorageReference thum_filepath=mImageStorage.child("profile_images").child("thumbs").child(current_user_id+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String download_url=task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask=thum_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot>thumb_task) {
                                    String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()) {

                                        Map update_hashMap=new HashMap();
                                        update_hashMap.put("image",download_url);
                                        update_hashMap.put("thumb_image",thumb_downloadUrl);
                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Successfully uploaded  ", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                    }
                                    else{ Toast.makeText(SettingsActivity.this, "Error in thumbnails  ", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                    }


                                }
                           });
                    }else{
                            Toast.makeText(SettingsActivity.this,"not Working",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
