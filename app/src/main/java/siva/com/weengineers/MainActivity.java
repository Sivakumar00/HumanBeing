package siva.com.weengineers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import io.paperdb.Paper;
import siva.com.weengineers.Common.Common;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    Menu menu;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;
    private FloatingActionButton floatingActionButton;

    private TabLayout mTabLayout;
    View background_view;
    boolean checked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background_view=findViewById(R.id.background_view);
        Paper.init(this);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Human Being");

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }


        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabpager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        floatingActionButton=(FloatingActionButton)findViewById(R.id.float_blog);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
                                                {
                                                    public void onClick(View v){
                                                        Intent intent=new Intent(MainActivity.this,BlogActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(),Paper.book().read("Auth").toString(),Toast.LENGTH_SHORT).show();
        // Check if user is signed in (non-null) and update UI accordingly.
       if(Paper.book().read("Finger")!=null&&Paper.book().read("Auth")==null&&!Common.Authenticated){

           Intent intent=new Intent(getApplicationContext(),FingerPrintActivity.class);
           //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);


       }
       else if (Paper.book().read("Auth")!=null&&Paper.book().read("Finger")==null&&!Common.Authenticated){
           Intent intent=new Intent(getApplicationContext(),CalculatorActivity.class);
           startActivity(intent);


       }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu=menu;
       getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.blood){
            Intent intent1 = new Intent(getApplicationContext(), BloodActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }
        if(item.getItemId()==R.id.action) {
            Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        if(item.getItemId() == R.id.main_logout_btn){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId()==R.id.action_pool){

            Intent settingsIntent = new Intent(MainActivity.this, PoolActivity.class);
            startActivity(settingsIntent);
        }
        if(item.getItemId() == R.id.main_settings){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }


        return true;
    }
}




