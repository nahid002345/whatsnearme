package com.diordna.whats.near.me.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diordna.whats.near.me.R;
import com.diordna.whats.near.me.header.HeaderView;
import com.diordna.whats.near.me.utilities.AlertDialogManager;
import com.diordna.whats.near.me.utilities.ConnectionDetector;
import com.diordna.whats.near.me.utilities.Constant;
import com.diordna.whats.near.me.utilities.GPSTracker;
import com.diordna.whats.near.me.utilities.GooglePlaces;
import com.diordna.whats.near.me.model.Place;
import com.diordna.whats.near.me.utilities.PlaceListViewAdapter;
import com.diordna.whats.near.me.model.PlacesList;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PlaceListActivity extends ActionBarActivity implements AppBarLayout.OnOffsetChangedListener{

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    GooglePlaces googlePlaces;
    PlacesList nearPlaces;
    GPSTracker gps;
    FloatingActionButton btnShowOnMap;
    ProgressDialog pDialog;
    MaterialDialog mDialog;
    ListView lv;
    ArrayList<Place> placesListItems = new ArrayList<Place>();

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name
    public static String PLACES_TYPE; // Listing places only cafes, restaurants
    public static int radius;

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.img_place_type)
    protected ImageView imgPlaceType;

    private boolean isHideToolbarView = false;
    private String listtypeid="";
    private String listtype="";
    private String listTypeDescription="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();
        loadList();
    }
    private void loadList(){
        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            mDialog = new MaterialDialog.Builder(this)
                    .title("Connection Error")
                    .content("Could not connect to the internet check network settings and try again")
                    .positiveText("SETTINGS")
                    .negativeText("BACK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    })
                    .show();
            return;
        }

        // creating GPS Class object
        gps = new GPSTracker(this);

        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            mDialog = new MaterialDialog.Builder(this)
                    .title("Location Error")
                    .content("Could not get user Location check GPS settings and try again")
                    .positiveText("SETTINGS")
                    .negativeText("BACK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    })
                    .show();
            return;
        }

        // Getting listview
        lv = (ListView) findViewById(R.id.list);


        /*// button show on map
        btnShowOnMap = (FloatingActionButton) findViewById(R.id.btn_show_map);

        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        new LoadPlaces().execute();

        *//** Button click event for shown on map *//*
        btnShowOnMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(),
                        MapPane.class);
                // Sending user current geo location
                i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
                i.putExtra("user_longitude", Double.toString(gps.getLongitude()));

                // passing near places to map activity
                i.putExtra("near_places", nearPlaces);
                // staring activity
                startActivity(i);
            }
        });
*/

        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        new LoadPlaces().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.txt_place_ref)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
    }
    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
        Intent intntFrmMain = getIntent();

        listtypeid=intntFrmMain.getStringExtra(Constant.KEY_PLACELIST_TYPE_ID);
        listtype=intntFrmMain.getStringExtra(Constant.KEY_PLACELIST_TYPE);
        listTypeDescription= intntFrmMain.getStringExtra(Constant.KEY_PLACELIST_TYPE_BRIEF);

        toolbarHeaderView.bindTo(listtype,listTypeDescription);
        floatHeaderView.bindTo(listtype, listTypeDescription);

        switch (listtypeid){
            case "1":
                imgPlaceType.setImageResource(R.drawable.header_home);
                break;
            case "2":
                imgPlaceType.setImageResource(R.drawable.header_education);
                break;
            case "3":
                imgPlaceType.setImageResource(R.drawable.header_hospital);
                break;
            case "4":
                imgPlaceType.setImageResource(R.drawable.header_police);
                break;
            case "5":
                imgPlaceType.setImageResource(R.drawable.header_fuel_station);
                break;
            case "6":
                imgPlaceType.setImageResource(R.drawable.header_take_away);
                break;
            case "7":
                imgPlaceType.setImageResource(R.drawable.header_resturant);
                break;
            case "8":
                imgPlaceType.setImageResource(R.drawable.header_home);
                break;
            case "9":
                imgPlaceType.setImageResource(R.drawable.header_bus_station);
                break;
            case "10":
                imgPlaceType.setImageResource(R.drawable.header_airport);
                break;
            case "11":
                imgPlaceType.setImageResource(R.drawable.header_library);
                break;
            case "12":
                imgPlaceType.setImageResource(R.drawable.header_home);
                break;
            case "13":
                imgPlaceType.setImageResource(R.drawable.header_home);
                break;
            case "14":
                break;
            case "15":
                break;
            case "16":
                break;
            case "17":
                break;
            case "18":
                break;
            case "19":
                break;
            case "20":
                break;
            case "21":
                break;
            case "22":
                break;
            case "23":
                break;
            default:
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    class LoadPlaces extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(HospitalListActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Searching</b><br/>Please Wait"));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show(); */

            mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                    .content(R.string.dialog_title)
                    .widgetColorRes(R.color.accentColor)
                    .progress(true, 0)
                    .show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();

            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                //

                // Radius in meters - increase this value if you don't find any places
                radius = 10000; // 1000 meters

                // get nearest places
                nearPlaces = googlePlaces.search(gps.getLatitude(),
                        gps.getLongitude(), radius, PLACES_TYPE);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            mDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;
                    Place nearPlace;
                    // Check for all possible status
                    if (status.equals("OK")) {
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Place p : nearPlaces.results) {
                                /*HashMap<String, String> map = new HashMap<String, String>();

                                // Place reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.reference);

                                // Place name
                                map.put(KEY_NAME, p.name);*/


                                // adding HashMap to ArrayList
                                placesListItems.add(p);
                            }
                            // list adapter
                            PlaceListViewAdapter adapter = new PlaceListViewAdapter(PlaceListActivity.this, R.layout.hospital_list_item,placesListItems);

                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                    } else if (status.equals("ZERO_RESULTS")) {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Sorry no places found. Try to increase search radius from settings")
                                .show();
                    } else if (status.equals("UNKNOWN_ERROR")) {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Sorry unknown error occured. Try again")
                                .show();
                    } else if (status.equals("OVER_QUERY_LIMIT")) {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Query over limit. Check back in a few minutes")
                                .show();
                    } else if (status.equals("REQUEST_DENIED")) {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Sorry error occured. Request denied")
                                .show();
                    } else if (status.equals("INVALID_REQUEST")) {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Sorry error occured. Request invalid")
                                .show();
                    } else {
                        mDialog = new MaterialDialog.Builder(PlaceListActivity.this)
                                .title("Near Places")
                                .content("Sorry, Unknown error occured")
                                .show();
                    }
                }
            });

        }

    }
}

