package com.test.anjane.firebase;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RainFirebase extends AppCompatActivity {
    private static final String TAG = "DustFirebase";
    private FirebaseFirestore db;

    boolean resultMsg = false,
            areaName = false,
            warnStress = false,
            startTime = false;
    ArrayList<String> areanameitem = new ArrayList<>();
    ArrayList<String> warnstressitem = new ArrayList<>();
    ArrayList<String> starttimeitem = new ArrayList<>();

    private String formatDate;
    private String startDate;
    List<List<Address>> address = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
        formatDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(cal.DATE, -6);
        startDate = format.format(cal.getTime());

        db = FirebaseFirestore.getInstance();

        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(areanameitem.size() == 0) {
            // onAuthSuccess(null, null, null);
        } else {
            for (int i = 0; i < areanameitem.size(); i++) {
                onAuthSuccess(areanameitem.get(i), warnstressitem.get(i), starttimeitem.get(i));
            }
        }
    }

    private void onAuthSuccess(String areaname, String warnstress, String startTime) {
        db.collection("rain").document(areaname).set(toMap(areaname, warnstress, startTime));
    }

    @Exclude
    public Map<String, Object> toMap(String areaname, String warnstress, String startTime) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("areaname", areaname);
        result.put("warnstress", warnstress);
        result.put("startTime", startTime);

        return result;
    }

    public void getData(){
        StrictMode.enableDefaults();
        try{
            URL url = new URL("http://apis.data.go.kr/1360000/WthrWrnInfoService/getPwnCd?serviceKey=S8lyiY1crKS8rseJMPCvPwBEUEvsSGUD4jc6f9Fxeb3TSOC6NY4lrNv6qr%" +
                    "2BWSdyy00n6ANIa9tGp9x9Qbh%2FEiw%3D%3D&numOfRows=20&pageNo=1" +
                    "&dataType=XML&fromTmFc=" + startDate + "&toTmFc=" + formatDate + "&warningType=2"
            ); //?????? URL??????

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("?????????????????????.");

            while (areanameitem.size() < 20 || parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser??? ?????? ????????? ????????? ??????
                        if(parser.getName().equals("resultMsg")){ //title ????????? ????????? ????????? ?????? ??????
                            resultMsg = true;
                        }
                        if(parser.getName().equals("areaName")){ //title ????????? ????????? ????????? ?????? ??????
                            areaName = true;
                        }
                        if(parser.getName().equals("warnStress")){ //mapy ????????? ????????? ????????? ?????? ??????
                            warnStress = true;
                        }
                        if(parser.getName().equals("startTime")){ //mapy ????????? ????????? ????????? ?????? ??????
                            startTime = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(resultMsg){ //isTitle??? true??? ??? ????????? ????????? ??????.
                            resultMsg = false;
                            if(parser.getText().equals("NO_DATA")){
                                return;
                            }
                        }
                        if(areaName){ //isTitle??? true??? ??? ????????? ????????? ??????.
                            areaName = false;
                            areanameitem.add(parser.getText());
                            geocoder(parser.getText());
                        }
                        if(warnStress){ //isMapy??? true??? ??? ????????? ????????? ??????.
                            warnStress = false;
                            warnstressitem.add(parser.getText());
                        }
                        if(startTime){ //isMapy??? true??? ??? ????????? ????????? ??????.
                            startTime = false;
                            starttimeitem.add(parser.getText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            Log.d("?????? ??????", e.toString());
        }
    }

    public void geocoder(String add) throws IOException {
        final Geocoder geocoder = new Geocoder(getApplicationContext());
        address.add( geocoder.getFromLocationName( add, 20 ) ); // ????????????
    }

    public void onDestroy() {
        super.onDestroy();
        finish();
    }
}