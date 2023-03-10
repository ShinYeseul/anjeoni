package com.test.anjane.covid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonSyntaxException;
import com.test.anjane.GpsTracker;
import com.test.anjane.R;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord;

public class Mask extends Fragment implements MapView.MapViewEventListener, CurrentLocationEventListener {
    private net.daum.mf.map.api.MapView maskmap;
    private GpsTracker gpsTracker;
    EditText search_edit;
    Button search_btn;

    public double latitude;
    private Context mContext;
    public double longitude;
    FloatingActionButton btn_location;
    Button btn_search;
    LinearLayout subLayout;
    LinearLayout subLayout_2;
    TextView sub_title,sub_title2,sub_title3;
    TextView sub_text2,sub_text3;

    //???????????? ?????? ?????????


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;




    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.mask, container, false );

        maskmap = v.findViewById( R.id.mask_map );
        sub_title=v.findViewById(R.id.Sub_title);
        sub_text2=v.findViewById(R.id.Sub_text2);
        sub_text3=v.findViewById(R.id.Sub_text3);
        subLayout_2=v.findViewById(R.id.Sub_layout2);

        sub_title2=v.findViewById(R.id.Sub_title2);
        sub_title3=v.findViewById(R.id.Sub_title3);
        subLayout_2.setVisibility(View.GONE);

        maskmap.setMapViewEventListener(this);
        maskmap.setCurrentLocationEventListener(this);
        maskmap.setCurrentLocationTrackingMode( MapView.CurrentLocationTrackingMode.
                TrackingModeOnWithoutHeadingWithoutMapMoving );
        maskmap.setCalloutBalloonAdapter( new Mask.CustomCalloutBalloonAdapter() );

        search_edit = v.findViewById(R.id.edit_mask);
        search_btn=v.findViewById(R.id.search_btn);
        maskmap.setZoomLevel(1,true);
        search_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                maskmap.removeAllPOIItems();
                maskmap.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                maskmap.setZoomLevel(4,true);
                GetXMLTask3 task = new GetXMLTask3();
                task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "" );

            }
        });
        btn_location = v.findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maskmap.setZoomLevel(1,true);
                maskmap.removeAllCircles();
                GpsTracker gpsTracker = new GpsTracker(mContext);
                double latitude1 = gpsTracker.getLatitude();
                double longitude1 = gpsTracker.getLongitude();
                MapPoint MyLocation_point = null;

                MyLocation_point=MapPoint.mapPointWithGeoCoord(latitude1,longitude1);

                maskmap.setMapCenterPoint(MyLocation_point,true);

                MapCircle centerCirc =
                        new MapCircle(MyLocation_point,200, Color.argb(128, 0, 0, 255),
                                Color.argb(30, 30, 0, 255)  );
                maskmap.addCircle(centerCirc);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                GetXMLTask task2 = new GetXMLTask();
                task2.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "" );
//                maskmap.selectPOIItem(MyLocation_marker,true);

            }
        });

        gpsTracker = new GpsTracker( getActivity() );

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        maskmap.setMapCenterPoint( MapPoint.mapPointWithGeoCoord( latitude, longitude ), true );
        String address = getCurrentAddress( latitude, longitude );


        //????????? ?????? ?????? ??????
        btn_search = (Button) v.findViewById( R.id.searchbutton );
        btn_search.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetXMLTask2 task2 = new GetXMLTask2();
                task2.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "" );
            }
        } );


        // ?????? ???????????? ?????? ?????? ???????????? ??????
