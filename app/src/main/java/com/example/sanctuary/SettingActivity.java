package com.example.sanctuary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingActivity extends AppCompatActivity implements EmergencyContacts.EmergencyContactsDialogListener{
    private Button mSave;
    private EditText mEditTextGname;
    private EditText mEditTextGage;
    RadioGroup mGenderGroup;
    RadioButton mGenderButton;
    RadioGroup mBTGroup;
    RadioButton mBtButton;
    private Button mGuardianContact1;
    private Button mGuardianContact2;
    private Button mGuardianContact3;
    private Button mGuardianContact4;
    private Button mGuardianContact1Delete;
    private Button mGuardianContact2Delete;
    private Button mGuardianContact3Delete;
    private Button mGuardianContact4Delete;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("My Information");
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSave = findViewById(R.id.Gsave);
        mEditTextGname = findViewById(R.id.editTextPersonName);
        mEditTextGage = findViewById(R.id.editTextAge);
        mGenderGroup = findViewById(R.id.RadioGender);
        mBTGroup = findViewById(R.id.RadioBT);
        mGuardianContact1 = findViewById(R.id.GMC1);
        mGuardianContact2 = findViewById(R.id.GMC2);
        mGuardianContact3 = findViewById(R.id.GMC3);
        mGuardianContact4 = findViewById(R.id.GMC4);
        mGuardianContact1Delete = findViewById(R.id.GMC1E);
        mGuardianContact2Delete = findViewById(R.id.GMC2E);
        mGuardianContact3Delete = findViewById(R.id.GMC3E);
        mGuardianContact4Delete = findViewById(R.id.GMC4E);

        String GC1_name = sp.getString("GC1_name", "");
        String GC2_name = sp.getString("GC2_name", "");
        String GC3_name = sp.getString("GC3_name", "");
        String GC4_name = sp.getString("GC4_name", "");

        String GC1_num = sp.getString("GC1_number", "");
        String GC2_num = sp.getString("GC2_number", "");
        String GC3_num = sp.getString("GC3_number", "");
        String GC4_num = sp.getString("GC4_number", "");

        if(!GC1_name.equals("")){
            mGuardianContact1.setText(GC1_name + " - "+GC1_num);
            mGuardianContact1Delete.setVisibility(View.VISIBLE);
        }
        if(!GC2_name.equals("")){
            mGuardianContact2.setText(GC2_name + " - "+GC2_num);
            mGuardianContact2Delete.setVisibility(View.VISIBLE);
        }
        if(!GC3_name.equals("")){
            mGuardianContact3.setText(GC3_name + " - "+GC3_num);
            mGuardianContact3Delete.setVisibility(View.VISIBLE);
        }
        if(!GC4_name.equals("")){
            mGuardianContact4.setText(GC4_name + " - "+GC4_num);
            mGuardianContact4Delete.setVisibility(View.VISIBLE);
        }

        String name = sp.getString("name", "");
        String age = sp.getString("age", "");
        String gender = sp.getString("gender", "");
        String bt = sp.getString("bt", "");
        if(!name.equals(""))
        {
            mEditTextGname.setText(name);
        }
        if(!age.equals(""))
        {
            mEditTextGage.setText(age);
        }
        if(!gender.equals(""))
        {
            if(gender.equals("Male")){
                mGenderButton = findViewById(R.id.radioM);
                mGenderButton.setChecked(true);
            }
            if(gender.equals("Female")){
                mGenderButton = findViewById(R.id.radioF);
                mGenderButton.setChecked(true);
            }
            if(gender.equals("Other")){
                mGenderButton = findViewById(R.id.radioOther);
                mGenderButton.setChecked(true);
            }
        }
        if(!bt.equals(""))
        {
            if(bt.equals("A")){
                mBtButton = findViewById(R.id.radioA);
                mBtButton.setChecked(true);
            }
            if(bt.equals("B")){
                mBtButton = findViewById(R.id.radioB);
                mBtButton.setChecked(true);
            }
            if(bt.equals("O")){
                mBtButton = findViewById(R.id.radioO);
                mBtButton.setChecked(true);
            }
            if(bt.equals("AB")){
                mBtButton = findViewById(R.id.radioAB);
                mBtButton.setChecked(true);
            }
            if(bt.equals("Other")){
                mBtButton = findViewById(R.id.radioOtherBlood);
                mBtButton.setChecked(true);
            }
        }



        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mEditTextGname.getText().toString();
                String age = mEditTextGage.getText().toString();
                int radioGId = mGenderGroup.getCheckedRadioButtonId();
                mGenderButton = findViewById(radioGId);
                int radioBTId = mBTGroup.getCheckedRadioButtonId();
                mBtButton = findViewById(radioBTId);
                String Gender = mGenderButton.getText().toString();
                String BT = mBtButton.getText().toString();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", name);
                editor.putString("age", age);
                editor.putString("gender", Gender);
                editor.putString("bt", BT);
                editor.commit();
            }
        });

        mGuardianContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(11);
            }
        });
        mGuardianContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(12);
            }
        });
        mGuardianContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(13);
            }
        });
        mGuardianContact4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(14);
            }
        });

        mGuardianContact1Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("GC1_name").commit();
                sp.edit().remove("GC1_number").commit();
                mGuardianContact1Delete.setVisibility(View.INVISIBLE);
                mGuardianContact1.setText("None");
            }
        });

        mGuardianContact2Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("GC2_name").commit();
                sp.edit().remove("GC2_number").commit();
                mGuardianContact2Delete.setVisibility(View.INVISIBLE);
                mGuardianContact2.setText("None");
            }
        });

        mGuardianContact3Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("GC3_name").commit();
                sp.edit().remove("GC3_number").commit();
                mGuardianContact3Delete.setVisibility(View.INVISIBLE);
                mGuardianContact3.setText("None");
            }
        });

        mGuardianContact4Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("GC4_name").commit();
                sp.edit().remove("GC4_number").commit();
                mGuardianContact4Delete.setVisibility(View.INVISIBLE);
                mGuardianContact4.setText("None");
            }
        });

    }

    private void openDialog(int which){
        EmergencyContacts ecDiaglog = new EmergencyContacts(which);
        ecDiaglog.show(getSupportFragmentManager(), "ecDiaglog");


    }


    @Override
    public void applyText(String ecName, String ecNumber, int which) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("GC" + String.valueOf(which-10) + "_name", ecName);
        editor.putString("GC" + String.valueOf(which-10) + "_number", ecNumber);
        editor.commit();
        String GC1_name = sp.getString("GC1_name", "");
        String GC2_name = sp.getString("GC2_name", "");
        String GC3_name = sp.getString("GC3_name", "");
        String GC4_name = sp.getString("GC4_name", "");

        String GC1_num = sp.getString("GC1_number", "");
        String GC2_num = sp.getString("GC2_number", "");
        String GC3_num = sp.getString("GC3_number", "");
        String GC4_num = sp.getString("GC4_number", "");

        if(!GC1_name.equals("")){
            mGuardianContact1.setText(GC1_name + " - "+GC1_num);
            mGuardianContact1Delete.setVisibility(View.VISIBLE);
        }
        if(!GC2_name.equals("")){
            mGuardianContact2.setText(GC2_name + " - "+GC2_num);
            mGuardianContact2Delete.setVisibility(View.VISIBLE);
        }
        if(!GC3_name.equals("")){
            mGuardianContact3.setText(GC3_name + " - "+GC3_num);
            mGuardianContact3Delete.setVisibility(View.VISIBLE);
        }
        if(!GC4_name.equals("")){
            mGuardianContact4.setText(GC4_name + " - "+GC4_num);
            mGuardianContact4Delete.setVisibility(View.VISIBLE);
        }


    }
}