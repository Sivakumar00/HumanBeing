package siva.com.weengineers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import io.paperdb.Paper;
import siva.com.weengineers.Common.Common;

public class CalculatorActivity extends AppCompatActivity {

    private double valueOne = Double.NaN;
    private double valueTwo;
    SharedPreferences myCred;

    Button one,two,three,four,five,six,seven,eight,nine,zero,cancel,add,sub,mul,div,equal;
    EditText edtText;
    TextView infoText;
    private DecimalFormat decimalFormat;
    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';
    String ORG_PASS="";
    String PASS="";

    private char CURRENT_ACTION;
    private double result=0;


    String user;
    String password=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Paper.init(this);

        decimalFormat = new DecimalFormat("#.##########");

        one=(Button)findViewById(R.id.buttonOne);
        three=(Button)findViewById(R.id.buttonThree);
        two=(Button)findViewById(R.id.buttonTwo);
        four=(Button)findViewById(R.id.buttonFour);
        five=(Button)findViewById(R.id.buttonFive);
        six=(Button)findViewById(R.id.buttonSix);
        seven=(Button)findViewById(R.id.buttonSeven);
        eight=(Button)findViewById(R.id.buttonEight);
        nine=(Button)findViewById(R.id.buttonNine);
        zero=(Button)findViewById(R.id.buttonZero);
        cancel=(Button)findViewById(R.id.buttonClear);
        add=(Button)findViewById(R.id.buttonAdd);
        sub=(Button)findViewById(R.id.buttonSubtract);
        mul=(Button)findViewById(R.id.buttonMultiply);
        div=(Button)findViewById(R.id.buttonDivide);
        equal=(Button)findViewById(R.id.buttonEqual);

        infoText=(TextView) findViewById(R.id.infoTextView);
        edtText=(EditText)findViewById(R.id.editText);

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtText.setText(edtText.getText() + "0");
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtText.setText(edtText.getText() + "1");
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "2");
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "3");
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "4");
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "5");
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "6");
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "7");
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "8");
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtText.setText(edtText.getText() + "9");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeCalculation();
                CURRENT_ACTION = ADDITION;
                infoText.setText(decimalFormat.format(valueOne) + "+");
                edtText.setText(null);
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeCalculation();
                CURRENT_ACTION = SUBTRACTION;
                infoText.setText(decimalFormat.format(valueOne) + "-");
                edtText.setText(null);
            }
        });

        mul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeCalculation();
                CURRENT_ACTION = MULTIPLICATION;
                infoText.setText(decimalFormat.format(valueOne) + "*");
                edtText.setText(null);
            }
        });

        div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeCalculation();
                CURRENT_ACTION = DIVISION;
                infoText.setText(decimalFormat.format(valueOne) + "/");
                edtText.setText(null);
            }
        });

        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeCalculation();
                ORG_PASS=Paper.book().read("Auth");
                PASS=infoText.getText().toString()+decimalFormat.format(valueTwo) + " = " + decimalFormat.format(valueOne);
               // Toast.makeText(getApplicationContext(),PASS,Toast.LENGTH_SHORT).show();
                if(PASS.equals(ORG_PASS)){
                    // Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    Common.Authenticated=true;
                    startActivity(intent);
                    finish();
                    Log.e("CHECK","SUCCESS");
                }
                infoText.setText(infoText.getText().toString() +
                        decimalFormat.format(valueTwo) + " = " + decimalFormat.format(valueOne));
                valueOne = Double.NaN;
                CURRENT_ACTION = '0';

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtText.getText().length() > 0) {
                    CharSequence currentText = edtText.getText();
                    edtText.setText(currentText.subSequence(0, currentText.length()-1));
                }
                else {
                    valueOne = Double.NaN;
                    valueTwo = Double.NaN;
                    edtText.setText("");
                    infoText.setText("");
                }
            }
        });
    }

    private void computeCalculation() {
        if(!Double.isNaN(valueOne)) {
            valueTwo = Double.parseDouble(edtText.getText().toString());
            edtText.setText(null);

            if(CURRENT_ACTION == ADDITION)
                valueOne = this.valueOne + valueTwo;
            else if(CURRENT_ACTION == SUBTRACTION)
                valueOne = this.valueOne - valueTwo;
            else if(CURRENT_ACTION == MULTIPLICATION)
                valueOne = this.valueOne * valueTwo;
            else if(CURRENT_ACTION == DIVISION)
                valueOne = this.valueOne / valueTwo;
        }
        else {
            try {
                valueOne = Double.parseDouble(edtText.getText().toString());
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"wrong Input",Toast.LENGTH_SHORT).show();
            }
        }
    }
}