//        GetXMLTask task = new GetXMLTask();
//        task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR,"");

        StartFrgm();
        return v;
    }

    MapView.POIItemEventListener piel = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            maskmap.setMapCenterPoint(mapPOIItem.getMapPoint(), true);

            btn_search.setVisibility(View.GONE);

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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //?????? ?????? ????????? ??? ??????
                ;
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.",
                            Toast.LENGTH_LONG).show();

                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????


        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(getActivity(), "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress(double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(getActivity(), "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    public class corona_item {

        private String addr;
        private String code;
        private String created_at;
        private String lat;
        private String lng;
        private String name;
        public String remain_stat;
        private String stock_at;
        private String type;


        public corona_item(String lat, String lng, String addr, String code, String created_at,
                           String name, String remain_stat, String stock_at, String type) {
            this.addr = addr;
            this.code = code;
            this.created_at = created_at;
            this.lat = lat;
            this.lng = lng;
            this.name = name;
            this.remain_stat = remain_stat;
            this.stock_at = stock_at;
            this.type = type;
        }



    }
    //?????? ???????????? ???????????? ???????????? ??????
    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        public String mask_time_str=null;
        Document doc = null;
        @SuppressLint("WrongThread")
        @Override
        protected Document doInBackground(String... urls) {
            String corona_API = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="
                    +latitude+"&lng="+longitude+"&m=200";

            String data = "";
            String myUrl3 = String.format(corona_API);


            try {
                maskmap.removeAllPOIItems();
                URL url1 = new URL(corona_API);
                Log.d("CoronaApi", "The response is :" + url1);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                String line;
                String result = "";

                BufferedReader bf;
                bf = new BufferedReader(new InputStreamReader(url1.openStream()));
                while ((line = bf.readLine()) != null) {
                    result = result.concat(line);

                }
                Log.d("CoronaApi", "The response is :" + result);
                JSONObject root = new JSONObject(result);

                JSONArray coronaArray = root.getJSONArray("stores");
                for (int i = 0; i < coronaArray.length(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    JSONObject item = coronaArray.getJSONObject(i);
                    Log.d("corona", item.getString("name"));
                    corona_item corona_item = new corona_item(
                            item.getString("lat"),
                            item.getString("lng"),
                            item.getString("addr"),
                            item.getString("code"),
                            item.getString("created_at"),
                            item.getString("name"),
                            item.optString("remain_stat","?????????"),
                            item.optString("stock_at","?????????"),
                            item.getString("type")
                    );
                    //           t1_temp.setText(result);

                    // Map_corona.corona_list.add(corona_item);
                    System.out.println(corona_item.addr);
                    System.out.println(corona_item.lat);
                    System.out.println(corona_item.lng);

                    //???????????? ?????? ??????
                    //  ?????? : 2020/07/01 08:56:00      > 18??????  0 ~ 19


                    mask_time_str = corona_item.created_at;

                    String lat1 = corona_item.lat;
                    String lng1 = corona_item.lng;
                    lat1=lat1.replaceAll("\"","");
                    lng1=lng1.replaceAll("\"","");

                    double lat = Double.parseDouble(lat1.trim());
                    double lng = Double.parseDouble(lng1.trim());


                    HashMap<String, String> body = new HashMap<>();
                    body.put("update_time",mask_time_str);
                    body.put("name",corona_item.name);
                    body.put("remain_stat",corona_item.remain_stat.trim());
                    body.put("stock_at",corona_item.stock_at);
                    marker.setUserObject(body);
                    //textView.setText(corona_item.created_at);
                    MapPoint mp=mapPointWithGeoCoord(lat,lng);
                    marker.setItemName(corona_item.name);

                    marker.setMapPoint(mp);
                    if (corona_item.remain_stat.contains("plenty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_gr);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_grb);

                    } else if (corona_item.remain_stat.contains("some")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_y);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_yb);

                    } else if (corona_item.remain_stat.contains("few")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_r);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_rb);

                    } else if (corona_item.remain_stat.contains("empty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);

                    }else if (corona_item.remain_stat.contains("break")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    else  {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    System.out.println(lat+"\n"+lng);

                    maskmap.setPOIItemEventListener(piel);


                    marker.setShowCalloutBalloonOnTouch(false);
                    maskmap.addPOIItem(marker);


                }

            } catch (NullPointerException | JsonSyntaxException | JSONException | MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //      mask_time.setText( "???????????? ?????? : "+mask_time_str );

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {







            super.onPostExecute(doc);

        }


    }
    //?????? ???????????? ?????? ????????? ???????????? ??????
    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {

        Document doc = null;
        @SuppressLint("WrongThread")
        @Override
        protected Document doInBackground(String... urls) {

            MapPoint centerPoint=null;

            centerPoint=maskmap.getMapCenterPoint();
            maskmap.removeAllPOIItems();
            String corona_API = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="
                    +centerPoint.getMapPointGeoCoord().latitude+"&lng="+centerPoint.getMapPointGeoCoord().longitude+"&m=200";

            String data = "";
            String myUrl3 = String.format(corona_API);


            try {
                URL url1 = new URL(corona_API);
                Log.d("CoronaApi", "The response is :" + url1);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                String line;
                String result = "";

                BufferedReader bf;
                bf = new BufferedReader(new InputStreamReader(url1.openStream()));
                while ((line = bf.readLine()) != null) {
                    result = result.concat(line);

                }
                Log.d("CoronaApi", "The response is :" + result);
                JSONObject root = new JSONObject(result);

                JSONArray coronaArray = root.getJSONArray("stores");
                for (int i = 0; i < coronaArray.length(); i++) {
                    JSONObject item = coronaArray.getJSONObject(i);
                    Log.d("corona", item.getString("name"));
                    corona_item corona_item = new corona_item(
                            item.getString("lat"),
                            item.getString("lng"),
                            item.getString("addr"),
                            item.getString("code"),
                            item.getString("created_at"),
                            item.getString("name"),
                            item.optString("remain_stat","?????????"),
                            item.optString("stock_at","?????????"),
                            item.getString("type")
                    );
                    //           t1_temp.setText(result);

                    // Map_corona.corona_list.add(corona_item);
                    System.out.println(corona_item.addr);
                    System.out.println(corona_item.lat);
                    System.out.println(corona_item.lng);


                    String lat1 = corona_item.lat;
                    String lng1 = corona_item.lng;
                    lat1=lat1.replaceAll("\"","");
                    lng1=lng1.replaceAll("\"","");

                    double lat = Double.parseDouble(lat1.trim());
                    double lng = Double.parseDouble(lng1.trim());

                    MapPOIItem marker = new MapPOIItem();
                    HashMap<String, String> body = new HashMap<>();
                    body.put("name",corona_item.name);

                    body.put("remain_stat", item.optString("remain_stat","?????????"));
                    body.put("stock_at", item.optString("stock_at","?????????"));
                    marker.setUserObject(body);

                    MapPoint mp=mapPointWithGeoCoord(lat,lng);
                    marker.setItemName(corona_item.name);

                    marker.setMapPoint(mp);
                    if (corona_item.remain_stat.contains("plenty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_gr);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_grb);

                    } else if (corona_item.remain_stat.contains("some")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_y);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_yb);

                    } else if (corona_item.remain_stat.contains("few")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_r);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_rb);

                    } else if (corona_item.remain_stat.contains("empty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);

                    }else if (corona_item.remain_stat.contains("break")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    else  {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    marker.setItemName(corona_item.name);
                    maskmap.setPOIItemEventListener(piel);

                    marker.setMapPoint(mp);

                    marker.setShowCalloutBalloonOnTouch(false);
                    maskmap.addPOIItem(marker);

                }

            } catch (NullPointerException | JsonSyntaxException | JSONException | MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return doc;
        }

        @Override
        protected void onPostExecute(Document doc){
            super.onPostExecute(doc);
        }

    }
    //????????????
    private class GetXMLTask3 extends AsyncTask<String, Void, Document> {
        final Geocoder geocoder = new Geocoder(mContext);
        Document doc = null;
        @SuppressLint("WrongThread")
        @Override
        protected Document doInBackground(String... urls) {
            List<Address> list = null;
            double searchlat = 0,searchlon = 0;

            String str =  String.valueOf(search_edit.getText());

            Log.d( "Re", "position" + str );
            try {
                list = geocoder.getFromLocationName( str, 5 );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e( "test", "????????? ??????- ???????????? ??????????????? ??????" );
            }

            if (list != null) {
                if (list.size() == 0) {
                    System.out.println( "???????????? ?????? ????????? ??????." );
                } else {
                    Address addr = list.get(0);
                    searchlat = addr.getLatitude();
                    searchlon = addr.getLongitude();
                    maskmap.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(searchlat,searchlon),true);


                }
            }
            String corona_API = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="
                    +searchlat+"&lng="+searchlon+"&m=2000";



            try {
                URL url1 = new URL(corona_API);
                Log.d("CoronaApi", "The response is :" + url1);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                String line;
                String result = "";

                BufferedReader bf;
                bf = new BufferedReader(new InputStreamReader(url1.openStream()));
                while ((line = bf.readLine()) != null) {
                    result = result.concat(line);

                }
                Log.d("CoronaApi", "The response is :" + result);
                JSONObject root = new JSONObject(result);

                JSONArray coronaArray = root.getJSONArray("stores");
                for (int i = 0; i < coronaArray.length(); i++) {
                    MapPOIItem marker = new MapPOIItem();
                    JSONObject item = coronaArray.getJSONObject(i);
                    Log.d("corona", item.getString("name"));
                    corona_item corona_item = new corona_item(
                            item.getString("lat"),
                            item.getString("lng"),
                            item.getString("addr"),
                            item.getString("code"),
                            item.getString("created_at"),
                            item.getString("name"),
                            item.optString("remain_stat","?????????"),
                            item.optString("stock_at","?????????"),
                            item.getString("type")
                    );
                    //           t1_temp.setText(result);

                    // Map_corona.corona_list.add(corona_item);
                    System.out.println(corona_item.addr);
                    System.out.println(corona_item.lat);
                    System.out.println(corona_item.lng);


                    String lat1 = corona_item.lat;
                    String lng1 = corona_item.lng;
                    lat1=lat1.replaceAll("\"","");
                    lng1=lng1.replaceAll("\"","");

                    double lat = Double.parseDouble(lat1.trim());
                    double lng = Double.parseDouble(lng1.trim());


                    HashMap<String, String> body = new HashMap<>();
                    body.put("name",corona_item.name);

                    body.put("remain_stat",corona_item.remain_stat.trim());
                    body.put("stock_at",corona_item.stock_at);
                    marker.setUserObject(body);
                    //textView.setText(corona_item.created_at);
                    MapPoint mp=mapPointWithGeoCoord(lat,lng);
                    marker.setItemName(corona_item.name);

                    marker.setMapPoint(mp);
                    if (corona_item.remain_stat.contains("plenty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_gr);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_grb);

                    } else if (corona_item.remain_stat.contains("some")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_y);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_yb);

                    } else if (corona_item.remain_stat.contains("few")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_r);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_rb);

                    } else if (corona_item.remain_stat.contains("empty")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);

                    }else if (corona_item.remain_stat.contains("break")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    else  {
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.mask_g);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomSelectedImageResourceId(R.drawable.mask_gb);
                    }
                    System.out.println(lat+"\n"+lng);

                    maskmap.setPOIItemEventListener(piel);


                    marker.setShowCalloutBalloonOnTouch(false);
                    maskmap.addPOIItem(marker);


                }

            } catch (NullPointerException | JsonSyntaxException | JSONException | MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {







            super.onPostExecute(doc);

        }


    }
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate( R.layout.customballoon_mask, null );
        }

        public View getCalloutBalloon(MapPOIItem mDefaultMarker) {

            ((TextView) mCalloutBalloon.findViewById( R.id.name )).setText( mDefaultMarker.getItemName() );

//            HashMap<String, String> body = (HashMap<String, String>) mDefaultMarker.getUserObject();
//            String date = body.get( "remain_stat" );
//            switch (date) {
//                case "plenty": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "100?????????" );
//                    break;
//                }
//                case "some": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "30??? ?????? 100??? ??????" );
//                    break;
//                }
//                case "few": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "2??? ?????? 30??? ??????" );
//                    break;
//                }
//                case "empty": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "1??? ??????" );
//                    break;
//                }
//                case "break": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "?????? ??????" );
//                    break;
//                }
//                default: {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.remain_stat )).setText( "?????? ??????" );
//                }
//            }
//
//            String date2 = body.get( "stock_at" );
//            ((TextView) mCalloutBalloon.findViewById( R.id.stock_at )).setText(date2);
//            switch (date) {
//                case "null": {
//                    ((TextView) mCalloutBalloon.findViewById( R.id.stock_at )).setText( "?????? ??????" );
//                    break;
//                }
//            }
            return mCalloutBalloon;

        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem DEFAULT_MARKER_POINT) {
            return null;
        }

    }
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//        MapCircle mapcircle= new MapCircle(maskmap.getMapCenterPoint(),1100, Color.argb(128, 0, 0, 255),
//                Color.argb(30, 30, 0, 255));
//        maskmap.setCurrentLocationRadiusStrokeColor(Color.argb(1,0,0,255));
//        //??? ????????? ??????
//        maskmap.setCurrentLocationRadiusFillColor(Color.argb(128,30,0,255));
//
//        maskmap.setCurrentLocationRadius( 800 );

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
        subLayout_2.setVisibility(View.GONE);

