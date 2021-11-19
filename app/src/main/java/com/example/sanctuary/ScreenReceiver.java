package com.example.sanctuary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;



public class ScreenReceiver extends BroadcastReceiver {
    int count = 0;
    double last_time = 0;

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "SanctuaryChannel";
            String description = "Channel for the app Sanctuary";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Sanctuary", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            wasScreenOn = false;


            last_time = System.currentTimeMillis();


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            double this_time = System.currentTimeMillis();


            if(this_time - last_time < 75)
            {

                TinyDB tinydb = new TinyDB(context);
                String isActivated = tinydb.getString("isActivated");
                if(isActivated.equals("false"))
                {
                    isActivated = "true";
                    tinydb.putString("isActivated", isActivated);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Sanctuary")
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Guardian mode is on")
                            .setContentText("Guardian Mode activated!")
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(100, builder.build());
                    MapsActivity.mButtonStartReset.setText("Stop");
                    MapsActivity.Tracking.setVisibility(View.VISIBLE);
                    MapsActivity.mGuardianModeOn.setVisibility(View.VISIBLE);

                }
                else
                {
                    isActivated = "false";
                    tinydb.putString("isActivated", isActivated);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Sanctuary")
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Guardian mode is off")
                            .setContentText("Guardian Mode deactivated!")
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(100, builder.build());

                    MapsActivity.mButtonStartReset.setText("Guardian");
                    MapsActivity.Tracking.setVisibility(View.INVISIBLE);
                    MapsActivity.mGuardianModeOn.setVisibility(View.INVISIBLE);
                }
            }


            // and do whatever you need to do here
            wasScreenOn = true;

        } else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){




            Log.e("LOB","1");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }








}