package com.test.anjane.shelter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.anjane.GpsTracker;
import com.test.anjane.R;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapCircle;
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
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Shelter extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        MapView.MapViewEventListener, MapView.CurrentLocationEventListener,MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private MapView mMapView;

    private FloatingActionButton fltbutton;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    int ia = 1;
    boolean first_start = true;
    private EditText edit_address;
    private Button search;
    private Button searchbutton;
    LinearLayout subLayout;
    LinearLayout subLayout_2;
    TextView sub_title, sub_title2, sub_title3;
    TextView sub_text1, sub_text2, sub_text3;
    private boolean circ = false;
    private static final String LOG_TAG = "Shelter";
    Document doc = null;

    public GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shelter);

        edit_address = findViewById(R.id.edit_address);
        search = findViewById(R.id.search);
        searchbutton = findViewById(R.id.searchbutton);
        fltbutton = findViewById(R.id.floatingActionButton);

        sub_title = findViewById(R.id.Sub_title);
        sub_text2 = findViewById(R.id.Sub_text2);
        sub_text3 = findViewById(R.id.Sub_text3);
        subLayout_2 = findViewById(R.id.Sub_layout2);


        sub_title2 = findViewById(R.id.Sub_title2);
        sub_title3 = findViewById(R.id.Sub_title3);
        subLayout_2.setVisibility(View.GONE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp); //???????????? ??????
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        setUpMapIfNeeded();
        mMapView.setZoomLevel(4, true);

        mMapView.setCurrentLocationEventListener(this);
        mMapView.setShowCurrentLocationMarker(true);

        mMapView.setMapViewEventListener(this);

        CenterSearch();
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());


        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }
