package com.galen.opennotes;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.galen.opennotes.Notes.NotesListActivity;
import com.galen.opennotes.cautions.CautionDBopenHelper;
import com.galen.opennotes.cautions.Cautions;
import com.galen.opennotes.cautions.CautionsViewAdapter;
import com.galen.opennotes.myrecords.MyRecordsActivity;
import com.galen.opennotes.weather.Example;
import com.galen.opennotes.weather.OpenWeatherInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int RC_SIGN_IN = 123;
    private static final int RC_SIGN_UP = 1231;
    public static final int REQUEST_LOCATION = 10;
    private TextView mUserNameView, mUserEmailView, mWeatherStatusView, mSuggestionView, mCurrentTemp, mLowTemp, mHighTemp;
    private ImageView mWeatherImage;
    private FirebaseAuth mAuth;
    private String mUserName, mUserEmail, mLat, mLon;
    private CautionsViewAdapter mCautionViewAdapter;
    private RecyclerView mRecyclerView;
    private List<Cautions> mCautionList;
    private Button mCautionEditButton, mNotesButton, mMyrecordsButton, mLogOutButton;
    private static final String BASE_URL = "http://api.openweathermap.org/";
    private GoogleApiClient mGoogleApiClient;
    private NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mCurrentTemp = (TextView) findViewById(R.id.temperature_current);
        mLowTemp = (TextView) findViewById(R.id.low_temperature);
        mHighTemp = (TextView) findViewById(R.id.high_temperature);


        // get the cautionlist from the caution edittexview(user inputs)
        // set up the recyclerview adapter for the cautions:


        mCautionEditButton = (Button) findViewById(R.id.edit_caution_button);
        mCautionEditButton.setOnClickListener(this);
        mWeatherStatusView = (TextView) findViewById(R.id.weather_status);
        mMyrecordsButton = (Button) findViewById(R.id.my_records);
        mMyrecordsButton.setOnClickListener(this);


        userLogin();

        // set up googleclient to get the current location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        //recyclerview for cautions
        mRecyclerView = (RecyclerView) findViewById(R.id.suggestion_recyclerview);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mCautionList = CautionDBopenHelper.getInstance(this).getCautions();

        mCautionViewAdapter = new CautionsViewAdapter(mCautionList, this);

        mRecyclerView.setAdapter(mCautionViewAdapter);

        //start the notes activity by clcking the notes button
        mNotesButton = (Button) findViewById(R.id.notes_button);
        mNotesButton.setOnClickListener(this);

        setUpUserInformation();


    }


    private void userLogin() {

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

        } else {
            // not signed in

            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                            .build(),
                    RC_SIGN_UP);

        }
    }


    private void setUpUserInformation() {

        mUserNameView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserEmailView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_email_address);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String name = user.getDisplayName();
            String email = user.getEmail();
            mUserNameView.setText(name);
            mUserEmailView.setText(email);

        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                        }
                    });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//
