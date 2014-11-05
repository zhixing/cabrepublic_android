package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;


public class MatchedInfoActivity extends Activity {
    private GoogleMap map;
    private RoundedImageView profilePicture;
    private TextView nameAndAgeTextView;
    private Button contactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_info);
        Intent intent = this.getIntent();

        String urlOfProfilePicture = intent.getStringExtra("urlOfProfilePicture");
        this.initializeGoogleMap();
        try {
            this.intializeProfilePicFromURL(urlOfProfilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        nameAndAgeTextView = (TextView) findViewById(R.id.matchedNameAndAge);
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        nameAndAgeTextView.setText(name + ", " + age);

        contactButton = (Button) findViewById(R.id.contactButton);
        String handphoneNumber = intent.getStringExtra("handphoneNumber");
        contactButton.setText(handphoneNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matched_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeGoogleMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.pickUpLocationMap)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
    }

    private void intializeProfilePicFromURL(String url) throws IOException {
        profilePicture = (RoundedImageView) findViewById(R.id.matchedProfilePicture);
        URL newUrl = new URL(url);
        Bitmap profilePicBitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());

        profilePicture.setImageBitmap(profilePicBitmap);
    }

    private void drawMarker(Location location) {
        map.clear();
        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        // zoom to the current location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

        // add a marker to the map indicating our current position
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude()));
    }
}
