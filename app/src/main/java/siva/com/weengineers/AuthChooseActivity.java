package siva.com.weengineers;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import io.paperdb.Paper;

public class AuthChooseActivity extends AppCompatActivity {
    RadioGroup radio;
    RadioButton radiobtn;
    RadioButton radioFinger;
    Button btnSubmit;
    Context mcontext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_choose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.choose_toolbar);
        toolbar.setTitle("Choose an Authentication");
        setSupportActionBar(toolbar);

        Paper.init(this);
        radioFinger=(RadioButton)findViewById(R.id.fingerprintLock);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)

                radioFinger.setVisibility(View.INVISIBLE);

        }
        //init
        radio = (RadioGroup) findViewById(R.id.radiogroup);
        btnSubmit = (Button) findViewById(R.id.btnChoose);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = radio.getCheckedRadioButtonId();
                radiobtn = (RadioButton) radio.findViewById(selected);

                Paper.book().destroy();
                if (selected==R.id.fingerprintLock) {
                    Paper.book().write("Finger","true");
                    Toast.makeText(getApplicationContext(), "Fingerprint Auth "+Paper.book().read("Finger").toString(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else if (selected==R.id.passcodeLock) {

                    Paper.book().write("Auth","true");
                    Toast.makeText(getApplicationContext(), "Passcode Auth"+Paper.book().read("Auth").toString(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),PinAuthConsole.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(getApplicationContext(),"None",Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();
                    Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
