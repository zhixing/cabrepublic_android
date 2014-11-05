package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import sg.edu.nus.cabrepublic.utilities.CRDataManager;


public class HomePageActivity extends Activity {
    private GoogleMap map;
    private RoundedImageView profilePicture;
    private TextView userName;
    private Button preferenceButton;
    private Button initializeShareButton;

    private CRDataManager crDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        profilePicture = (RoundedImageView) findViewById(R.id.profilePicture);
        userName = (TextView) findViewById(R.id.userName);
        preferenceButton = (Button) findViewById(R.id.preferenceButton);
        initializeShareButton = (Button) findViewById(R.id.initializeShareButton);

        crDataManager = CRDataManager.getInstance();

        // Initialize Google Map:
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.pickUpLocationMap)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        centerMapOnMyLocation();

        // Set the texts:
        userName.setText(crDataManager.currentUser.Name);

        String preferenceString = "";
        if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_FEMALE) {
            preferenceString += "Male. ";
        } else if (crDataManager.currentUser.Gender_preference == crDataManager.GENDER_MALE) {
            preferenceString += "Female. ";
        }

        preferenceString += "Age between " + crDataManager.currentUser.Age_min + " and " + crDataManager.currentUser.Age_max;

        preferenceButton.setText(preferenceString);

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

    private void centerMapOnMyLocation() {

        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        LatLng myLocation = new LatLng(103.78072, 1.297402);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, (float) 2.0));
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPreferenceClicked(View v) {
        Intent intent = new Intent(HomePageActivity.this, PreferenceActivity.class);
        startActivity(intent);
    }
}
