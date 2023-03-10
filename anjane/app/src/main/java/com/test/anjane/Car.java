package com.test.anjane;

import android.app.Dialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Car extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, MapView.POIItemEventListener,
        MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, View.OnClickListener {
    private MapView mMapView;
    public ArrayList<String> a = new ArrayList();
    public double latitude;
    public double longitude;

    private LinearLayout subLayout_2;
    private TextView sub_title,sub_title3;
    private TextView sub_text2,sub_text3;

    public double selected_distance = 0;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2,fab3,fab4;
    public MapPoint MyLocation_point = null;


    Document doc = null;

    Button chartbt;
    FloatingActionButton btn_location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.car);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sub_text2=findViewById(R.id.Sub_text2);
        sub_text3=findViewById(R.id.Sub_text3);
        subLayout_2=findViewById(R.id.Sub_layout2);
        sub_title=findViewById(R.id.Sub_title);

        sub_title3=findViewById(R.id.Sub_title3);
        subLayout_2.setVisibility(View.GONE);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        //Set toolbar
        mMapView = findViewById(R.id.map_view);


        GpsTracker gpsTracker = new GpsTracker(Car.this);
        double latitude1 = gpsTracker.getLatitude();
        double longitude1 = gpsTracker.getLongitude();
        final MapPOIItem MyLocation_marker=new MapPOIItem();
        MyLocation_point=MapPoint.mapPointWithGeoCoord(latitude1,longitude1);
        MyLocation_marker.setItemName("?????????");
        MyLocation_marker.setMapPoint(MyLocation_point);

        mMapView.addPOIItem(MyLocation_marker);
        mMapView.setZoomLevel(2,true);
        mMapView.setMapCenterPoint(MyLocation_point,true);

        mMapView.setMapViewEventListener(this);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp); //???????????? ??????
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(Car.this);
                double latitude1 = gpsTracker.getLatitude();
                double longitude1 = gpsTracker.getLongitude();
                MapPoint MyLocation_point = null;

                MyLocation_point=MapPoint.mapPointWithGeoCoord(latitude1,longitude1);
                mMapView.setMapCenterPoint(MyLocation_point,true);
                mMapView.setZoomLevel(2,true);
                mMapView.selectPOIItem(MyLocation_marker,true);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mMapView.setShowCurrentLocationMarker(false);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                mMapView.setShowCurrentLocationMarker(true);


            }
        });

        GetXMLTask task = new GetXMLTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=125.625&MaxX=129.741&MinY=34.206%20&MaxY=38.753&type=its");

    }

    MapView.POIItemEventListener piel = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            mMapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);

            if(mapPOIItem.getItemName()!="?????????") {
                SetSubLayout(mapPOIItem);
                Log.d("?????????110", "1");
                subLayout_2.setVisibility(View.GONE);
                subLayout_2.setVisibility(View.VISIBLE);

            }


        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };
    //fab ????????? ?????? ??????
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();

                break;
            case R.id.fab1:
                anim();
                Toast.makeText(getBaseContext(), "???????????? ?????? 10KM?????????", Toast.LENGTH_SHORT).show();
                mMapView.setZoomLevel(8,true);
                mMapView.setMapCenterPoint(MyLocation_point,true);
                mMapView.removeAllPOIItems();
                selected_distance=10000;

                GetXMLTask5 task5_1 = new GetXMLTask5();
                task5_1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");
                break;
            case R.id.fab2:
                anim();

                Toast.makeText(getBaseContext(), "???????????? ?????? 50KM?????????", Toast.LENGTH_SHORT).show();
                mMapView.setZoomLevel(8,true);
                mMapView.setMapCenterPoint(MyLocation_point,true);

                mMapView.removeAllPOIItems();
                selected_distance=50000;

                GetXMLTask5 task5_2 = new GetXMLTask5();
                task5_2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");
                break;
            case R.id.fab3:
                anim();
                mMapView.setZoomLevel(10,true);
                mMapView.removeAllPOIItems();
                mMapView.setMapCenterPoint(MyLocation_point,true);

                Toast.makeText(getBaseContext(), "???????????? ?????? 100KM?????????", Toast.LENGTH_SHORT).show();
                selected_distance=100000;

                GetXMLTask5 task5_3 = new GetXMLTask5();
                task5_3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");
                break;
            case R.id.fab4:
                anim();
                mMapView.setZoomLevel(12,true);
                mMapView.removeAllPOIItems();
                mMapView.setMapCenterPoint(MyLocation_point,true);

                Toast.makeText(getBaseContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                selected_distance=10000000;
                GetXMLTask5 task5_4 = new GetXMLTask5();
                task5_4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");

                break;
        }
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {//XMLTask = ?????? ???????????? ??????
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();
            //  mMapView.removeAllPOIItems();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            for (int i = 0; i < nodeList.getLength(); i++) {
                MapPOIItem marker = new MapPOIItem();
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                //?????? ??????
                NodeList nameList = fstElmnt.getElementsByTagName("type");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();

                //????????????
                NodeList incidentmsg = fstElmnt.getElementsByTagName("incidentmsg");
                NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());

                HashMap<String, String> body = new HashMap<>();
                String incidenttype="??????";
                String blocktype="NULL";
                marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                {
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                    blocktype="??????????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                    blocktype="????????????";
                }

                //?????? ????????? ?????? ????????? ?????? ????????????
                try{
                    incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                }catch(NullPointerException e){
                    System.out.println("??????2");
                    incimsg = "??????????????????";
                }

                // ?????????????????? body ?????????

                if(Distance(latitude,longitude,latitude1,longitude1) <= 731120000) {

                    body.put("type", blocktype);
                    body.put("statusmsg", incimsg);
                    body.put("key", "00");
                    marker.setItemName("????????????[" + incidenttype + "]");
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    marker.setUserObject(body);

                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    marker.setCustomImageResourceId(R.drawable.trafficaff);
                    mMapView.setPOIItemEventListener(piel);

                    //       marker.setShowCalloutBalloonOnTouch(false);
                    marker.setShowDisclosureButtonOnCalloutBalloon(false);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);
                }

            }
            GetXMLTask2 task2 = new GetXMLTask2();
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");

            super.onPostExecute(doc);
        }
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {//XMLTask2 = ???????????? ???????????? ??????
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc){
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();

            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            for (int i = 0; i < nodeList.getLength(); i++) {
                MapPOIItem marker = new MapPOIItem();
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                //?????? ??????
                NodeList nameList = fstElmnt.getElementsByTagName("type");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                String startday;

                //????????????
                NodeList incidentmsg = fstElmnt.getElementsByTagName("incidentmsg");
                NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());

                HashMap<String, String> body = new HashMap<>();
                String incidenttype="????????????";
                String blocktype="NULL";

                marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                {
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                    blocktype="??????????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                    blocktype="????????????";
                }

                //?????? ????????? ?????? ????????? ?????? ????????????
                try{
                    incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                }catch(NullPointerException e){
                    System.out.println("??????2");
                    incimsg = "??????????????????";

                }

                // ?????????????????? body ?????????


                if(Distance(latitude,longitude,latitude1,longitude1) <= 122230000) {
                    body.put("type",blocktype);
                    body.put("statusmsg",incimsg);

                    body.put("key","01");

                    marker.setItemName("????????????["+incidenttype+"]");
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    System.out.println(incimsg+"\n"+incidenttype+"\n"+blocktype);
                    marker.setUserObject(body);

                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    marker.setCustomImageResourceId(R.drawable.trafficaff);

                    //        marker.setShowCalloutBalloonOnTouch(false);
                    marker.setShowDisclosureButtonOnCalloutBalloon(false);
                    mMapView.setPOIItemEventListener(piel);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);
                }}
            GetXMLTask3 task3 = new GetXMLTask3();
            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NEventIdentity?key=1586662500411&ReqType=2&MinX=125.625&MaxX=129.741&MinY=34.206%20&MaxY=38.753&type=its");

            super.onPostExecute(doc);
        }
    }

    //?????? ??????
    private class GetXMLTask3 extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";

            NodeList nodeList = doc.getElementsByTagName("data");

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    Node node = nodeList.item(i); //data???????????? ??????
                    Element fstElmnt = (Element) node;
                    //?????? ??????
                    NodeList nameList = fstElmnt.getElementsByTagName("type");
                    Element nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();

                    //????????????
                    NodeList incidentmsg = fstElmnt.getElementsByTagName("eventstatusmsg");
                    NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                    NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                    latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                    NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                    longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList startdate = fstElmnt.getElementsByTagName("eventstartday");
                    String startday;

                    startday=startdate.item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(startday+"\n");

                    HashMap<String, String> body = new HashMap<>();
                    String incidenttype="??????";
                    String blocktype="NULL";
                    marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                    if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                    {
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                        blocktype="??????????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                        blocktype="????????????";
                    }

                    //?????? ????????? ?????? ????????? ?????? ????????????
                    try{
                        incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                    }catch(NullPointerException e){
                        System.out.println("??????2");
                        incimsg = "??????????????????";

                    }

                    // ?????????????????? body ?????????

                    if(Distance(latitude,longitude,latitude1,longitude1) <= 312320000) {
                        body.put("type",blocktype);
                        body.put("statusmsg",incimsg);
                        body.put("startday",startday);
                        body.put("key","11");
                        marker.setItemName("????????????["+incidenttype+"]");
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                        marker.setMapPoint(mp);
                        System.out.println(incimsg+"\n"+incidenttype+"\n"+blocktype);
                        marker.setUserObject(body);

                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.construction);
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        //      marker.setShowCalloutBalloonOnTouch(false);
                        marker.setShowDisclosureButtonOnCalloutBalloon(false);
                        mMapView.setPOIItemEventListener(piel);
                        Log.d("??????2","?????????");
                        marker.setShowCalloutBalloonOnTouch(false);
                        mMapView.addPOIItem(marker);
                    }}
            }catch (NullPointerException e){

                System.out.println("??????3");
                Toast.makeText(Car.this, "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(doc);
            GetXMLTask4 task4 = new GetXMLTask4();
            task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NEventIdentity?key=1586662500411&ReqType=2&MinX=125.625&MaxX=129.741&MinY=34.206%20&MaxY=38.753&type=ex");

        }

    }

    //???????????? ????????????
    private class GetXMLTask4 extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    Node node = nodeList.item(i); //data???????????? ??????

                    Element fstElmnt = (Element) node;
                    //?????? ??????
                    NodeList nameList = fstElmnt.getElementsByTagName("type");
                    Element nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();

                    //????????????
                    NodeList incidentmsg = fstElmnt.getElementsByTagName("eventstatusmsg");
                    NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                    NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                    latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                    NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                    longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList startdate = fstElmnt.getElementsByTagName("eventstartday");
                    String startday;

                    startday=startdate.item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(startday+"\n");

                    HashMap<String, String> body = new HashMap<>();
                    String incidenttype="????????????";
                    String blocktype="NULL";
                    marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                    if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                    {
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                        blocktype="??????????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                        blocktype="????????????";
                    }

                    //?????? ????????? ?????? ????????? ?????? ????????????
                    try{
                        incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                    }catch(NullPointerException e){
                        System.out.println("??????2");
                        incimsg = "??????????????????";
                    }

                    startday=startday.substring(0,8);

                    // ?????????????????? body ?????????

                    if(Distance(latitude,longitude,latitude1,longitude1) <= 312320000) {
                        body.put("type", blocktype);
                        body.put("statusmsg", incimsg);
                        body.put("startday", startday);
                        body.put("key", "12");
                        marker.setItemName("????????????[" + incidenttype + "]");
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                        marker.setMapPoint(mp);
                        System.out.println(incimsg + "\n" + incidenttype + "\n" + blocktype);
                        marker.setUserObject(body);

                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.construction);

