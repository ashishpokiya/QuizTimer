package com.example.quiztimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BroadcastService extends Service {

    public long counter = MainActivity.timeMinute * 60 * 1000;
//    public long counter2 = counter;

    private Context ctx;
    private Activity localActivity;

    private static BroadcastService instance = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        timer.start();
        Log.e("TIMER", ">start");

        instance = this;
        //showNotification();
    }


    //count to end the game
    private CountDownTimer timer = new CountDownTimer(MainActivity.timeMinute * 60 * 1000, 1000) {

        public void onTick(long millisUntilFinished) {
            counter = millisUntilFinished / 1000;

            Log.e("TIMER", ">" + counter);

            createNotification();

        }

        public void onFinish() {
            counter = 0;
            timer.cancel();

        }

    };


    String CHANNEL_ID = "0";
    int notificationId = 111;

    /*
     * Show a notification while this service is running
     */
    private void createNotificationChannel() {
        CharSequence channelName = CHANNEL_ID;
        String channelDesc = "channelDesc";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setSound(null, null);
            channel.setDescription(channelDesc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (currChannel == null)
                notificationManager.createNotificationChannel(channel);
        }
    }


    public void createNotification() {

        CHANNEL_ID = this.getString(R.string.app_name);
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        int min = (int) counter / 60;

        int sec = (int) counter % 60;
        String format = "%1$02d";

        @SuppressLint("DefaultLocale")
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(CHANNEL_ID)
                .setContentText("Timer: " + (String.format(format, min)) + ":" + (String.format(format, sec)))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        startForeground(notificationId, mBuilder.build());

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(notificationId, mBuilder.build());
    }


    public static boolean isInstanceCreated() {
        return instance != null;
    }//met

    @Override
    public void onDestroy() {
        Log.e("TIMER", "dest>");
        timer.cancel();
        instance = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}
