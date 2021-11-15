package com.example.sanctuary;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sanctuary.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener,
        EmergencyContacts.EmergencyContactsDialogListener, AdapterView.OnItemSelectedListener {
    final long SEND_INTERVAL = 5000;

    private static final int REQUEST_CALL = 1;
    private TextView mTextViewCountDown;
    TextView Tracking;
    private Button mButtonStartReset;
    private Button mEmergencyCall;
    private Button mEmergencyCall1;
    private Button mEmergencyCall2;
    private Button mEmergencyCall3;
    private Button mEmergencyCall1_notNull;
    private Button mEmergencyCall2_notNull;
    private Button mEmergencyCall3_notNull;
    private Button mDEC1;
    private Button mDEC2;
    private Button mDEC3;
    private Button mAlarm;
    private Button mSetting;
    private Button mShowPath;
    private boolean isShowingPath;
    boolean isRecording;
    private Spinner mModeSpinner;
    private String distanceMode;
    Handler handler;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private boolean mEmergencyContactsRunning;
    private long mTimeLeftMillis ;
    private TextView mGuardianModeOn;
    private boolean alarmPlaying;
    SharedPreferences sp;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    EditText editText;
    ArrayList<LatLng> guardianLocations;
    ArrayList<Double> LocationServiceArray;
    Polyline routePolyline;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 32;
    private int color;


    private int addwhich = 0;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 76;
    private static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        
        handler = new Handler();

        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }




        final MediaPlayer alarmSound = MediaPlayer.create(this, R.raw.alarm);
        alarmSound.setLooping(true);

        distanceMode = "driving";
        alarmPlaying = false;
        isRecording = false;
        mEmergencyContactsRunning = false;
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mTextViewCountDown = findViewById(R.id.textView_countdown);
        mButtonStartReset = findViewById(R.id.Guardian);
        mGuardianModeOn = findViewById(R.id.GuardianModeAct);
        mEmergencyCall = findViewById(R.id.emergencyCall);
        mEmergencyCall1 = findViewById(R.id.EC1);
        mEmergencyCall2 = findViewById(R.id.EC2);
        mEmergencyCall3 = findViewById(R.id.EC3);
        mEmergencyCall1_notNull = findViewById(R.id.EC1_notnull);
        mEmergencyCall2_notNull = findViewById(R.id.EC2_notnull);
        mEmergencyCall3_notNull = findViewById(R.id.EC3_notnull);
        mSetting = findViewById(R.id.setting);
        mDEC1 = findViewById(R.id.DEC1);
        mDEC2 = findViewById(R.id.DEC2);
        mDEC3 = findViewById(R.id.DEC3);
        mAlarm = findViewById(R.id.alarm);
        Tracking = findViewById(R.id.guardianModeAct3);
        mShowPath = findViewById(R.id.showPath);
        isShowingPath = false;
        mModeSpinner = findViewById(R.id.mode);
        color = 2;

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.modes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModeSpinner.setAdapter(adapter);
        mModeSpinner.setOnItemSelectedListener(this);

        if(isLocationServiceRunning())
        {
            mButtonStartReset.setText("Stop");

            mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_googred));


            mTextViewCountDown.setVisibility(View.VISIBLE);
            mGuardianModeOn.setVisibility(View.VISIBLE);
            Tracking.setVisibility(View.VISIBLE);

        }

        mShowPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowingPath)
                {
                    guardianLocations = new ArrayList<>();
                    handleGetDirectionsResult(guardianLocations);
                    isShowingPath = false;
                }
                else
                {
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    LocationServiceArray = tinydb.getListDouble("GuadianLocations");
                    guardianLocations = new ArrayList<>();
                    for (int i = 0; i < LocationServiceArray.size(); i = i + 2)
                    {
                        double latitude = LocationServiceArray.get(i);
                        double longitude = LocationServiceArray.get(i+1);
                        LatLng cur = new LatLng(latitude, longitude);
                        guardianLocations.add(cur);

                    }
                    handleGetDirectionsResult(guardianLocations);
                    isShowingPath = true;
                }

            }
        });
        mModeSpinner.setVisibility(View.VISIBLE);
        editText = findViewById(R.id.edit_text);
        Places.initialize(getApplicationContext(), "AIzaSyBg_acHBZAQMZPuNdxH4pz_zt-AXUb_FZw");
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<com.google.android.libraries.places.api.model.Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapsActivity.this);
                startActivityForResult(intent, 100);
            }
        });


        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                }
                else
                {
                    Intent intent = new Intent(MapsActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            }
        });


        createNofiticationChannel();



        mAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(!alarmPlaying)
                {
                    alarmSound.start();
                    alarmPlaying = true;
                }else
                {
                    alarmSound.pause();
                    alarmPlaying = false;
                }
            }
        });



        mDEC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("EC1_name").commit();
                sp.edit().remove("EC1_number").commit();
                mDEC1.setVisibility(View.INVISIBLE);
                mEmergencyCall1.setVisibility(View.VISIBLE);
                mEmergencyCall1_notNull.setVisibility(View.INVISIBLE);
            }
        });

        mDEC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("EC2_name").commit();
                sp.edit().remove("EC2_number").commit();
                mDEC2.setVisibility(View.INVISIBLE);
                mEmergencyCall2.setVisibility(View.VISIBLE);
                mEmergencyCall2_notNull.setVisibility(View.INVISIBLE);
            }
        });

        mDEC3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("EC3_name").commit();
                sp.edit().remove("EC3_number").commit();
                mDEC3.setVisibility(View.INVISIBLE);
                mEmergencyCall3.setVisibility(View.VISIBLE);
                mEmergencyCall3_notNull.setVisibility(View.INVISIBLE);
            }
        });

        mEmergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mEmergencyContactsRunning)
                {


                    mEmergencyContactsRunning = true;
                    mEmergencyCall1.setVisibility(View.VISIBLE);
                    mEmergencyCall2.setVisibility(View.VISIBLE);
                    mEmergencyCall3.setVisibility(View.VISIBLE);
                    String EC1_name = sp.getString("EC1_name", "");
                    String EC2_name = sp.getString("EC2_name", "");
                    String EC3_name = sp.getString("EC3_name", "");
                    if(!EC1_name.equals("")){
                        mEmergencyCall1_notNull.setVisibility(View.VISIBLE);
                        mEmergencyCall1_notNull.setText(EC1_name);
                        mEmergencyCall1.setVisibility(View.INVISIBLE);
                        mDEC1.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mEmergencyCall1.setVisibility(View.VISIBLE);
                        mDEC1.setVisibility(View.INVISIBLE);
                        mEmergencyCall1_notNull.setVisibility(View.INVISIBLE);
                    }

                    if(!EC2_name.equals("")){
                        mEmergencyCall2_notNull.setVisibility(View.VISIBLE);
                        mEmergencyCall2_notNull.setText(EC2_name);
                        mEmergencyCall2.setVisibility(View.INVISIBLE);
                        mDEC2.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mEmergencyCall2.setVisibility(View.VISIBLE);
                        mDEC2.setVisibility(View.INVISIBLE);
                        mEmergencyCall2_notNull.setVisibility(View.INVISIBLE);
                    }

                    if(!EC3_name.equals("")){
                        mEmergencyCall3_notNull.setVisibility(View.VISIBLE);
                        mEmergencyCall3_notNull.setText(EC1_name);
                        mEmergencyCall3.setVisibility(View.INVISIBLE);
                        mDEC3.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mEmergencyCall3.setVisibility(View.VISIBLE);
                        mDEC3.setVisibility(View.INVISIBLE);
                        mEmergencyCall3_notNull.setVisibility(View.INVISIBLE);
                    }

                }else{
                    mDEC1.setVisibility(View.INVISIBLE);
                    mDEC2.setVisibility(View.INVISIBLE);
                    mDEC3.setVisibility(View.INVISIBLE);
                    mEmergencyCall1.setVisibility(View.INVISIBLE);
                    mEmergencyCall2.setVisibility(View.INVISIBLE);
                    mEmergencyCall3.setVisibility(View.INVISIBLE);
                    mEmergencyCall1_notNull.setVisibility(View.INVISIBLE);
                    mEmergencyCall2_notNull.setVisibility(View.INVISIBLE);
                    mEmergencyCall3_notNull.setVisibility(View.INVISIBLE);
                    mEmergencyContactsRunning = false;
                }

            }
        });

        mEmergencyCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(1);
            }
        });

        mEmergencyCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(2);
            }
        });

        mEmergencyCall3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openDialog(3);
            }
        });


        mEmergencyCall1_notNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EC1_number = sp.getString("EC1_number", "");
                if(EC1_number != "")
                {
                    makePhoneCall(EC1_number);
                }

            }
        });

        mEmergencyCall2_notNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EC2_number = sp.getString("EC2_number", "");
                if(EC2_number != "")
                {
                    makePhoneCall(EC2_number);
                }
            }
        });


        mEmergencyCall3_notNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EC3_number = sp.getString("EC3_number", "");
                if(EC3_number != "")
                {
                    makePhoneCall(EC3_number);
                }
            }
        });


        mButtonStartReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Please complete your personal info in settings please.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    )
                    {
                        ActivityCompat.requestPermissions(
                                MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                REQUEST_BACKGROUND_LOCATION_PERMISSION
                        );
                    }else
                    {
                        if(CheckPermissions())
                        {
                            if(mTimerRunning || isLocationServiceRunning()) {
                                resetTimer();
                                mTextViewCountDown.setVisibility(View.INVISIBLE);
                                mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_lightgreen));
                                mGuardianModeOn.setVisibility(View.INVISIBLE);
                            }
                            else {
                                DialogFragment timePicker = new TimePickerFragment();
                                timePicker.show(getSupportFragmentManager(), "time picker");
                            }
                        }
                        else
                        {
                            RequestPermissions();
                        }

                    }


                }




            }
        });
        updateCountdownText();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void DisplayTrack(String sSource, String sDestination)
    {
        try{
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" +sSource + "/" +sDestination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    public void startSharingLocation(){
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_BACKGROUND_LOCATION_PERMISSION
            );
        }

        else{
            startLocationService();
        }
    }

    public void stopSharingLocation()
    {
        stopLocationService();


    }


    private boolean isLocationServiceRunning(){
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private void startLocationService(){
        if(!isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);

            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }




    public Runnable runnable = new Runnable() {
        public void run() {


            fusedLocationProviderClient
                    .getLastLocation().
                    addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location != null)
                            {
                                LatLng cur = new LatLng(location.getLatitude(), location.getLongitude());
                                guardianLocations.add(cur);
                                handleGetDirectionsResult(guardianLocations);

                                String GC1_name = sp.getString("GC1_name", "");
                                String GC2_name = sp.getString("GC2_name", "");
                                String GC3_name = sp.getString("GC3_name", "");
                                String GC4_name = sp.getString("GC4_name", "");

                                String GC1_num = sp.getString("GC1_number", "");
                                String GC2_num = sp.getString("GC2_number", "");
                                String GC3_num = sp.getString("GC3_number", "");
                                String GC4_num = sp.getString("GC4_number", "");

                                SmsManager smsManager = SmsManager.getDefault();

                                StringBuffer smsBody = new StringBuffer();
                                smsBody.append("http://maps.google.com?q=");
                                smsBody.append(location.getLatitude());
                                smsBody.append(",");
                                smsBody.append(location.getLongitude());

                                if(!GC1_name.equals("")){
                                    smsManager.sendTextMessage(GC1_num, null, smsBody.toString(), null, null);
                                }
                                if(!GC2_name.equals("")){
                                    smsManager.sendTextMessage(GC2_num, null, smsBody.toString(), null, null);
                                }
                                if(!GC3_name.equals("")){
                                    smsManager.sendTextMessage(GC3_num, null, smsBody.toString(), null, null);
                                }
                                if(!GC4_name.equals("")){
                                    smsManager.sendTextMessage(GC4_num, null, smsBody.toString(), null, null);
                                }
                            }



                        }
                    });
            handler.postDelayed(runnable, SEND_INTERVAL);
        }};


    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place =  Autocomplete.getPlaceFromIntent(data);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    String srcLat = String.valueOf(location.getLatitude());
                    String srcLong = String.valueOf(location.getLongitude());

                    String desLat = String.valueOf(place.getLatLng().latitude);
                    String desLong = String.valueOf(place.getLatLng().longitude);
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                Request request = new Request.Builder()
                                //.url("https://maps.googleapis.com/maps/api/distancematrix/json?origins=Scarborough%20Town%20Centre%2C%20DC&destinations=UTSC%2C%20NY&units=imperial&mode=transit&key=AIzaSyBg_acHBZAQMZPuNdxH4pz_zt-AXUb_FZw")
                                        .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + srcLat+   "%2C" + srcLong + "&destinations=" + desLat
                                                + "%2C" + desLong     + "&units=imperial&mode="+ distanceMode +"&key=AIzaSyBg_acHBZAQMZPuNdxH4pz_zt-AXUb_FZw")

                                        .method("GET", null)
                                        .build();
                                Response response = client.newCall(request).execute();
                                String jsonData = response.body().string();
                                JSONObject Jobject = new JSONObject(jsonData);
                                JSONArray Jarray = Jobject.getJSONArray("rows");
                                JSONObject object     = Jarray.getJSONObject(0);
                                object.getJSONArray("elements");
                                String distance = object.getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("text").toString();
                                String duration = object.getJSONArray("elements").getJSONObject(0).getJSONObject("duration").get("text").toString();

                                String mode;
                                if(distanceMode.equals("driving"))
                                {
                                    mode = "Driving";
                                }else if(distanceMode.equals("transit"))
                                {
                                    mode = "Transit";
                                }
                                else
                                {
                                    mode = "Walking";
                                }


                                showToast(mode + " Distance: " + distance +"\n" + mode+" ETA: " + duration);
                                showToast("Marker -> botton-right icon to get directions from Google Maps");




                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                }
            });






        }else if (requestCode == 100 && resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == -1)
        {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String num = c.getString(phoneIndex);
                int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
                String name = c.getString(nameIndex);







                SharedPreferences.Editor editor = sp.edit();

                editor.putString("EC"+ String.valueOf(addwhich)+ "_name", name);
                editor.putString("EC"+ String.valueOf(addwhich)+ "_number", num);
                editor.commit();
                String EC1_name = sp.getString("EC1_name", "");
                String EC2_name = sp.getString("EC2_name", "");
                String EC3_name = sp.getString("EC3_name", "");
                if(!EC1_name.equals("")){
                    mEmergencyCall1.setVisibility(View.INVISIBLE);
                    mEmergencyCall1_notNull.setVisibility(View.VISIBLE);
                    mEmergencyCall1_notNull.setText(EC1_name);
                    mDEC1.setVisibility(View.VISIBLE);
                }
                if(!EC2_name.equals("")){
                    mEmergencyCall2.setVisibility(View.INVISIBLE);
                    mEmergencyCall2_notNull.setVisibility(View.VISIBLE);
                    mEmergencyCall2_notNull.setText(EC2_name);
                    mDEC2.setVisibility(View.VISIBLE);
                }
                if(!EC3_name.equals("")){
                    mEmergencyCall3.setVisibility(View.INVISIBLE);
                    mEmergencyCall3_notNull.setVisibility(View.VISIBLE);
                    mEmergencyCall3_notNull.setText(EC3_name);
                    mDEC3.setVisibility(View.VISIBLE);
                }
            }




        }

    }

    private void makePhoneCall(String number){
        if(ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }else{
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Phone call permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 2){
            if( grantResults.length>0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(), "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == 44){
            if(grantResults.length>0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(), "Location permission denied", Toast.LENGTH_SHORT).show();

            }
            else if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(), "Please restart your app to enable location service", Toast.LENGTH_SHORT).show();

            }
        }else if (requestCode == REQUEST_AUDIO_PERMISSION_CODE)
        {
            if (grantResults.length> 0) {
                boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startLocationService();
        }else if (requestCode == REQUEST_CODE_LOCATION_PERMISSION)
        {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        int c_h = c.get(Calendar.HOUR_OF_DAY);
        int c_m = c.get(Calendar.MINUTE);
        int hours;
        int minutes;
        if (hourOfDay < c_h) {
            hours = hourOfDay + 24 - c_h;
        }else {
            hours = hourOfDay - c_h;
        }
        if (minute < c_m) {
            minutes = minute + 24 - c_m;
        }else {
            minutes = minute - c_m;
        }
        mTimeLeftMillis = hours*60*60*1000 + minutes*60*1000;
        guardianLocations = new ArrayList<LatLng>();
        startTimer();
        mGuardianModeOn.setVisibility(View.VISIBLE);
        mTextViewCountDown.setVisibility(View.VISIBLE);

        if(mTimeLeftMillis/1000 <= 299 && mTimeLeftMillis/1000 > 59)
        {
            mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_yellow));
        }else if(mTimeLeftMillis/1000 <= 59)
        {
            mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_googred));
        }
        else
        {
            mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_lightgreen));
        }



        if(!CheckPermissions()) {
            RequestPermissions();
        }

    }

    private void openDialog(int which){
        addwhich = which;
        EmergencyContacts ecDiaglog = new EmergencyContacts(which);
        ecDiaglog.show(getSupportFragmentManager(), "ecDiaglog");
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftMillis = millisUntilFinished;
                if(millisUntilFinished/1000 == 299)
                {

                    mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_yellow));
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this, "Sanctuary")
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Guardian mode is on")
                            .setContentText("Guardian Mode will be activated in 5 minutes!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);
                    notificationManager.notify(100, builder.build());
                }
                else if(millisUntilFinished/1000 == 59)
                {
                    mTextViewCountDown.setTextColor(getResources().getColor(R.color.quantum_googred));
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this, "Sanctuary")
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Guardian mode is on")
                            .setContentText("Guardian Mode will be activated in 1 minutes!")
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);
                    notificationManager.notify(100, builder.build());
                }
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                startSharingLocation();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this, "Sanctuary")
                        .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                        .setContentTitle("Guardian mode is on")
                        .setContentText("Guardian Mode activated!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);
                notificationManager.notify(100, builder.build());

                sendMessages();

                if(CheckPermissions()) {
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    Context ctx = getApplicationContext();
                    File audioDir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos");
                    Log.d("mt",ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
                    audioDir.mkdirs();
                    String audioDirPath = audioDir.getAbsolutePath();
                    File recordingFile = new File(audioDirPath + "/" + "a" + ".m4a");

                    Log.d("myTag", recordingFile.getAbsolutePath());
                    mRecorder.setOutputFile(recordingFile.getAbsolutePath());
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                    mRecorder.start();
                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
                    isRecording = true;
                    Tracking.setVisibility(View.VISIBLE);

                }
                else
                {

                }



            }
        }.start();
        mTimerRunning = true;





        mButtonStartReset.setText("Stop");
    }
    private void resetTimer() {
        stopSharingLocation();
        Tracking.setVisibility(View.INVISIBLE);
        if(mRecorder != null)
        {
            mRecorder.stop();
            mRecorder.release();
        }
        mRecorder = null;
        if(isRecording)
        {
            Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();

            isRecording = false;
        }
        if(mCountDownTimer != null)
        {
            mCountDownTimer.cancel();
        }
        mTimerRunning = false;
        mTimeLeftMillis = 0;
        updateCountdownText();
        mButtonStartReset.setText("Guardian");

    }

    private void updateCountdownText(){
        int hours = (int) mTimeLeftMillis/1000/60/60;
        int minutes = (int) mTimeLeftMillis/1000%3600/60;
        int seconds = (int) mTimeLeftMillis/1000%60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);

    }

    private void sendMessages(){
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendActualMessages();
        }
        else{
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }

    }
    private void sendActualMessages() {
        String GC1_name = sp.getString("GC1_name", "");
        String GC2_name = sp.getString("GC2_name", "");
        String GC3_name = sp.getString("GC3_name", "");
        String GC4_name = sp.getString("GC4_name", "");

        String GC1_num = sp.getString("GC1_number", "");
        String GC2_num = sp.getString("GC2_number", "");
        String GC3_num = sp.getString("GC3_number", "");
        String GC4_num = sp.getString("GC4_number", "");

        String name = sp.getString("name", "");
        String age = sp.getString("age", "");
        String gender = sp.getString("gender", "");
        String bt = sp.getString("bt", "");
        String helpMessage = "I am in danger, please come and help me! \nI am also providing my "+
                "personal information and current locations update every 5 min until Guardian mode is off. ";
        String info_loc = "\nName: " + name + "\n" + "Age: " + age + "\n" + "Gender: " + gender + "\n" + "Blood type: " + bt + "\n";

        SmsManager smsManager = SmsManager.getDefault();

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                int count = 0;
                if(location != null){
                    if(!GC1_name.equals("")){
                        smsManager.sendTextMessage(GC1_num, null, helpMessage, null, null);
                        smsManager.sendTextMessage(GC1_num, null, info_loc, null, null);
                        count++;
                    }
                    if(!GC2_name.equals("")){
                        smsManager.sendTextMessage(GC2_num, null, helpMessage, null, null);
                        smsManager.sendTextMessage(GC2_num, null, info_loc, null, null);
                        count++;
                    }
                    if(!GC3_name.equals("")){
                        smsManager.sendTextMessage(GC3_num, null, helpMessage, null, null);
                        smsManager.sendTextMessage(GC3_num, null, info_loc, null, null);
                        count++;
                    }
                    if(!GC4_name.equals("")){
                        smsManager.sendTextMessage(GC4_num, null, helpMessage, null, null);
                        smsManager.sendTextMessage(GC4_num, null, info_loc, null, null);
                        count++;
                    }
                    Toast.makeText(getApplicationContext(), "SMS sent to all " +String.valueOf(count) + " of your guardian contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

        mMap = googleMap;
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        else
        {
            fusedLocationProviderClient
                    .getLastLocation().
                    addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location != null)
                            {
                                LatLng cur = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, 12.0f));
                            }

                        }
                    });
            mMap.setMyLocationEnabled(true);
        }


    }

    @Override
    public void applyText(String ecName, String ecNumber, int which) {
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("EC"+ String.valueOf(which)+ "_name", ecName);
        editor.putString("EC"+ String.valueOf(which)+ "_number", ecNumber);
        editor.commit();
        String EC1_name = sp.getString("EC1_name", "");
        String EC2_name = sp.getString("EC2_name", "");
        String EC3_name = sp.getString("EC3_name", "");
        if(!EC1_name.equals("")){
            mEmergencyCall1.setVisibility(View.INVISIBLE);
            mEmergencyCall1_notNull.setVisibility(View.VISIBLE);
            mEmergencyCall1_notNull.setText(EC1_name);
            mDEC1.setVisibility(View.VISIBLE);
        }
        if(!EC2_name.equals("")){
            mEmergencyCall2.setVisibility(View.INVISIBLE);
            mEmergencyCall2_notNull.setVisibility(View.VISIBLE);
            mEmergencyCall2_notNull.setText(EC2_name);
            mDEC2.setVisibility(View.VISIBLE);
        }
        if(!EC3_name.equals("")){
            mEmergencyCall3.setVisibility(View.INVISIBLE);
            mEmergencyCall3_notNull.setVisibility(View.VISIBLE);
            mEmergencyCall3_notNull.setText(EC3_name);
            mDEC3.setVisibility(View.VISIBLE);
        }
    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED); //red color line & size=15
        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
            Log.d("DebugLog", String.valueOf(directionPoints.get(i)));
        }
        //clear the old line
        if (routePolyline != null) {
            routePolyline.remove();
        }
        // mMap is the Map Object
        routePolyline = mMap.addPolyline(rectLine);
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(12);
        String text = parent.getItemAtPosition(position).toString();
        if(text.equals("Driving"))
        {
            distanceMode = "driving";
        }else if(text.equals("Transit"))
        {
            distanceMode = "transit";
        }
        else
        {
            distanceMode = "walking";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void createNofiticationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "SanctuaryChannel";
            String description = "Channel for the app Sanctuary";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Sanctuary", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}