//                        marker.setShowCalloutBalloonOnTouch(false);
                        marker.setShowDisclosureButtonOnCalloutBalloon(false);
                        mMapView.setPOIItemEventListener(piel);
                        Log.d("??????1","?????????");
                        marker.setShowCalloutBalloonOnTouch(false);
                        mMapView.addPOIItem(marker);
                    }
                }
            }catch (NullPointerException e){

                System.out.println("??????3");
                Toast.makeText(Car.this, "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(Car.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

            super.onPostExecute(doc);
        }

    }

    private class GetXMLTask5 extends AsyncTask<String, Void, Document> {//XMLTask = ?????? ???????????? ??????
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();
            //  mMapView.removeAllPOIItems();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            for (int i = 0; i < nodeList.getLength(); i++) {
                MapPOIItem marker = new MapPOIItem();
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                //?????? ??????
                NodeList nameList = fstElmnt.getElementsByTagName("type");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();

                //????????????
                NodeList incidentmsg = fstElmnt.getElementsByTagName("incidentmsg");
                NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());

                HashMap<String, String> body = new HashMap<>();
                String incidenttype="??????";
                String blocktype="NULL";
                marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                {
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                    blocktype="??????????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                    blocktype="????????????";
                }

                //?????? ????????? ?????? ????????? ?????? ????????????
                try{
                    incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                }catch(NullPointerException e){
                    System.out.println("??????2");
                    incimsg = "??????????????????";
                }

                // ?????????????????? body ?????????

                if(Distance(latitude,longitude,latitude1,longitude1) <= selected_distance) {

                    body.put("type", blocktype);
                    body.put("statusmsg", incimsg);
                    body.put("key", "00");
                    marker.setItemName("????????????[" + incidenttype + "]");
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    marker.setUserObject(body);

                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    marker.setCustomImageResourceId(R.drawable.trafficaff);
                    mMapView.setPOIItemEventListener(piel);

                    //       marker.setShowCalloutBalloonOnTouch(false);
                    marker.setShowDisclosureButtonOnCalloutBalloon(false);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);
                }

            }
            GetXMLTask6 task6 = new GetXMLTask6();
            task6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NIncidentIdentity?key=1586662500411&ReqType=2&MinX=0&MaxX=999&MinY=0%20&MaxY=99&type=ex");

            super.onPostExecute(doc);
        }
    }

    private class GetXMLTask6 extends AsyncTask<String, Void, Document> {//XMLTask2 = ???????????? ???????????? ??????
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc){
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();

            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            for (int i = 0; i < nodeList.getLength(); i++) {
                MapPOIItem marker = new MapPOIItem();
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                //?????? ??????
                NodeList nameList = fstElmnt.getElementsByTagName("type");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                String startday;

                //????????????
                NodeList incidentmsg = fstElmnt.getElementsByTagName("incidentmsg");
                NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());

                HashMap<String, String> body = new HashMap<>();
                String incidenttype="????????????";
                String blocktype="NULL";

                marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                {
                    blocktype="????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                    blocktype="??????????????????";
                }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                    blocktype="????????????";
                }

                //?????? ????????? ?????? ????????? ?????? ????????????
                try{
                    incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                }catch(NullPointerException e){
                    System.out.println("??????2");
                    incimsg = "??????????????????";

                }

                // ?????????????????? body ?????????


                if(Distance(latitude,longitude,latitude1,longitude1) <= selected_distance) {
                    body.put("type",blocktype);
                    body.put("statusmsg",incimsg);

                    body.put("key","01");

                    marker.setItemName("????????????["+incidenttype+"]");
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    System.out.println(incimsg+"\n"+incidenttype+"\n"+blocktype);
                    marker.setUserObject(body);

                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    marker.setCustomImageResourceId(R.drawable.trafficaff);

                    //        marker.setShowCalloutBalloonOnTouch(false);
                    marker.setShowDisclosureButtonOnCalloutBalloon(false);
                    mMapView.setPOIItemEventListener(piel);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);
                }}
            GetXMLTask7 task7 = new GetXMLTask7();
            task7.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NEventIdentity?key=1586662500411&ReqType=2&MinX=125.625&MaxX=129.741&MinY=34.206%20&MaxY=38.753&type=its");

            super.onPostExecute(doc);
        }
    }

    //?????? ??????
    private class GetXMLTask7 extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";

            NodeList nodeList = doc.getElementsByTagName("data");

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    Node node = nodeList.item(i); //data???????????? ??????
                    Element fstElmnt = (Element) node;
                    //?????? ??????
                    NodeList nameList = fstElmnt.getElementsByTagName("type");
                    Element nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();

                    //????????????
                    NodeList incidentmsg = fstElmnt.getElementsByTagName("eventstatusmsg");
                    NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                    NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                    latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                    NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                    longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList startdate = fstElmnt.getElementsByTagName("eventstartday");
                    String startday;

                    startday=startdate.item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(startday+"\n");

                    HashMap<String, String> body = new HashMap<>();
                    String incidenttype="??????";
                    String blocktype="NULL";
                    marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                    if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                    {
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                        blocktype="??????????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                        blocktype="????????????";
                    }

                    //?????? ????????? ?????? ????????? ?????? ????????????
                    try{
                        incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                    }catch(NullPointerException e){
                        System.out.println("??????2");
                        incimsg = "??????????????????";

                    }

                    // ?????????????????? body ?????????

                    if(Distance(latitude,longitude,latitude1,longitude1) <= selected_distance) {
                        body.put("type",blocktype);
                        body.put("statusmsg",incimsg);
                        body.put("startday",startday);
                        body.put("key","11");
                        marker.setItemName("????????????["+incidenttype+"]");
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                        marker.setMapPoint(mp);
                        System.out.println(incimsg+"\n"+incidenttype+"\n"+blocktype);
                        marker.setUserObject(body);

                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.construction);
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        //      marker.setShowCalloutBalloonOnTouch(false);
                        marker.setShowDisclosureButtonOnCalloutBalloon(false);
                        mMapView.setPOIItemEventListener(piel);
                        Log.d("??????2","?????????");
                        marker.setShowCalloutBalloonOnTouch(false);
                        mMapView.addPOIItem(marker);
                    }}
            }catch (NullPointerException e){

                System.out.println("??????3");
                Toast.makeText(Car.this, "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(doc);
            GetXMLTask8 task8 = new GetXMLTask8();
            task8.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://openapi.its.go.kr:8082/api/NEventIdentity?key=1586662500411&ReqType=2&MinX=125.625&MaxX=129.741&MinY=34.206%20&MaxY=38.753&type=ex");

        }

    }

    //???????????? ????????????
    private class GetXMLTask8 extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML?????? ?????? ????????? ??????
                doc = db.parse(new InputSource(url.openStream())); //XML????????? ????????????.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            GpsTracker gpsTracker = new GpsTracker(Car.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();
            MapPoint ma = null;
            MapPoint mp = null;
            double latitude;
            double longitude;
            String incimsg;
            ma = mMapView.getMapCenterPoint();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("data");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    Node node = nodeList.item(i); //data???????????? ??????

                    Element fstElmnt = (Element) node;
                    //?????? ??????
                    NodeList nameList = fstElmnt.getElementsByTagName("type");
                    Element nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();

                    //????????????
                    NodeList incidentmsg = fstElmnt.getElementsByTagName("eventstatusmsg");
                    NodeList LanesBlockType = fstElmnt.getElementsByTagName("lanesblocktype");

                    NodeList ycord = fstElmnt.getElementsByTagName("coordy");

                    latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());

                    NodeList xcord = fstElmnt.getElementsByTagName("coordx");

                    longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList startdate = fstElmnt.getElementsByTagName("eventstartday");
                    String startday;

                    startday=startdate.item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(startday+"\n");

                    HashMap<String, String> body = new HashMap<>();
                    String incidenttype="????????????";
                    String blocktype="NULL";
                    marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                    if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("0")){
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("1"))
                    {
                        blocktype="????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("2")){
                        blocktype="??????????????????";
                    }else if(LanesBlockType.item(0).getChildNodes().item(0).getNodeValue().contains("3")){
                        blocktype="????????????";
                    }

                    //?????? ????????? ?????? ????????? ?????? ????????????
                    try{
                        incimsg = incidentmsg.item(0).getChildNodes().item(0).getNodeValue();

                    }catch(NullPointerException e){
                        System.out.println("??????2");
                        incimsg = "??????????????????";
                    }

                    startday=startday.substring(0,8);

                    // ?????????????????? body ?????????

                    if(Distance(latitude,longitude,latitude1,longitude1) <= selected_distance) {
                        body.put("type", blocktype);
                        body.put("statusmsg", incimsg);
                        body.put("startday", startday);
                        body.put("key", "12");
                        marker.setItemName("????????????[" + incidenttype + "]");
                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                        marker.setMapPoint(mp);
                        System.out.println(incimsg + "\n" + incidenttype + "\n" + blocktype);
                        marker.setUserObject(body);

                        mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.construction);