//        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        param.topMargin = 130;
//
//        btn_search.setLayoutParams(param);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        param.topMargin = 2070;
        btn_search.setPadding(30,0,15,0);
        param.gravity=Gravity.CENTER_HORIZONTAL;
        param.rightMargin=10;

        btn_search.setLayoutParams(param);

        btn_search.setVisibility(View.VISIBLE);


        maskmap.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        maskmap.removeAllCircles();

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        MapCircle movingcircle= new MapCircle(maskmap.getMapCenterPoint(),200, Color.argb(128, 0, 0, 255),
                Color.argb(30, 30, 0, 255)  );


        maskmap.addCircle(movingcircle);

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    @Override

    public void onAttach(Context context) {

        super.onAttach(context);

        mContext = context;

    }


    public void SetSubLayout(MapPOIItem marker) {
        if (marker.getItemName().contains("?????????")) {
            return;
        } else {



            btn_search.setVisibility(View.GONE);
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = 440;
            btn_search.setPadding(30, 0, 15, 0);
            param.gravity = Gravity.CENTER;
//        param.rightMargin=10;

            btn_search.setLayoutParams(param);


            marker.getUserObject();
            HashMap<String, String> body = (HashMap<String, String>) marker.getUserObject();

            String date = body.get("remain_stat");



            sub_title.setText(body.get("name"));
            sub_text3.setText(body.get("stock_at"));

            switch (date) {
                case "plenty": {
                    date = "100??? ??????";
                    sub_text2.setText(date);
                    break;
                }
                case "some": {
                    date = "30??? ?????? 100??? ??????";
                    sub_text2.setText(date);
                    break;
                }
                case "few": {
                    date = "2??? ?????? 30??? ??????";
                    sub_text2.setText(date);
                    break;
                }
                case "empty": {
                    date = "1??? ??????";
                    sub_text2.setText(date);
                    break;
                }
                case "break": {
                    date = "?????? ??????";
                    sub_text2.setText(date);
                    break;
                }
                default: {
                    date = "?????? ??????";
                    sub_text2.setText(date);

                }



            }
        }

    }

    public void StartFrgm(){
        GpsTracker gpsTracker = new GpsTracker(mContext);
        double latitude1 = gpsTracker.getLatitude();
        double longitude1 = gpsTracker.getLongitude();
        MapPoint MyLocation_point = null;

        MyLocation_point=MapPoint.mapPointWithGeoCoord(latitude1,longitude1);

        maskmap.setMapCenterPoint(MyLocation_point,true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        maskmap.setZoomLevel(1,true);
        //??????
        GetXMLTask task2 = new GetXMLTask();
        task2.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "" );
        //???????????? ?????? ??????

    }

}