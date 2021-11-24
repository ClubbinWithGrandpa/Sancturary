package com.example.sanctuary;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

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
    private Switch switchAllowAudio;
    private Switch switchAllowTracking;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 32;
    private Boolean allowAudio;
    private Boolean allowTracking;
    SharedPreferences sp;
    private int addwhich = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Setting");
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
        switchAllowAudio = findViewById(R.id.switchAudio);
        switchAllowTracking = findViewById(R.id.switchTrack);


        if(sp.getString("allowAudio", "").equals(""))
        {
            allowAudio = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("allowAudio", "false");
            editor.commit();
            switchAllowAudio.setChecked(false);
        }
        else
        {
            if(sp.getString("allowAudio", "").equals("true"))
            {
                allowAudio = true;
                switchAllowAudio.setChecked(true);

            }
        }


        if(sp.getString("allowTracking", "").equals(""))
        {
            MapsActivity.mButtonStartReset.setBackground(getDrawable(R.drawable.buttonshape2));
            allowTracking = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("allowTracking", "false");
            editor.commit();
            switchAllowTracking.setChecked(false);
        }
        else
        {
            if(sp.getString("allowTracking", "").equals("true"))
            {
                MapsActivity.mButtonStartReset.setBackground(getDrawable(R.drawable.buttonshape));
                allowTracking = true;
                switchAllowTracking.setChecked(true);

            }
        }


        switchAllowAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allowAudio = switchAllowAudio.isChecked();
                if(allowAudio == true)
                {
                    if(CheckPermissions())
                    {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("allowAudio", allowAudio.toString());
                        editor.commit();
                        switchAllowAudio.setChecked(allowAudio);
                    }
                    else
                    {
                        RequestPermissions();
                    }
                }
                else
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("allowAudio", allowAudio.toString());
                    editor.commit();
                    switchAllowAudio.setChecked(allowAudio);
                }



            }
        });


        switchAllowTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allowTracking = switchAllowTracking.isChecked();
                if(allowTracking)
                {
                    MapsActivity.mButtonStartReset.setBackground(getDrawable(R.drawable.buttonshape));
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("allowTracking", allowTracking.toString());
                    editor.commit();
                    switchAllowTracking.setChecked(allowTracking);
                }
                else
                {
                    MapsActivity.mButtonStartReset.setBackground(getDrawable(R.drawable.buttonshape2));
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("allowTracking", allowTracking.toString());
                    editor.commit();
                    switchAllowTracking.setChecked(allowTracking);
                }
            }
        });



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
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.05, 20);



        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mSave.startAnimation(myAnim);

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
                Toast.makeText(getApplicationContext(), "Your personal info has been saved!", Toast.LENGTH_SHORT).show();
            }
        });

        mGuardianContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mGuardianContact1.startAnimation(myAnim);

                openDialog(11);
            }
        });
        mGuardianContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mGuardianContact2.startAnimation(myAnim);
                openDialog(12);
            }
        });
        mGuardianContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mGuardianContact3.startAnimation(myAnim);
                openDialog(13);
            }
        });
        mGuardianContact4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mGuardianContact4.startAnimation(myAnim);
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
        addwhich = which;
        EmergencyContacts ecDiaglog = new EmergencyContacts(which);
        ecDiaglog.show(getSupportFragmentManager(), "ecDiaglog");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String num = c.getString(phoneIndex);
                int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
                String name = c.getString(nameIndex);

                SmsManager smsManager = SmsManager.getDefault();
                String messageBody;


                SharedPreferences.Editor editor = sp.edit();

                String nameInfo = sp.getString("name", "");
                if(name.equals(""))
                {
                    messageBody = "You have selected as my guardian contact";
                }
                else
                {
                    messageBody = "You have selected as "+ nameInfo +"'s guardian contact";
                }

                smsManager.sendTextMessage(num, null, messageBody, null, null);


                editor.putString("GC" + String.valueOf(addwhich-10) + "_name", name);
                editor.putString("GC" + String.valueOf(addwhich-10) + "_number", num);
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

    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(SettingActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE)
        {
            if (grantResults.length> 0) {
                boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("allowAudio", "true");
                    editor.commit();
                    switchAllowAudio.setChecked(allowAudio);
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                    switchAllowAudio.setChecked(false);
                }
            }
        }
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