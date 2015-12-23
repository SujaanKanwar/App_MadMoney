package com.payment.sujan.madmoney.Services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.Position;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceBackgroundServices extends IntentService implements ResultCallback<Status> {
    private static final int GEO_FENCE_EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    private static final int GEO_FENCE_LOITERING_TIME = 1 * 1000;


    private static final String ACTION_SET_GEOFENCE = "com.payment.sujan.madmoney.Services.action.SET_GEOFENCES";

    private static final String EXTRA_PARAM1 = "com.payment.sujan.madmoney.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.payment.sujan.madmoney.Services.extra.PARAM2";

    public DeviceBackgroundServices() {
        super("DeviceBackgroundServices");
    }

    public static void setGeofences(Context context, ArrayList<Position> positions) {
        Intent intent = new Intent(context, DeviceBackgroundServices.class);
        intent.setAction(ACTION_SET_GEOFENCE);
        intent.putExtra(EXTRA_PARAM1, positions);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_GEOFENCE.equals(action)) {
                ArrayList<Position> param1 = (ArrayList<Position>) intent.getSerializableExtra(EXTRA_PARAM1);
                GoogleApiClient param2 = GlobalStatic.getGoogleApiClient();
                handleActionFoo(param1, param2);
            }
        }
    }

    private void handleActionFoo(ArrayList<Position> positions, GoogleApiClient googleApiClient) {
        ArrayList<Geofence> geofenceList = new ArrayList<>();
        double latitude, longitude;
        int radius;
        for (int i = 0; i < positions.size(); i++) {
            latitude = Double.parseDouble(positions.get(i).getLatitude());
            longitude = Double.parseDouble(positions.get(i).getLongitude());
            radius = Integer.parseInt(positions.get(i).getRadius());
            geofenceList.add(new Geofence.Builder()
                    .setRequestId("123")
                    .setCircularRegion(
                            latitude,
                            longitude,
                            radius
                    )
                    .setExpirationDuration(GEO_FENCE_EXPIRATION_TIME)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(GEO_FENCE_LOITERING_TIME)
                    .build());
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                getGeofencingRequest(geofenceList),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this.getApplicationContext(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
    }
}
