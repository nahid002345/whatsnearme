package com.diordna.whats.near.me.ui.activities;

/**
 * Created by Encripted on 2/24/2015.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diordna.whats.near.me.R;
import com.diordna.whats.near.me.model.PlaceDetails;
import com.diordna.whats.near.me.utilities.AlertDialogManager;
import com.diordna.whats.near.me.utilities.ConnectionDetector;
import com.diordna.whats.near.me.utilities.GooglePlaces;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SinglePlaceActivity extends ActionBarActivity implements OnMapReadyCallback {
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Place Details
    PlaceDetails placeDetails;

    // Progress dialog
    MaterialDialog materialDialog;

    //Map fragment
    GoogleMap singleMap = null;

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_place);

        //Activity toolbar_main setup
        toolbar = (Toolbar) findViewById(R.id.toolbar_main); // Attaching the layout to the toolbar_main object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((MapFragment) getFragmentManager().findFragmentById(R.id.mapSingle)).getMapAsync(this);

        Intent i = getIntent();

        // Place referece id
        String reference = i.getStringExtra(KEY_REFERENCE);

        // Calling a Async Background thread
        new LoadSinglePlaceDetails().execute(reference);
    }

    public void onPhoneClick(View v) {
        String phoneNumber=((TextView) v.findViewById(R.id.phone)).getText().toString().trim();
        if(!phoneNumber.isEmpty() && !phoneNumber.toLowerCase().contains("not present")){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            getApplicationContext().startActivity(intent);
        }
        else{
            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                    "Sorry no phone number found.",
                    false);
        }
    }

    public void onWebsiteClick(View v) {
        String url = ((TextView) v.findViewById(R.id.txt_website)).getText().toString().trim();
        if(!url.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else{
            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                    "Sorry no web address found.",
                    false);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //When Settings Menu is clicked
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        singleMap=googleMap;
    }

    /**
     * Background Async Task to Load Google places
     * */
    class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            materialDialog = new MaterialDialog.Builder(SinglePlaceActivity.this)
                    .content(R.string.dialog_title)
                    .widgetColorRes(R.color.accentColor)
                    .progress(true, 0)
                    .show();
        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... args) {
            String reference = args[0];

            // creating Places class object
            googlePlaces = new GooglePlaces();

            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getPlaceDetails(reference);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            materialDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    if(placeDetails != null){
                        String status = placeDetails.status;

                        // check place deatils status
                        // Check for all possible status
                        if(status.equals("OK")){
                            if (placeDetails.result != null) {
                                String name = placeDetails.result.name;
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String website="";
                                double latitude = placeDetails.result.geometry.location.lat;
                                double longitude = placeDetails.result.geometry.location.lng;

                                Log.d("Place ", name + address + phone + latitude + longitude);

                                // Displaying all the details in the view
                                // single_place.xml
                                TextView lbl_name = (TextView) findViewById(R.id.name);
                                TextView lbl_address = (TextView) findViewById(R.id.address);
                                TextView lbl_phone = (TextView) findViewById(R.id.phone);
                                TextView lbl_web = (TextView) findViewById(R.id.txt_website);

                                final LatLng location = new LatLng(latitude,longitude); //locatoin co-ordinates for the zoom

                                singleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_accent)));

                                // Move the camera instantly to location with a zoom of 100.
                                singleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));

                                // Zoom in, animating the camera.
                                singleMap.animateCamera(CameraUpdateFactory.zoomIn());

                                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                singleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                                // Check for null data from google
                                // Sometimes place details might missing
                                name = name == null ? "Not present" : name; // if name is null display as "Not present"
                                address = address == null ? "Not present" : address;
                                phone = phone == null ? "Not present" : phone;
                                //latitude = latitude == null ? "Not present" : latitude;
                                //longitude = longitude == null ? "Not present" : longitude;

                                lbl_name.setText(name);
                                lbl_address.setText(address);
                                lbl_phone.setText(phone);
                                lbl_web.setText(website);
                            }
                        }
                        else if(status.equals("ZERO_RESULTS")){
                            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                                    "Sorry no place found.",
                                    false);
                        }
                        else if(status.equals("UNKNOWN_ERROR"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                        }
                        else if(status.equals("OVER_QUERY_LIMIT"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        }
                        else if(status.equals("REQUEST_DENIED"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        }
                        else if(status.equals("INVALID_REQUEST"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        }
                        else
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                        }
                    }else{
                        alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }


                }
            });

        }

    }

}
