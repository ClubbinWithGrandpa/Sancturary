package com.example.sanctuary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class LocationService extends Service {

    ArrayList<Double> LocationServiceArray;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("LOCATION_UPDATE", latitude + ", " + longitude);


                SharedPreferences sp;
                sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {


        LocationServiceArray = new ArrayList<>();

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
        locationRequest.setFastestInterval(2000);
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
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putListDouble("GuadianLocations", LocationServiceArray);
                    Log.d("whoFirst", "one");
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}



