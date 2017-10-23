package com.diordna.whats.near.me.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diordna.whats.near.me.R;
import com.diordna.whats.near.me.model.WeatherResponseModel;
import com.diordna.whats.near.me.restservice.ApiClient;
import com.diordna.whats.near.me.restservice.ApiInterface;
import com.diordna.whats.near.me.utilities.AlertDialogManager;
import com.diordna.whats.near.me.utilities.ConnectionDetector;
import com.diordna.whats.near.me.utilities.Constant;
import com.diordna.whats.near.me.utilities.GPSTracker;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends ActionBarActivity {
    //Initialize text views used to set fonts..
    //Button hospital,education,shopping,sports,travel,services;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private TextView mPlaceName;
    private TextView mTemp;
    private TextView mDate;
    private TextView mTempCondition;

    private LinearLayout ll_placeInfo;
    private LinearLayout ll_loaderror;
    private ImageButton btnSync;

    MaterialSearchView searchView;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    WeatherResponseModel wResponse;
    GPSTracker gps;
    FloatingActionButton btnShowOnMap;
    ProgressDialog pDialog;
    MaterialDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar_main); // Attaching the layout to the toolbar_main object
        setSupportActionBar(toolbar);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);


        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        //find drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);

        ll_placeInfo=(LinearLayout)findViewById(R.id.ll_placeInfo);
        ll_loaderror=(LinearLayout)findViewById(R.id.ll_error);
        btnSync=(ImageButton)findViewById(R.id.btn_sync);

        mPlaceName=(TextView)findViewById(R.id.txt_city_name);
        mTemp=(TextView)findViewById(R.id.txt_temp);
        mDate=(TextView)findViewById(R.id.txt_date);
        mTempCondition=(TextView)findViewById(R.id.txt_temp_condition);
        loadPlaceTempInfo();

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlaceTempInfo();
            }
        });




    }
    private void HideControl(){
        ll_loaderror.setVisibility(View.VISIBLE);
        ll_placeInfo.setVisibility(View.GONE);
    }

    private void VisibleControl(){
        ll_placeInfo.setVisibility(View.VISIBLE);
        ll_loaderror.setVisibility(View.GONE);
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    //Setting up the drawer content
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //selectDrawerItem(menuItem);
                        switch(menuItem.getItemId()) {
                            case R.id.review_menu:
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                break;
                            case R.id.settings_menu:
                                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                                break;
                            case R.id.share_menu:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Nearest Places App for your smartphone. Download it today from -----link-----");
                                shareIntent.setType("text/plain");
                                startActivity(shareIntent);
                                break;
                            case R.id.info_menu:

                                break;
                            default:
                                //fragmentClass = FirstFragment.class;
                        }
                        return true;
                    }
                });

    }
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // newText is text entered by user to SearchView
            Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
            return false;
        }
    };
    public void onHomeClick(View v) {
        PlaceListActivity.PLACES_TYPE = "funeral_home";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "1");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Home");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"All homes");
        startActivity(in);
    }
    public void onSchoolClick(View v) {
        PlaceListActivity.PLACES_TYPE = "school|university|library|physiotherapist";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "2");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Educational Inst.");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"School, College, University etc.");
        startActivity(in);
    }
    public void onHospitalClick(View v) {
        PlaceListActivity.PLACES_TYPE = "health|hospital|pharmacy|doctor|dentist";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "3");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Hospital");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Every Medical Institution");
        startActivity(in);
    }
    public void onPoliceStationClick(View v) {
        PlaceListActivity.PLACES_TYPE = "police";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "4");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Police Station");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Police Station");
        startActivity(in);
    }
    public void onPatrolStationClick(View v) {
        PlaceListActivity.PLACES_TYPE = "gas_station";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "5");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Patrol Station");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Patrol and Fuel Station");
        startActivity(in);
    }
    public void onTakeAwayPlaceClick(View v) {
        PlaceListActivity.PLACES_TYPE = "meal_delivery|meal_takeaway";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "6");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Take Away");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Take Away Food Stores");
        startActivity(in);
    }
    public void onRestaurantClick(View v) {
        PlaceListActivity.PLACES_TYPE = "restaurant|meal_delivery|bakery|cafe";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "7");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Restaurant");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Place of Food Heaven");
        startActivity(in);
    }
    public void onTravelClick(View v) {
        PlaceListActivity.PLACES_TYPE = "train_station|travel_agency|subway_station|embassy";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "8");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Travel");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Travelling Agencies");
        startActivity(in);
    }
    public void onBusStationClick(View v) {
        PlaceListActivity.PLACES_TYPE = "bus_station";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "9");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Bus Station");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Place to Ride");
        startActivity(in);
    }
    public void onAirportClick(View v) {
        PlaceListActivity.PLACES_TYPE = "airport";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "10");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Airport");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Place to Fly High!");
        startActivity(in);
    }
    public void onLibraryClick(View v) {
        PlaceListActivity.PLACES_TYPE = "library";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "11");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Library");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Worlds of Books");
        startActivity(in);
    }
    public void onPortClick(View v) {
        PlaceListActivity.PLACES_TYPE = "";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "12");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Port");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Port");
        startActivity(in);
    }
    public void onGymClick(View v) {
        PlaceListActivity.PLACES_TYPE = "gym";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "13");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Gymnasium");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Body Fitness Centers");
        startActivity(in);
    }
    public void onCarWashClick(View v) {
        PlaceListActivity.PLACES_TYPE = "car_wash|car_repair";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "14");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Car Wash");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Car Wash or Repair");
        startActivity(in);
    }
    public void onJobsClick(View v) {
        PlaceListActivity.PLACES_TYPE = "";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "15");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Jobs");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Recruiter Institution");
        startActivity(in);
    }
    public void onVeichleTradeClick(View v) {
        PlaceListActivity.PLACES_TYPE = "car_rental|car_dealer";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "16");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Veichle Trade");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Transport Rental or Dealer");
        startActivity(in);
    }
    public void onPropertyClick(View v) {
        PlaceListActivity.PLACES_TYPE = "real_estate_agency";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "17");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Property");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Real Estate Agency");
        startActivity(in);
    }
    public void onGarageClick(View v) {
        PlaceListActivity.PLACES_TYPE = "parking";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "18");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Garage");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Garage/Parking Place");
        startActivity(in);
    }
    public void onPostOfficeClick(View v) {
        PlaceListActivity.PLACES_TYPE = "post_office";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "19");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Post Office");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Postal Institute");
        startActivity(in);
    }
    public void onFinanceClick(View v) {
        PlaceListActivity.PLACES_TYPE = "accounting|bank|atm|finance";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "20");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Financial Inst.");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Bank, ATM and Financial Institution");
        startActivity(in);
    }
    public void onShoppingClick(View v) {
        PlaceListActivity.PLACES_TYPE = "bicycle_store|book_store|clothing_store|convenience_store|department_store|electronics_store|furniture_store|hardware_store|home_goods_store|jewelry_store|liquor_store|pet_store|shoe_store|shopping_mall";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "21");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Shopping Center");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Shopping Center and Store");
        startActivity(in);
    }
    public void onSportsClick(View v) {
        PlaceListActivity.PLACES_TYPE = "gym|stadium";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "22");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Sports");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Stadium");
        startActivity(in);
    }
    public void onReligionClick(View v) {
        PlaceListActivity.PLACES_TYPE = "church|hindu_temple|mosque";
        Intent in = new Intent(getApplicationContext(),PlaceListActivity.class);
        in.putExtra(Constant.KEY_PLACELIST_TYPE_ID, "23");
        in.putExtra(Constant.KEY_PLACELIST_TYPE, "Religious Institution");
        in.putExtra(Constant.KEY_PLACELIST_TYPE_BRIEF,"Church, Temple, Mosque etc.");
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //when home icon is clicked
        if(id == R.id.action_search) {

            return true;
        }

        if(id == R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        //When Settings Menu is clicked
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        if (id == R.id.share_app) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Nearest Places App for your smartphone. Download it today from -----link-----");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPlaceTempInfo(){
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            HideControl();
            mDialog = new MaterialDialog.Builder(this)
                    .title("Connection Error")
                    .content("Could not connect to the internet check network settings and try again")
                    .positiveText("SETTINGS")
                    .negativeText("Exit")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    })
                    .show();
            return;
        }
        else {
            gps = new GPSTracker(this);

            // check if GPS location can get
            if (gps.canGetLocation()) {
                final DateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
                final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                final Date weatherDate = new Date();
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                GPSTracker mGPSTracker = new GPSTracker(this);
                if (mGPSTracker.canGetLocation()) {
                    Log.i("requestSend", "request approached");
                    String locationLong = String.valueOf(mGPSTracker.getLongitude());
                    String locationLat = String.valueOf(mGPSTracker.getLatitude());
                    Call<WeatherResponseModel> call = apiService.getWeatherByLongLat(locationLong, locationLat, Constant.REST_WEATHER_API_KEY, Constant.SYS_WEATHER_UNIT);
                    call.enqueue(new Callback<WeatherResponseModel>() {
                        @Override
                        public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {
                            //List<WeatherResponseModel> movies = response.body().getResults();
                            WeatherResponseModel weather = response.body();
                            mDate.setText(dateFormat.format(weatherDate));
                            mPlaceName.setText(weather.getName());
                            mTemp.setText(weather.getMain().getTemp()+ "˚c");
                            mTempCondition.setText(weather.getWeather()[0].getDescription());
                            VisibleControl();


                        }

                        @Override
                        public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                            HideControl();
                        }
                    });
                }
                Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
            } else {
                HideControl();
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
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        })
                        .show();
                return;
            }
        }
    }

}
