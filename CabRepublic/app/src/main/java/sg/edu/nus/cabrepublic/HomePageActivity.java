package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import sg.edu.nus.cabrepublic.models.PickUpLocation;
import sg.edu.nus.cabrepublic.models.User;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.ViewHelper;


public class HomePageActivity extends Activity {
    private GoogleMap map;
    private RoundedImageView profilePicture;
    private TextView userName;
    private Button preferenceButton;
    private Button onpickUpLocationEditButton;
    private Button initializeShareButton;
    private Button destinationLocationEditButton;

    // At bottom:
    private LinearLayout buttonsHolder;
    private ImageButton cancelButton;
    private TextView countDownTextView;

    // Utility:
    private CountDownTimer countDownTimer;
//    private Timer pollAppServerForMatchTimer;
    private android.os.Handler poller;

    private CRDataManager crDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        profilePicture = (RoundedImageView) findViewById(R.id.profilePicture);
        userName = (TextView) findViewById(R.id.userName);
        preferenceButton = (Button) findViewById(R.id.preferenceButton);
        initializeShareButton = (Button) findViewById(R.id.initializeShareButton);
        onpickUpLocationEditButton = (Button)findViewById(R.id.pickUpLocationEditButton);
        destinationLocationEditButton = (Button)findViewById(R.id.destinationEditButton);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        countDownTimer = null;
        countDownTextView = (TextView)findViewById(R.id.countDownTextView);
        buttonsHolder = (LinearLayout)findViewById(R.id.buttonsholder);

        // To be hidden:
        cancelButton.setVisibility(View.INVISIBLE);
        countDownTextView.setVisibility(View.INVISIBLE);
        buttonsHolder.setVisibility(View.INVISIBLE);

        crDataManager = CRDataManager.getInstance();

