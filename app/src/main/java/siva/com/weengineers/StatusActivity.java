package siva.com.weengineers;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private TextInputLayout mStudy;
    private TextInputLayout mWork;
    private TextInputLayout mBranch;
    private Button mSavebtn;
    //firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    //progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar=(Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       String status_value=getIntent().getStringExtra("status_value");
       String study_value=getIntent().getStringExtra("study_value");
        String branch_value=getIntent().getStringExtra("branch_value");
        String work_value=getIntent().getStringExtra("work_value");

        mStatus=(TextInputLayout)findViewById(R.id.status_input);
      //  mStudy=(TextInputLayout)findViewById(R.id.status_study);
        //mWork=(TextInputLayout)findViewById(R.id.status_work);
        //mBranch=(TextInputLayout)findViewById(R.id.status_branch);
        mSavebtn=(Button)findViewById(R.id.status_save_btn);

       mStatus.getEditText().setText(status_value);
     //   mStudy.getEditText().setText(study_value);
       // mBranch.getEditText().setText(branch_value);
       //mWork.getEditText().setText(work_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please Wait...");
                mProgress.show();

                String status=mStatus.getEditText().getText().toString();
         //       String study=mStudy.getEditText().getText().toString();
           //     String work=mWork.getEditText().getText().toString();
             //   String branch=mBranch.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status);
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
               // mStatusDatabase.child("study").setValue(study);
               // mStatusDatabase.child("branch").setValue(branch);
               /* mStatusDatabase.child("work").setValue(work).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                    mProgress.dismiss();
                    else
                            Toast.makeText(getApplicationContext(),"There is some error",Toast.LENGTH_SHORT).show();

                }
            });*/
        }
    });
    }
}
