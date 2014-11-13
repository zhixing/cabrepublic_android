package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import sg.edu.nus.cabrepublic.models.PickUpLocation;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.ViewHelper;


public class TaxiHomeActivity extends Activity {
    private GoogleMap map;
    Handler poller = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_home);
        // Initialize Google Map:
        initializeGoogleMap();
        updateCustomersLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_taxi_home, menu);
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

    private void initializeGoogleMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.taxiMap)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        centerMapOnMyLocation();
        setUpMapMarkerListener();
    }

    private void updateCustomersLocation () {

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                map.clear();
                if (msg.what == 0) {
                    ArrayList<PickUpLocation> locations = (ArrayList<PickUpLocation>) msg.obj;
                    for (PickUpLocation p : locations) {
                        if (p != null) {
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(p.latitude, p.longitude)).snippet(p.locationName);
                            map.addMarker(markerOptions);
                        }

                    }
                } else {

                }
            }
        };
        CRDataManager.getInstance().findAllMatchingsWithCompletion(handler);
    }

    private void setUpMapMarkerListener () {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                new AlertDialog.Builder(TaxiHomeActivity.this).setTitle("Confirmation")
                        .setMessage("Do you confirm to serve this customer?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                map.clear();
                                Marker m = map.addMarker(new MarkerOptions()
                                        .position(marker.getPosition())
                                        .title("Customer Info:")
                                        .snippet(marker.getSnippet()));
                                m.showInfoWindow();
                                poller.removeCallbacksAndMessages(null);
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        new AlertDialog.Builder(TaxiHomeActivity.this).setTitle("Service finished")
                                                .setMessage("Do you want to refresh the map to find another customer to serve?")
                                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        map.clear();
                                                        centerMapOnMyLocation();
                                                        setUpMapMarkerListener();
                                                    }
                                                })
                                                .setNegativeButton("No, still serving.", null).show();
                                        return true;
                                    }
                                });

                            }
                        }).setNegativeButton("NO", null).show();
                return true;
            }
        });
    }

    private void centerMapOnMyLocation() {

        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        LatLng myLocation = new LatLng(1.297402, 103.78072);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, (float) 13.0));

        final long interval = 15000;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateCustomersLocation();
                poller.postDelayed(this, interval);
            }
        };
        poller.postDelayed(runnable, interval/10);
    }

    @Override
    public void finish() {
        poller.removeCallbacksAndMessages(null);
        super.finish();
    }
}
