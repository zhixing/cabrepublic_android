package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.edu.nus.cabrepublic.models.PickUpLocation;
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
        if (CRDataManager.getInstance().currentUser.destinationLocation == null){
            ViewHelper.getInstance().toastMessage(HomePageActivity.this, "Please select your destination.");
        } else {
            Intent intent = new Intent(HomePageActivity.this, MatchedInfoActivity.class);
            startActivity(intent);
        }
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
        //Bitmap profileImageBitmap = BitmapFactory.decodeResource(getResources(), 3);

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