        initializeGoogleMap();
        setUserNameAndProfileImage();
        setLocationAndPreferenceTexts();
    }

    private void initializeGoogleMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.pickUpLocationMap)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        centerMapOnMyLocation();
    }

    private void setUserNameAndProfileImage(){
        userName.setText(crDataManager.currentUser.Name);

        // Set the profile image:
        int resourceID;
        if (crDataManager.currentUser.Gender == crDataManager.GENDER_FEMALE) {
            resourceID = R.drawable.ic_female;
        } else {
            resourceID = R.drawable.ic_male;
        }
        Bitmap profileImageBitmap = BitmapFactory.decodeResource(getResources(), resourceID);
        profilePicture.setImageBitmap(profileImageBitmap);
    }

    private void setLocationAndPreferenceTexts(){

        String preferenceString = "";
        if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_FEMALE) {
            preferenceString += "Male. ";
        } else if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_MALE) {
            preferenceString += "Female. ";
        }

        preferenceString += "Age between " + crDataManager.currentUser.Age_min + " and " + crDataManager.currentUser.Age_max;

        preferenceButton.setText(preferenceString);

        onpickUpLocationEditButton.setText(CRDataManager.getInstance().currentUser.pickUpLocation.locationName);

        destinationLocationEditButton.setText("Please Select");
        CRDataManager.getInstance().currentUser.destinationLocation = null;
    }

    private void centerMapOnMyLocation() {

        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        LatLng myLocation = new LatLng(1.297402, 103.78072);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, (float) 13.0));

        drawStartAndEndMarkers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search){
            Intent intent = new Intent(HomePageActivity.this, SearchPlacesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPreferenceClicked(View v) {
        Intent intent = new Intent(HomePageActivity.this, PreferenceActivity.class);
        startActivity(intent);
    }

    public void onPickUpLocationEditButtonClicked(View v){
        Intent intent = new Intent(HomePageActivity.this, PickUpLocationListActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onDestinationButtonClicked(View v){
        Intent intent = new Intent(HomePageActivity.this, SearchPlacesActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, "Prince George's Park Singapore");
        startActivityForResult(intent, 2);
    }

    public void onStartSharingButtonClicked(View v){
        User currentUser = CRDataManager.getInstance().currentUser;
        if (currentUser.destinationLocation == null){
            ViewHelper.getInstance().toastMessage(HomePageActivity.this, "Please select your destination.");
        } else {

            // Create an intention and send to server:
            android.os.Handler startIntentionHandler = new android.os.Handler() {
                @Override
                public void handleMessage(Message userMsg) {
                    if (userMsg.what == 0) {

                        // Show the timer:
                        buttonsHolder.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        countDownTextView.setVisibility(View.VISIBLE);
                        countDownTimer = initializeCountDownTextView();

                        queryCoalitionServerForMatch();

                    } else {
                        ViewHelper.getInstance().handleRequestFailure(HomePageActivity.this, userMsg.what, (String) userMsg.obj);
                    }
                }
            };

            CRDataManager.getInstance().createIntentionWithCompletion(
                    currentUser.pickUpLocation.longitude,
                    currentUser.pickUpLocation.latitude, startIntentionHandler);
        }
    }

    public void cancelButtonPressed(View v){
        // Delete the intention from the server:
        // Create an intention and send to server:
        android.os.Handler deleteMatchHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message userMsg) {
                if (userMsg.what == 0) {

                    poller.removeCallbacksAndMessages(null);
                    cancelButton.setVisibility(View.INVISIBLE);
                    countDownTextView.setVisibility(View.INVISIBLE);
                    buttonsHolder.setVisibility(View.INVISIBLE);

                } else {
                    ViewHelper.getInstance().handleRequestFailure(HomePageActivity.this, userMsg.what, (String) userMsg.obj);
                }
            }
        };

        CRDataManager.getInstance().deleteMatchingWithCompletion(deleteMatchHandler);
    }

    private void queryCoalitionServerForMatch(){
        // Query the coalition server for a few potential email addresses that fits my preference:
        // If NOT Found:
        final long interval = 2000;
        poller = new android.os.Handler();
        final Runnable runnable = new Runnable() {
            final Runnable innerThis = this;
            @Override
            public void run() {
                android.os.Handler innerHandler = new android.os.Handler() {
                    @Override
                    public void handleMessage(Message userMsg) {
                        // Found a match:
                        if (userMsg.what == 0) {
                            //Intent intent = new Intent(HomePageActivity.this, MatchedInfoActivity.class);
                            //startActivity(intent);
                        } else if (userMsg.what == 1) {
                            // 404 not found
                            poller.postDelayed(innerThis, interval);
                        } else{
                            ViewHelper.getInstance().handleRequestFailure(HomePageActivity.this, userMsg.what, (String) userMsg.obj);
                        }
                    }
                };
                CRDataManager.getInstance().pollMatchStatusWithCompletion(innerHandler);
            }
        };
        poller.postDelayed(runnable, interval);

        // If emails were found from coalition:
        /*
        pollAppServerForMatchTimer.cancel();
        android.os.Handler pollMatchFromAppServerHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message userMsg) {
                if (userMsg.what == 0) {

                    Intent intent = new Intent(HomePageActivity.this, MatchedInfoActivity.class);
                    startActivity(intent);

                } else {
                    ViewHelper.getInstance().handleRequestFailure(HomePageActivity.this, userMsg.what, (String) userMsg.obj);
                }
            }
        };
        //CRDataManager.getInstance().findMatchingWithCompletion, pollMatchFromAppServerHandler);
        */
    }

    private CountDownTimer initializeCountDownTextView() {
        countDownTextView = (TextView) findViewById(R.id.countDownTextView);
        final CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            int numberOfDots = 0;
            public void onTick(long millisUntilFinished) {
                //countDownTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
                long secondsRemaining = millisUntilFinished/1000;
                String mins = "" + secondsRemaining/60;
                String secs = "" + secondsRemaining%60;

                if (secs.length() < 2) {
                    secs = "0" + secs;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Finding a match");
                for (int i = 0; i < numberOfDots; ++i) {
                    sb.append(".");
                }
                for (int i = 0; i < 3-numberOfDots; ++i) {
                    sb.append(" ");
                }
                sb.append("(" + mins + ":" + secs + ")");
                countDownTextView.setText(sb.toString());
                ++numberOfDots;
                if (numberOfDots > 3) {
                    numberOfDots = 0;
                }
            }

            public void onFinish() {
                ViewHelper.getInstance().toastMessage(HomePageActivity.this, "No match was found. Please try again");
                cancelButtonPressed(null);
            }
        }.start();

        return countDownTimer;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                PickUpLocation result = data.getParcelableExtra("newLocation");
                CRDataManager.getInstance().currentUser.pickUpLocation = result;
                onpickUpLocationEditButton.setText(result.locationName);
                drawStartAndEndMarkers();
            }
            if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == 2){
            if(resultCode == RESULT_OK){
                PickUpLocation result = data.getParcelableExtra("newLocation");
                CRDataManager.getInstance().currentUser.destinationLocation = result;
                destinationLocationEditButton.setText(result.locationName);
                drawStartAndEndMarkers();
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void drawStartAndEndMarkers(){
        map.clear();
        PickUpLocation start = CRDataManager.getInstance().currentUser.pickUpLocation;
        PickUpLocation end = CRDataManager.getInstance().currentUser.destinationLocation;
        if (start != null) {
            drawMarker(start.latitude, start.longitude, "Start");
        }
        if (end != null){
            drawMarker(end.latitude, end.longitude, "Destination");
        }
    }

    private void drawMarker(Double latitude, Double longitude, String title) {
        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(latitude, longitude);

        // zoom to the current location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 14));

        // add a marker to the map indicating our current position
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentPosition)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
    }
}
