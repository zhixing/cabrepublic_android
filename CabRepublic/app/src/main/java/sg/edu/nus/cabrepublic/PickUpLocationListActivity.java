package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sg.edu.nus.cabrepublic.adapters.PickUpLocationAdapter;
import sg.edu.nus.cabrepublic.models.PickUpLocation;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;


public class PickUpLocationListActivity extends Activity {
    private PickUpLocationAdapter pickUpLocationAdapter;
    private ListView pickUpLocationListView = (ListView) findViewById(R.id.pick_up_location_list_view);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_location_list);

        ArrayList<PickUpLocation> locations = CRDataManager.getInstance().getPickUpLocations();
        pickUpLocationAdapter = new PickUpLocationAdapter(PickUpLocationListActivity.this, locations);
        pickUpLocationListView.setAdapter(pickUpLocationAdapter);
        pickUpLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("Cat", "Item clicked");
                //intent.putExtra("topic", (Parcelable) adapterView.getItemAtPosition(i));

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pick_up_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