////            // if you click on the camera, it will take you to the noteconent activity which is new notes
////            Intent intent =new Intent( this, NoteContentActivity.class);
////            startActivity(intent);
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else
        if (id == R.id.na_logout) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_caution_button:
                // start a dialog ask the user to input the cautions

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add cautions");

                //make a customer layout for dialog
                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                layout.addView(input);

                builder.setView(layout);
                builder.setPositiveButton("Save", null);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add the cautions in the local database, then show it on the recylerview.

                        String cautions = input.getText().toString();

                        // use the time to create a unique id
                        long theid = System.currentTimeMillis();
                        int id = (int) theid;

                        CautionDBopenHelper.getInstance(getApplicationContext()).insertCautions(id, cautions);

                        mCautionList = CautionDBopenHelper.getInstance(getApplicationContext()).getCautions();

                        mCautionViewAdapter = new CautionsViewAdapter(mCautionList, getApplicationContext());

                        mRecyclerView.setAdapter(mCautionViewAdapter);
                        dialog.dismiss();


                    }
                });

                break;

            case R.id.notes_button:
                Intent intent = new Intent(MainActivity.this, NotesListActivity.class);
                startActivity(intent);
                break;

            case R.id.my_records:
                Intent intent2 = new Intent(MainActivity.this, MyRecordsActivity.class);
                startActivity(intent2);
                break;


        }

    }

    // create a class to get weather using API

    protected void getWeatherReport(String lat, String lon) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // make a retrofit to get all the data
        if (networkInfo != null && networkInfo.isConnected()) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            OpenWeatherInterface weatherService = retrofit.create(OpenWeatherInterface.class);

            Call<Example> call = weatherService.getWeather(lat, lon);


            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {

                    Example weather = response.body();

                    if (weather == null) {
                        Toast.makeText(MainActivity.this, "no data", Toast.LENGTH_SHORT).show();
                    } else {

                        mWeatherStatusView = (TextView) findViewById(R.id.weather_status);

                        String description = weather.getWeather().get(0).getDescription();

                        int currentTemp = weather.getMain().getTemp().intValue() - 273;
                        int lowTemp = weather.getMain().getTempMin().intValue() - 273;
                        int highTemp = weather.getMain().getTempMax().intValue() - 273;

                        String ct = String.valueOf(currentTemp);


                        mWeatherStatusView.setText(description);
                        mCurrentTemp.setText(ct
                                + (char) 0x00B0);
                        mLowTemp.setText("" + lowTemp + (char) 0x00B0);
                        mHighTemp.setText("" + highTemp + (char) 0x00B0);


                        WeatherImageAndSuggestionsBasedOnResponse(weather);
                    }
                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "fall", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
        }
    }

    // make a method to update the views

    // use google service to get the current location

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (lastLocation == null) {
                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            } else {
                mLat = String.valueOf(lastLocation.getLatitude());
                mLon = String.valueOf(lastLocation.getLongitude());
                getWeatherReport(mLat, mLon);

            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "sorry to hear that", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    private void WeatherImageAndSuggestionsBasedOnResponse(Example weather) {

        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
        mSuggestionView = (TextView) findViewById(R.id.suggestion_indoor_outdoor);
        String condition = weather.getWeather().get(0).getDescription();


        if (getCurrentTime().equals("nighttime")) {

            if (condition.contains("clear")) {

                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/01n.png").into(mWeatherImage);
                mSuggestionView.setText("It's beautiful outside, you can train in the park.");

            } else if (condition.contains("few clouds")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/02n.png").into(mWeatherImage);
                mSuggestionView.setText("It's cloudy outside, better train indoor.");

            } else if (condition.contains("clouds")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/03n.png").into(mWeatherImage);
                mSuggestionView.setText("It's cloudy outside, better train indoor.");

            } else if (condition.contains("rain")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/10n.png").into(mWeatherImage);
                mSuggestionView.setText("It's raining outside, better train indoor.");

            } else if (condition.contains("snow")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/0n.png").into(mWeatherImage);
                mSuggestionView.setText("It's snowing, better train indoor.");

            } else if (condition.contains("mist")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/50n.png").into(mWeatherImage);
                mSuggestionView.setText("It's mist, better train indoor.");

            } else if (condition.contains("thunderstorm")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/11n.png").into(mWeatherImage);
                mSuggestionView.setText("There is thunderstorm outside, better train indoor.");

            }
            else if (condition.contains("drizzle")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/50n.png").into(mWeatherImage);
                mSuggestionView.setText("It's drizzle, better train indoor.");

            }
            else {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/01n.png").into(mWeatherImage);
                mSuggestionView.setText("It's beautiful outside, you can train in the park.");
            }

        }

        if (getCurrentTime().equals("daytime")) {

            if (condition.contains("clear")) {

                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/01d.png").into(mWeatherImage);
                mSuggestionView.setText("It's beautiful outside, you can train in the park.");
            } else if (condition.contains("clouds")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/03d.png").into(mWeatherImage);
                mSuggestionView.setText("It's cloudy outside, better train indoor.");

            } else if (condition.contains("rain")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/09d.png").into(mWeatherImage);
                mSuggestionView.setText("It's raining outside, better train indoor.");


            } else if (condition.contains("snow")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/13d.png").into(mWeatherImage);
                mSuggestionView.setText("It's snowing, better train indoor.");

            } else if (condition.contains("mist")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/50d.png").into(mWeatherImage);
                mSuggestionView.setText("It's mist, better train indoor.");

            } else if (condition.contains("thunderstorm")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/11d.png").into(mWeatherImage);
                mSuggestionView.setText("There is thunderstorm outside, better train indoor.");

            } else if (condition.contains("drizzle")) {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/50d.png").into(mWeatherImage);
                mSuggestionView.setText("It's drizzle, better train indoor.");

            }else {
                Picasso.with(mWeatherImage.getContext()).load("http://openweathermap.org/img/w/01d.png").into(mWeatherImage);
                mSuggestionView.setText("It's beautiful outside, you can train in the park.");
            }

        }


    }

    // use sunrise and sunset later~
    private String getCurrentTime() {
        String currentTime;

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 18) {
            currentTime = "daytime";
        } else {
            currentTime = "nighttime";
        }
        return currentTime;

    }
}
