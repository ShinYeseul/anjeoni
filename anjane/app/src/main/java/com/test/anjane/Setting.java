package com.test.anjane;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.anjane.dust.DustActivity;
import com.test.anjane.earthquakes.Earthquakes;
import com.test.anjane.rain.Rain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Setting extends AppCompatActivity {
    private Switch sRain;
    private Switch sSnow;
    private Switch sWind;
    private Switch sDust;
    private Switch sEar;
    private FirebaseFirestore db;
    private List<Map<String, Object>> rainList = new ArrayList<>();
    private List<Map<String, Object>> snowList = new ArrayList<>();
    private List<Map<String, Object>> windList = new ArrayList<>();
    private List<Map<String, Object>> dustList = new ArrayList<>();
    private List<Map<String, Object>> earthquakesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.setting );

        sRain = findViewById(R.id.swRain);
        sSnow = findViewById(R.id.swSnow);
        sWind = findViewById(R.id.swWind);
        sDust = findViewById(R.id.swDust);
        sEar = findViewById(R.id.swEar);

        sRain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                    Dustdb("rian");
                }
            }
        });

        sSnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                    Dustdb("sonw");
                }
            }
        });

        sWind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                    Dustdb("wind");
                }
            }
        });

        sDust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "???????????? ?????????", Toast.LENGTH_SHORT).show();
                    Dustdb("dust");
                }
            }
        });

        sEar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                    Dustdb("earthquakes");
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp); //???????????? ??????
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void Dustdb(final String name) {
        db = FirebaseFirestore.getInstance();
        db.collection(name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            if(task.getResult().size() == 0) {
                                Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                switch (name) {
                                    case "rain":
                                        rainList.add(i, document.getData());
                                        if (rainList.get(i).get("startTime").toString().contains("20200624")) {
                                            String warnstress = null;
                                            if(rainList.get(i).get("warnstress").toString().equals( "0" )) warnstress = "?????? ?????????";
                                            else warnstress = "?????? ??????";
                                            Message(name, rainList.get(i).get("areaname").toString(), warnstress);
                                            break;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    case "snow":
                                        snowList.add(i, document.getData());
                                        if(snowList.get(i).get("startTime").toString().contains("20200624")) {
                                            String warnstress = null;
                                            if(snowList.get(i).get("warnstress").toString().contains("0")) warnstress = "?????? ?????????";
                                            else warnstress = "?????? ??????";
                                            Message(name, snowList.get(i).get("areaname").toString(), warnstress);
                                            break;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    case "wind":
                                        windList.add(i, document.getData());
                                        if(windList.get(i).get("startTime").toString().contains("20200624")) {
                                            String warnstress = null;
                                            if(windList.get(i).get("warnstress").toString().contains("0")) warnstress = "?????? ?????????";
                                            else warnstress = "?????? ??????";
                                            Message(name, windList.get(i).get("areaname").toString(), warnstress);
                                            break;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    case "dust":
                                        dustList.add(i, document.getData());
                                        if(dustList.get(i).get("address").toString().contains("??????")) {
                                            Message(name, dustList.get(i).get("address").toString(), dustList.get(i).get("content").toString());
                                            break;
                                        }
                                        break;
                                    case "earthquakes":
                                        earthquakesList.add(i, document.getData());
                                        if(earthquakesList.get(i).get("time").toString().contains("2020-06-19")) {
                                            Message(name, earthquakesList.get(i).get("gps").toString(), earthquakesList.get(i).get("size").toString());
                                            break;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                }
                                i++;
                            }
                        }
                    }
                });
    }

    public void Message(String name, String address, String value) {
        Intent intent = null;
        String channelId = name;
        PendingIntent pendingIntent;
        NotificationCompat.Builder notificationBuilder;
        NotificationManager mNotM;
        int importance;
        switch (name) {
            case "rain":
            case "snow":
            case "wind":
                intent = new Intent(this, Rain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);

                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.mainiv)
                                .setContentTitle("[?????? ?????? ??????]")
                                .setContentText("[" + value + "] " + address)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                mNotM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelId, importance);
                    mNotM.createNotificationChannel(mChannel);
                }
                mNotM.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());
                break;
            case "dust":
                intent = new Intent(this, DustActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);

                String v;
                if(Integer.parseInt(value) <= 30)
                    v = "??????";
                else if(Integer.parseInt(value) <= 80)
                    v = "??????";
                else if(Integer.parseInt(value) <= 150)
                    v = "??????";
                else
                    v = "?????? ??????";
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.mainiv)
                                .setContentTitle("[?????? ?????? ??????]")
                                .setContentText("[????????????] " + address + " " + v)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                mNotM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelId, importance);
                    mNotM.createNotificationChannel(mChannel);
                }
                mNotM.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());
                break;
            case "earthquakes":
                intent = new Intent(this, Earthquakes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);

                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.mainiv)
                                .setContentTitle("[?????? ?????? ??????]")
                                .setContentText("[??????] " + address + "(??????" + value + ")")
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                mNotM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelId, importance);
                    mNotM.createNotificationChannel(mChannel);
                }
                mNotM.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}