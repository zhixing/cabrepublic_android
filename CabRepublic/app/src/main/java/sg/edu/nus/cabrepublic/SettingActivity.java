package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class SettingActivity extends Activity {
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spinner = (Spinner) findViewById(R.id.gender_content);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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
        EditText ageField = (EditText) findViewById(R.id.age_content);
        final int age = Integer.valueOf(ageField.getText().toString());
        if (age < 0 || age > 100) {
            Toast.makeText(this, "Age must be between 0 and 100.", Toast.LENGTH_LONG).show();
        } else {
            EditText nameField = (EditText) findViewById(R.id.name_content);
            String name = nameField.getText().toString();
            int gender = spinner.getSelectedItemPosition();
            EditText numberField = (EditText) findViewById(R.id.number_content);
            String number = numberField.getText().toString();
            
        }
    }
}