//                        marker.setShowCalloutBalloonOnTouch(false);
                        marker.setShowDisclosureButtonOnCalloutBalloon(false);
                        mMapView.setPOIItemEventListener(piel);
                        Log.d("??????1","?????????");
                        marker.setShowCalloutBalloonOnTouch(false);
                        mMapView.addPOIItem(marker);
                    }
                }
            }catch (NullPointerException e){

                System.out.println("??????3");
                Toast.makeText(Car.this, "?????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(doc);
            Toast.makeText(Car.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            selected_distance=0;
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

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {


    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        subLayout_2.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {




//            ((TextView) mCalloutBalloon.findViewById(R.id.car_title)).setText(marker.getItemName());
//
//
        //           HashMap<String, String> body = (HashMap<String, String>) mapPOIItem.getUserObject();
        //           String date = body.get("type");
//            String spin2 = body.get("statusmsg");
//            String startday = "?????????";
//
//            try {
//                startday= body.get("startday");
//
//            }catch (NullPointerException e){
//
//            }
//
//            ((TextView) mCalloutBalloon.findViewById(R.id.content1)).setText(date);
//            ((TextView) mCalloutBalloon.findViewById(R.id.content2)).setText(spin2);
//
//            //??????,???????????? ?????? ???????????? ??????????????? ????????????
//            if(body.get("key")=="0"){
//                mCalloutBalloon.findViewById(R.id.subtitle3).setVisibility(View.INVISIBLE);
//                mCalloutBalloon.findViewById(R.id.content3).setVisibility(View.INVISIBLE);
//                mCalloutBalloon.findViewById(R.id.view3).setVisibility(View.INVISIBLE);
//                //???????????? ???????????? ????????????????????? ?????? ?????????
//            }else if(body.get("key")=="1") {
//                mCalloutBalloon.findViewById(R.id.subtitle3).setVisibility(View.VISIBLE);
//                mCalloutBalloon.findViewById(R.id.content3).setVisibility(View.VISIBLE);
//                mCalloutBalloon.findViewById(R.id.view3).setVisibility(View.VISIBLE);
//
//                ((TextView) mCalloutBalloon.findViewById(R.id.content3)).setText(startday);
//
//            }
//
//

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    public double Distance(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }

    public void SetSubLayout(MapPOIItem marker){
        if(marker.getItemName().contains("?????????"))
        {
            return ;
        }else {
            marker.getUserObject();


            HashMap<String, String> body = (HashMap<String, String>) marker.getUserObject();
            switch(body.get("key")){
                case "00" :
                    sub_title.setText("?????? ????????????");
                    sub_title3.setText("?????? ??????");

                    switch(body.get("statusmsg")){
                        case "": {
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        default : {
                            sub_text2.setText(body.get("statusmsg"));
                            break;

                        }

                    }
                    switch(body.get("type")){
                        case "":{
                            sub_text3.setText("???????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text3.setText("???????????? ??????");
                            break;

                        }
                        default : {
                            sub_text3.setText(body.get("type"));
                            break;
                        }

                    }
                    break;
                case "01" :
                    sub_title.setText("???????????? ????????????");
                    sub_title3.setText("?????? ??????");




                    switch(body.get("statusmsg")){
                        case "": {
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        default : {
                            sub_text2.setText(body.get("statusmsg"));
                            break;

                        }

                    }
                    switch(body.get("type")) {
                        case "": {
                            sub_text3.setText("???????????? ??????");
                            break;
                        }
                        case " ": {
                            sub_text3.setText("???????????? ??????");
                            break;

                        }
                        default: {
                            sub_text3.setText(body.get("type"));
                            break;
                        }

                    }
                    break;
                case "11" :
                    sub_title.setText("?????? ??????");
                    sub_title3.setText("?????? ??????");

                    switch(body.get("statusmsg")){
                        case "": {
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        default : {
                            sub_text2.setText(body.get("statusmsg"));
                            break;

                        }

                    }

                    switch(body.get("startday")){
                        case "":{
                            sub_text3.setText("?????? ????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text3.setText("?????? ????????? ??????");
                            break;

                        }
                        default : {
                            sub_text3.setText(cutting(body.get("startday")));
                            break;
                        }

                    }
                    break;
                case "12" :
                    sub_title.setText("???????????? ??????");
                    sub_title3.setText("?????? ??????");

                    switch(body.get("statusmsg")){
                        case "": {
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text2.setText("???????????? ??????");
                            break;
                        }
                        default : {
                            sub_text2.setText(body.get("statusmsg"));
                            break;

                        }


                    }
                    switch(body.get("startday")){
                        case "":{
                            sub_text3.setText("?????? ????????? ??????");
                            break;
                        }
                        case " ":{
                            sub_text3.setText("?????? ????????? ??????");
                            break;

                        }
                        default : {
                            sub_text3.setText(cutting(body.get("startday")));
                            break;
                        }

                    }

                    break;
            };

//           String date = body.get("type");
//            String spin2 = body.get("statusmsg");
//            String startday = "?????????";
//
//            try {
//                startday= body.get("startday");
//
//            }catch (NullPointerException e){
//
//            }
//
//            ((TextView) mCalloutBalloon.findViewById(R.id.content1)).setText(date);
//            ((TextView) mCalloutBalloon.findViewById(R.id.content2)).setText(spin2);
//
//            //??????,???????????? ?????? ???????????? ??????????????? ????????????
//            if(body.get("key")=="0"){
//                mCalloutBalloon.findViewById(R.id.subtitle3).setVisibility(View.INVISIBLE);
//                mCalloutBalloon.findViewById(R.id.content3).setVisibility(View.INVISIBLE);
//                mCalloutBalloon.findViewById(R.id.view3).setVisibility(View.INVISIBLE);
//                //???????????? ???????????? ????????????????????? ?????? ?????????
//            }else if(body.get("key")=="1") {
//                mCalloutBalloon.findViewById(R.id.subtitle3).setVisibility(View.VISIBLE);
//                mCalloutBalloon.findViewById(R.id.content3).setVisibility(View.VISIBLE);
//                mCalloutBalloon.findViewById(R.id.view3).setVisibility(View.VISIBLE);
//
//                ((TextView) mCalloutBalloon.findViewById(R.id.content3)).setText(startday);
//
//            }
//
//

        }



    }

    public String cutting(String date){
        try {
            String formattingString = null;
            String A = "-";
            String B = ":";
            String C = " ";
            String year;
            String month;
            String day;
            String hour;
            String minute;
            year = date.substring(0, 4);
            month = date.substring(4, 6);
            day = date.substring(6, 8);

            formattingString = year + A + month + A + day;

            return formattingString;
        }catch (StringIndexOutOfBoundsException e){
            return date;
        }


    }
    public void anim() {

        if (isFabOpen) {

            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);

            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }
    }
}

