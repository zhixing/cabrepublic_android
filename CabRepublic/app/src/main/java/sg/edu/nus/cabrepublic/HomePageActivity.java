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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter.MpsgStarter;
import sg.edu.nus.cabrepublic.models.FindMatchResponse;
import sg.edu.nus.cabrepublic.models.PickUpLocation;
import sg.edu.nus.cabrepublic.models.User;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.ViewHelper;


public class HomePageActivity extends Activity {
    private GoogleMap map;
    private MpsgStarter starter;
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
        android.os.Handler delayedHandler = new android.os.Handler(){

        };
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
            preferenceString += "Female. ";
        } else if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_MALE) {
            preferenceString += "Male. ";
        } else if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_MALE){
            preferenceString += "Both genders. ";
        }

        preferenceString += "Age between " + crDataManager.currentUser.Age_min + " and " + crDataManager.currentUser.Age_max;

        preferenceButton.setText(preferenceString);

        CRDataManager.getInstance().currentUser.pickUpLocation = CRDataManager.getInstance().getPickUpLocations().get(0);

        onpickUpLocationEditButton.setText(CRDataManager.getInstance().currentUser.pickUpLocation.locationName);

        destinationLocationEditButton.setText("Please select");
    }

    private void centerMapOnMyLocation() {

        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        LatLng myLocation = new LatLng(1.297402, 103.78072);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        } else{
            Log.d("ddd", "ddd");
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
        starter = new MpsgStarter(this);
        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        currentUser.mLocation = new PickUpLocation(String.valueOf(location.getLongitude()) + "-" + String.valueOf(location.getLatitude()));

        if (location != null) {
            Toast.makeText(this, "Location is not null!", Toast.LENGTH_LONG).show();
            starter.updateDynamicContextAttribute("person.location", String.valueOf(location.getLongitude()) + "-" + String.valueOf(location.getLatitude()));

        } else {
            Toast.makeText(this, "Location is null!", Toast.LENGTH_LONG).show();
        }


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
        android.os.Handler deleteMatchHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message userMsg) {
                if (userMsg.what == 0) {
                    if (poller !=  null) {
                        poller.removeCallbacksAndMessages(null);
                    }


                    countDownTimer.cancel();

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
        final User user = CRDataManager.getInstance().currentUser;
        // Query the coalition server for a few potential email addresses that fits my preference:
        starter.sendQuery(user.Email + ";query:select person.email,person.gender,person.destination,person.location,person.gender_preference,person.number,person.name,person.age,person.age_max,person.age_min"
                + " from person where person.group = \"eight\""
        , new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                // Found a match:
                final HashMap<String, HashMap<String, String>> result = (HashMap<String, HashMap<String, String>>) msg.obj;
                ArrayList<String> qualifiedEmails = new ArrayList<String>();
                if (msg.what == 0) {

                    Set<String> s = result.keySet();
                    for (String key : s) {
                        HashMap<String, String> person = result.get(key);
                        String loc = person.get("person.location");
                        if (!loc.equalsIgnoreCase("nil")) {
                            PickUpLocation otherLocation = new PickUpLocation(person.get("person.location"));
                            float[] startDistance = new float[2];
                            Location.distanceBetween(user.mLocation.latitude, user.mLocation.longitude, otherLocation.latitude, otherLocation.longitude, startDistance);
                            if (startDistance[0] < 500) {
                                String dest = person.get("person.destination");
                                if (!dest.equalsIgnoreCase("nil")) {
                                    PickUpLocation otherDestination = new PickUpLocation(person.get("person.destination"));
                                    float[] destDistance = new float[2];
                                    Location.distanceBetween(user.destinationLocation.latitude, user.destinationLocation.longitude, otherDestination.latitude, otherDestination.longitude, destDistance);
                                    if (destDistance[0] < 1000) {
                                        if (user.Gender_preference == 0 || user.Gender_preference == Integer.valueOf(person.get("person.gender"))) {
                                            if (Integer.valueOf(person.get("person.gender_preference")) == 0 || user.Gender == Integer.valueOf(person.get("person.gender"))) {
                                                if (user.Age > Integer.valueOf(person.get("person.age_min")) && user.Age < Integer.valueOf(person.get("person.age_max")) && Integer.valueOf(person.get("person.age")) > user.Age_min && Integer.valueOf(person.get("person.age")) < user.Age_max) {
                                                    if (!person.get("person.email").equalsIgnoreCase(user.Email)) {
                                                        qualifiedEmails.add(person.get("person.email"));
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }


                    }
                }
                final long interval = 2000;
                final Runnable runnable = new Runnable() {
                    final Runnable innerThis = this;
                    @Override
                    public void run() {
                        android.os.Handler innerHandler = new android.os.Handler() {
                            @Override
                            public void handleMessage(Message userMsg) {
                                // Found a match:
                                if (userMsg.what == 0) {
                                    matchIsFoundFromServer(userMsg, result);
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
                poller = new android.os.Handler();
                if (qualifiedEmails.size() == 0) {
                    poller.postDelayed(runnable, interval);
                } else {
                    android.os.Handler findMatchFromAppServerHandler = new android.os.Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 0) {
                                matchIsFoundFromServer(msg, result);
                            } else {
                                poller.postDelayed(runnable, interval);
                            }
                        }
                    };
                    CRDataManager.getInstance().findMatchingWithCompletion(qualifiedEmails, findMatchFromAppServerHandler);
                }
            }

        });
    }

    private void matchIsFoundFromServer(Message userMsg, HashMap<String, HashMap<String, String>> result){
        countDownTimer.cancel();
        FindMatchResponse response = (FindMatchResponse) userMsg.obj;
        Intent intent = new Intent(HomePageActivity.this, MatchedInfoActivity.class);
        HashMap<String, String> map1 = result.get(response.Email);
        intent.putExtra("name", map1.get("person.name"));
        intent.putExtra("email", response.Email);
        intent.putExtra("age", result.get(response.Email).get("person.age"));

        PickUpLocation loc = CRDataManager.getInstance().convertFromStringToPickUpLocation(response.Pickup_location);
        intent.putExtra("pickup_name", loc.locationName);
        intent.putExtra("pickup_latitude", loc.latitude);
        intent.putExtra("pickup_longitude", loc.longitude);
        intent.putExtra("number", result.get(response.Email).get("person.number"));
        startActivity(intent);
    }

    private CountDownTimer initializeCountDownTextView() {
        countDownTextView = (TextView) findViewById(R.id.countDownTextView);
        final CountDownTimer countDownTimer = new CountDownTimer(300000, 1000) {
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
                MpsgStarter.getInstance().updateDynamicContextAttribute("person.destination", String.valueOf(CRDataManager.getInstance().currentUser.destinationLocation.longitude + "-" + CRDataManager.getInstance().currentUser.destinationLocation.latitude));
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