//?????? ??? ?????????


        fltbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CenterSearch();
            }
        });

        //???????????? ?????? ?????????
        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                imm.showSoftInput(edit_address, 0);
                imm.hideSoftInputFromWindow(edit_address.getWindowToken(), 0);
                mMapView.removeAllPOIItems();
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

                GetXMLTask2 task = new GetXMLTask2();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=1&numOfRows=999&type=xml&flag=Y");

                mMapView.setZoomLevel(4, true);

                if (ia == 6) {
                    ia = 1;
                }


            }
        });
        searchbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker2 = new GpsTracker(Shelter.this);

                mMapView.removeAllPOIItems();
                GetXMLTask task = new GetXMLTask();

                //  task.execute("http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=1&numOfRows=999&type=xml&flag=Y");
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=1&numOfRows=999&type=xml&flag=Y");

                if (ia == 6) {
                    ia = 1;

                }

                //?????????
            }
        });

    }
    //?????? ?????? ????????? ???????????? ???????????????

    @Override
    protected void onResume() {
        System.out.println("?????????");
        super.onResume();

    }

    private void setUpMapIfNeeded() {
        System.out.println("????????????????????????"); // ????????????
        if (mMapView == null) {
            System.out.println("mMap??? ????????? ");

            mMapView = findViewById(R.id.map_view);

            if (mMapView != null) {

                System.out.println("mMap??? ???????????????");

            }
        }
    }

    MapView.POIItemEventListener piel = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            //    mMapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
            searchbutton.setVisibility(View.GONE);
            if (mapPOIItem.getItemName() != "?????????") {
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

    // ????????? ??? ?????? ??????
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
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
            MapPoint ma = null;

            ma = mMapView.getMapCenterPoint();
            //  mMapView.removeAllPOIItems();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("row");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????
            MapPoint mp = null;
            double latitude;
            double longitude;
            String mngps_nm1;
            String dtl_adres1;
            for (int i = 0; i < nodeList.getLength(); i++) {
                MapPOIItem marker = new MapPOIItem();

                //?????? ???????????? ??????
                s += "" + i + ": ???????????????: ";
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("vt_acmdfclty_nm");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                s += "?????? = " + ((Node) nameList.item(0)).getNodeValue() + " ,";


                NodeList telno = fstElmnt.getElementsByTagName("mngps_telno");

                NodeList dtl_adres = fstElmnt.getElementsByTagName("dtl_adres");

                NodeList ycord = fstElmnt.getElementsByTagName("ycord");
                //<wfKor>??????</wfKor> =====> <wfKor> ????????? ????????? ??????????????? TextNode ?????? TextNode??? ?????? ??????
                s += "ycord = " + ycord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());
                NodeList xcord = fstElmnt.getElementsByTagName("xcord");
                //<wfKor>??????</wfKor> =====> <wfKor> ????????? ????????? ??????????????? TextNode ?????? TextNode??? ?????? ??????
                s += "xcord = " + xcord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                HashMap<String, String> body = new HashMap<>();
                body.put("dtl_adres", dtl_adres.item(0).getChildNodes().item(0).getNodeValue());
                try {
                    body.put("telno", telno.item(0).getChildNodes().item(0).getNodeValue());
                } catch (NullPointerException e) {
                    body.put("telno", "????????????");
                }

                marker.setUserObject(body);

                if (Distance(ma.getMapPointGeoCoord().latitude, ma.getMapPointGeoCoord().longitude, latitude, longitude) <= 1000) {

                    if (((Node) nameList.item(0)).getNodeValue().contains("?????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.gym);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.church);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????"))//??????????????????
                    {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.school);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("????????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.centre);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("?????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.book);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("?????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.shelter_university);
                    } else {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.etc);
                    }
                    marker.setItemName("????????? : " + ((Node) nameList.item(0)).getNodeValue());
                    body.put("itemname", ((Node) nameList.item(0)).getNodeValue());
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    mMapView.setPOIItemEventListener(piel);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);

                }

            }
            if (ia < 6) {
                ia++;
                if (ia == 6) {

                } else {
                    GetXMLTask task = new GetXMLTask();
                    task.execute("http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=" + ia + "&numOfRows=999&type=xml&flag=Y");

                }
            }
            super.onPostExecute(doc);
        }
    }

    // ?????? ?????? ?????? ?????? ??????
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
            MapPoint ma = null;

            gpsTracker = new GpsTracker(Shelter.this);
            double latitude1 = gpsTracker.getLatitude();
            double longitude1 = gpsTracker.getLongitude();

            ma = MapPoint.mapPointWithGeoCoord(latitude1, longitude1);


            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("row");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????
            MapPoint mp = null;
            double latitude;
            double longitude;
            String mngps_nm1;
            String dtl_adres1;

            for (int i = 0; i < nodeList.getLength(); i++) {
                String edit_ad = String.valueOf(edit_address.getText());
                MapPOIItem marker = new MapPOIItem();
                //?????? ???????????? ??????
                s += "" + i + ": ???????????????: ";
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("vt_acmdfclty_nm");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                s += "?????? = " + ((Node) nameList.item(0)).getNodeValue() + " ,";

                NodeList telno = fstElmnt.getElementsByTagName("mngps_telno");

                NodeList dtl_adres = fstElmnt.getElementsByTagName("dtl_adres");

                NodeList ycord = fstElmnt.getElementsByTagName("ycord");
                s += "ycord = " + ycord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());
                NodeList xcord = fstElmnt.getElementsByTagName("xcord");
                s += "xcord = " + xcord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());
                HashMap<String, String> body = new HashMap<>();
                body.put("dtl_adres", dtl_adres.item(0).getChildNodes().item(0).getNodeValue());
                try {
                    body.put("telno", telno.item(0).getChildNodes().item(0).getNodeValue());
                } catch (NullPointerException e) {
                    body.put("telno", "????????????");
                }

                marker.setUserObject(body);
                if (Distance(latitude, longitude, latitude1, longitude1) <= 1000) {
                    if (((Node) nameList.item(0)).getNodeValue().contains("?????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.gym);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.church);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.school);
                    } else {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.etc);
                    }

                    marker.setItemName("????????? : " + ((Node) nameList.item(0)).getNodeValue());
                    body.put("itemname", ((Node) nameList.item(0)).getNodeValue());
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    mMapView.setPOIItemEventListener(piel);
                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);

                }

            }
            if (ia < 6) {
                ia++;
                if (ia == 6) {

                } else {
                    GetXMLTask3 task = new GetXMLTask3();
                    task.execute("http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=" + ia + "&numOfRows=999&type=xml&flag=Y");

                }

            }

            super.onPostExecute(doc);

        }

        // ????????? ????????????
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {

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
            MapPoint ma = null;
            ma = mMapView.getMapCenterPoint();

            String s = "";
            //data????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
            NodeList nodeList = doc.getElementsByTagName("row");
            //data ????????? ????????? ????????? ??????, ???????????? ?????? ????????? ??????
            MapPoint mp = null;
            double latitude;
            double longitude;
            String mngps_nm1;
            String dtl_adres1;

            for (int i = 0; i < nodeList.getLength(); i++) {
                String edit_ad = String.valueOf(edit_address.getText());
                MapPOIItem marker = new MapPOIItem();
                //?????? ???????????? ??????
                s += "" + i + ": ???????????????: ";
                Node node = nodeList.item(i); //data???????????? ??????
                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("vt_acmdfclty_nm");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                s += "?????? = " + ((Node) nameList.item(0)).getNodeValue() + " ,";

                NodeList telno = fstElmnt.getElementsByTagName("mngps_telno");

                NodeList dtl_adres = fstElmnt.getElementsByTagName("dtl_adres");

                NodeList ycord = fstElmnt.getElementsByTagName("ycord");
                s += "ycord = " + ycord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                latitude = Double.parseDouble(ycord.item(0).getChildNodes().item(0).getNodeValue());
                NodeList xcord = fstElmnt.getElementsByTagName("xcord");
                s += "xcord = " + xcord.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                longitude = Double.parseDouble(xcord.item(0).getChildNodes().item(0).getNodeValue());

                if (dtl_adres.item(0).getChildNodes().item(0).getNodeValue().contains(edit_ad)) {
                    HashMap<String, String> body = new HashMap<>();
                    body.put("dtl_adres", dtl_adres.item(0).getChildNodes().item(0).getNodeValue());
                    try {
                        body.put("telno", telno.item(0).getChildNodes().item(0).getNodeValue());
                    } catch (NullPointerException e) {

                        body.put("telno", "????????????");
                    }

                    marker.setUserObject(body);

                    if (((Node) nameList.item(0)).getNodeValue().contains("?????????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.gym);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.church);
                    } else if (((Node) nameList.item(0)).getNodeValue().contains("??????")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.school);
                    } else {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.etc);
                    }
                    marker.setItemName("????????? : " + ((Node) nameList.item(0)).getNodeValue());
                    body.put("itemname", ((Node) nameList.item(0)).getNodeValue());
                    mp = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    marker.setMapPoint(mp);
                    mMapView.setPOIItemEventListener(piel);

                    marker.setShowCalloutBalloonOnTouch(false);
                    mMapView.addPOIItem(marker);

                }

            }
            if (ia < 6) {
                ia++;
                if (ia == 6) {

                } else {
                    GetXMLTask2 task = new GetXMLTask2();
                    task.execute("http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=" + ia + "&numOfRows=999&type=xml&flag=Y");

                }
            }

            super.onPostExecute(doc);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.customballoon_shelter, null);
        }

        public View getCalloutBalloon(MapPOIItem marker) {

            ((TextView) mCalloutBalloon.findViewById(R.id.sheltername)).setText(marker.getItemName());

            HashMap<String, String> body = (HashMap<String, String>) marker.getUserObject();
//            String date = body.get("dtl_adres");
//            ((TextView) mCalloutBalloon.findViewById(R.id.tvaddress)).setText(date);
//            String spin2 = body.get("telno");
//            ((TextView) mCalloutBalloon.findViewById(R.id.tvphone)).setText(spin2);
//
            mCalloutBalloon.findViewById(R.id.tvaddress).setVisibility(View.GONE);
            mCalloutBalloon.findViewById(R.id.tvphone).setVisibility(View.GONE);

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem DEFAULT_MARKER_POINT) {
            return null;
        }

    }


    @Override
    public void onMapViewInitialized(MapView mapView) {
        gpsTracker = new GpsTracker(Shelter.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        MapPoint mp123 = null;
        MapPoint.mapPointWithGeoCoord(latitude, longitude);

        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();
        MapPoint mp1234 = null;
        mp1234 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        //     AllView2(mp1234,1);

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
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        subLayout_2.setVisibility(View.GONE);

        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        param.topMargin = 2175;
        searchbutton.setPadding(45, 0, 15, 0);
        param.gravity = Gravity.CENTER_HORIZONTAL;
        param.rightMargin = 10;

        searchbutton.setLayoutParams(param);

        searchbutton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        MapCircle mapcircle = new MapCircle(mMapView.getMapCenterPoint(), 1100,
                Color.argb(128, 0, 0, 255), // strokeColor
                Color.argb(30, 30, 0, 255));

        mMapView.removeAllCircles();
        mMapView.addCircle(mapcircle);

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);


    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
//        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
//
//        mMapView.setCurrentLocationRadius(780);
//
//        //??? ????????? ??????
//        mMapView.setCurrentLocationRadiusStrokeColor(Color.argb(1,0,0,255));
//        //??? ????????? ??????
//        mMapView.setCurrentLocationRadiusFillColor(Color.argb(128,30,0,255));

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
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            }
            else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(Shelter.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();

                }else {
                    Toast.makeText(Shelter.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Shelter.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Shelter.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(Shelter.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(Shelter.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(Shelter.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Shelter.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
    public void SetSubLayout(MapPOIItem marker) {
        if (marker.getItemName().contains("?????????")) {
            return;
        } else {

//            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT);
//            param.topMargin = 350;
//            searchbutton.setPadding(30, 0, 15, 0);
//            param.gravity = Gravity.CENTER;
////        param.rightMargin=10;
//
//            searchbutton.setLayoutParams(param);

            searchbutton.setVisibility(View.GONE);
            marker.getUserObject();
            HashMap<String, String> body = (HashMap<String, String>) marker.getUserObject();

            String date = body.get("remain_stat");



            sub_title.setText(body.get("itemname"));
            sub_text2.setText(body.get("dtl_adres"));
            sub_text3.setText(body.get("telno"));



        }
    }
    public void CenterSearch(){

        GpsTracker gpsTracker2=new GpsTracker(Shelter.this);

        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();

        double latitude = gpsTracker2.getLatitude();
        double longitude = gpsTracker2.getLongitude();

        MapPoint mp1234 = null;
        mp1234=MapPoint.mapPointWithGeoCoord(latitude,longitude);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mMapView.setMapCenterPoint(mp1234,true);


//                ?????????????????? ??????
        GetXMLTask3 task = new GetXMLTask3();

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://apis.data.go.kr/1741000/EarthquakeIndoors/getEarthquakeIndoorsList?serviceKey=vmsEkjiinmE2KbCAP9TQXX6NwzgYYEDtqh2hjCtFfDWzUQFwKDi%2FF14dHfVqmW0uMP1jdAIcHDj2LOKNfzKlZA%3D%3D&pageNo=1&numOfRows=999&type=xml&flag=Y");

        if(ia==6){
            ia=1;

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