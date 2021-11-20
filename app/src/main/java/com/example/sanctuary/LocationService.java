package com.example.sanctuary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class LocationService extends Service {

    private String isActivated;
    private int count=0;
    private MediaRecorder mRecorder;

    ArrayList<Double> LocationServiceArray;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            super.onLocationResult(locationResult);
            TinyDB tinydb = new TinyDB(getApplicationContext());
            String isActivated = tinydb.getString("isActivated");
            if (isActivated.equals("true"))
            {



                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();


                    SharedPreferences sp;
                    sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String name = sp.getString("name", "");
                    String age = sp.getString("age", "");
                    String gender = sp.getString("gender", "");
                    String bt = sp.getString("bt", "");
                    String helpMessage = "I am in danger, please come and help me! \nI am also providing my "+
                            "personal information and current locations update every 5 min until Guardian mode is off. ";
                    String info_loc = "\nName: " + name + "\n" + "Age: " + age + "\n" + "Gender: " + gender + "\n" + "Blood type: " + bt + "\n";


                    String GC1_name = sp.getString("GC1_name", "");
                    String GC2_name = sp.getString("GC2_name", "");
                    String GC3_name = sp.getString("GC3_name", "");
                    String GC4_name = sp.getString("GC4_name", "");

                    String GC1_num = sp.getString("GC1_number", "");
                    String GC2_num = sp.getString("GC2_number", "");
                    String GC3_num = sp.getString("GC3_number", "");
                    String GC4_num = sp.getString("GC4_number", "");

                    SmsManager smsManager = SmsManager.getDefault();


                    if(count == 0)
                    {
                        if (sp.getString("allowAudio", "").equals("true"))
                        {
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
                            }
                            mRecorder.start();
                            Log.d("RRR", "start!");
                        }






                        LocationServiceArray = new ArrayList<>();
                        if(!GC1_name.equals("")){
                            smsManager.sendTextMessage(GC1_num, null, helpMessage, null, null);
                            smsManager.sendTextMessage(GC1_num, null, info_loc, null, null);
                        }
                        if(!GC2_name.equals("")){
                            smsManager.sendTextMessage(GC2_num, null, helpMessage, null, null);
                            smsManager.sendTextMessage(GC2_num, null, info_loc, null, null);
                        }
                        if(!GC3_name.equals("")){
                            smsManager.sendTextMessage(GC3_num, null, helpMessage, null, null);
                            smsManager.sendTextMessage(GC3_num, null, info_loc, null, null);
                        }
                        if(!GC4_name.equals("")){
                            smsManager.sendTextMessage(GC4_num, null, helpMessage, null, null);
                            smsManager.sendTextMessage(GC4_num, null, info_loc, null, null);
                        }
                        count = 1;
                    }

                    StringBuffer smsBody = new StringBuffer();
                    smsBody.append("http://maps.google.com?q=");
                    smsBody.append(latitude);
                    smsBody.append(",");
                    smsBody.append(longitude);

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

                    LocationServiceArray.add(latitude);
                    Log.d("DebugLog", String.valueOf(latitude));
                    LocationServiceArray.add(longitude);
                    Log.d("DebugLog", String.valueOf(longitude));


                }
            }
            else
            {
                if(count == 1)
                {
                    tinydb.putListDouble("GuadianLocations", LocationServiceArray);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (sp.getString("allowAudio", "").equals("true"))
                    {
                        if(mRecorder != null)
                        {
                            mRecorder.stop();
                            mRecorder.release();
                            Log.d("RRR", "end!");
                        }
                        mRecorder = null;

                    }
                }
                count = 0;

            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {


        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.drawable.ic_baseline_notification_important_24);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Runnning");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(4000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {


        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        final BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        isActivated = "false";
        TinyDB tinydb = new TinyDB(getApplicationContext());
        tinydb.putString("isActivated", isActivated);

        createNofiticationChannel();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                    tinydb.putListDouble("GuadianLocations", LocationServiceArray);
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
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



