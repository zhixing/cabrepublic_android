package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.ViewHelper;


public class PreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        Spinner spinner = (Spinner) findViewById(R.id.gender_preference);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_preference_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preference, menu);
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

    public void onSaveButtonClicked(View v) {
        EditText minAgeField = (EditText) findViewById(R.id.min_age_content);
        final int ageMin = Integer.valueOf(minAgeField.getText().toString());

        EditText maxAgeField = (EditText) findViewById(R.id.max_age_content);
        final int ageMax = Integer.valueOf(maxAgeField.getText().toString());

        if (ageMin < 0 || ageMin > 100 || ageMax < 0 || ageMax > 100 || ageMin > ageMax) {
            Toast.makeText(this, "Min age and max age must be between 0 and 100 with min age being equal to or smaller than max age.", Toast.LENGTH_LONG).show();
        } else {
            Spinner spinner = (Spinner) findViewById(R.id.gender_preference);
            final int gender = spinner.getSelectedItemPosition();
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_circle);

            Handler updateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    progressBar.setVisibility(View.GONE);
                    if (msg.what == 0) {
                        Toast.makeText(PreferenceActivity.this, "Preference updated!", Toast.LENGTH_LONG).show();
                        CRDataManager.getInstance().currentUser.Age_max = ageMax;
                        CRDataManager.getInstance().currentUser.Age_min = ageMin;
                        CRDataManager.getInstance().currentUser.Gender_preference = gender;
                    } else {
                        Toast.makeText(PreferenceActivity.this, "Cannot update the preference!", Toast.LENGTH_LONG).show();
                    }
                }
            };
            progressBar.setVisibility(View.VISIBLE);
            CRDataManager.getInstance().updatePreferenceWithCompletion(ageMin, ageMax, gender, updateHandler);
        }
    }
}
