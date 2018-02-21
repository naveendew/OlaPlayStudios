package com.dewnaveen.olaplaystudios.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dewnaveen.olaplaystudios.Class.Constants;
import com.dewnaveen.olaplaystudios.MainActivity;
import com.dewnaveen.olaplaystudios.R;

import java.util.concurrent.ExecutionException;

import static java.lang.System.load;

/**
 * Created by naveendewangan on 21/12/17.
 */

public class NotificationService extends Service {

    String TAG = "NotificationService.class";
    Notification status;
    String track_ = "";
    String artist_ = "";
    String cover_ = "";
    Bitmap cvrBitmap;
    private final String LOG_TAG = "NotificationService";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        track_ = bundle.getString("Track", "");
        artist_ = bundle.getString("Artist", "");
        cover_ = bundle.getString("Cover", "");

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
//            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
        final RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);


        Glide
                .with(this)
                .load(cover_)
                .asBitmap()
                .placeholder(R.drawable.loader_2)
                .error(R.drawable.loader_2)
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        views.setImageViewBitmap(R.id.status_bar_icon,
                                resource);
                        bigViews.setImageViewBitmap(R.id.status_bar_icon,
                                resource);
                    }
                });

        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Intent previousIntent = new Intent(this, NotificationService.class);

        Intent playIntent = new Intent(Constants.ACTION.PLAY_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 100, playIntent, 0);
        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);

        Intent nextIntent = new Intent(Constants.ACTION.NEXT_ACTION);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 100, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.status_bar_next, pendingNextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pendingNextIntent);


        Intent prevIntent = new Intent(Constants.ACTION.PREV_ACTION);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 100, prevIntent, 0);
        views.setOnClickPendingIntent(R.id.status_bar_prev, pendingPrevIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, pendingPrevIntent);


        Intent closeIntent = new Intent(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 100, closeIntent, 0);
        views.setOnClickPendingIntent(R.id.status_bar_collapse, pendingCloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pendingCloseIntent);

/*
        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

*/
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.exo_controls_play);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.exo_controls_pause);
        views.setTextViewText(R.id.status_bar_track_name, track_);
//        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, track_);

        views.setTextViewText(R.id.status_bar_artist_name, artist_);
//        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, artist_);

//        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

}
