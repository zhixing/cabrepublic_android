package sg.edu.nus.cabrepublic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.edu.nus.cabrepublic.R;
import sg.edu.nus.cabrepublic.models.PickUpLocation;

/**
 * Created by zhixing on 14.11.06.
 */
public class PickUpLocationAdapter extends ArrayAdapter<PickUpLocation> {

    public PickUpLocationAdapter(Context context, ArrayList<PickUpLocation> locations) {
        super(context, R.layout.pick_up_list_item, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PickUpLocation location = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pick_up_list_item, parent, false);
        }

        // Lookup view for data population
        TextView locationName = (TextView) convertView.findViewById(R.id.pick_up_location_name_view);
        // Populate the data into the template view using the data object
        locationName.setText(location.locationName);
        // Return the completed view to render on screen
        return convertView;
    }
}
