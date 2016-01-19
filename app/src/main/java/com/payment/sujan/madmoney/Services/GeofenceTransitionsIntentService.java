package com.payment.sujan.madmoney.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.payment.sujan.madmoney.AppData.Position;
import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.Resources.DBTeleLocation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            ArrayList<String> geofenceTransitionIds = getGeofenceTransitionIds(triggeringGeofences);

            DBTeleLocation dbTeleLocation = new DBTeleLocation(this);
            Position position;
            for (String id : geofenceTransitionIds) {
                dbTeleLocation.updateOperationStatus(id, 2);
                position = dbTeleLocation.selectTelPosition(id);
                String message;
                message = "Congratulations !! You have earned the mad money !! by entering at" + position.getLocationName()
                        + ":" + position.getDescription() +
                        " : City" + position.getCity();
                sendNotification(message);
            }
        }
    }

    private void sendNotification(String geofenceTransitionDetails) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Congratulations! from MadMoney")
                        .setContentText(geofenceTransitionDetails);

        Intent resultIntent = new Intent(this, com.payment.sujan.madmoney.MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(com.payment.sujan.madmoney.MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12, mBuilder.build());
    }

    private ArrayList getGeofenceTransitionIds(List<Geofence> triggeringGeofences) {

        ArrayList triggeringGeofencesIdsList = new ArrayList();

        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }

        return triggeringGeofencesIdsList;
    }

}
