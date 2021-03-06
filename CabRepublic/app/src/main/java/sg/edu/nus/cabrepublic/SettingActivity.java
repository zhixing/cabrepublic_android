package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter.MPSG;
import sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter.MpsgStarter;
import sg.edu.nus.cabrepublic.models.User;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;


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

        CRDataManager.getInstance().loadUserDataFromFile(SettingActivity.this);

        User user = CRDataManager.getInstance().currentUser;

        if (user.Name != null){
            EditText editText0 = (EditText)findViewById(R.id.name_content);
            editText0.setText(user.Name);
        }

        if (user.Phone_number != null){
            EditText editText1 = (EditText)findViewById(R.id.number_content);
            editText1.setText(user.Phone_number);
        }

        if (user.Gender != 0) {
            EditText editText = (EditText) findViewById(R.id.user_age_content);
            editText.setText("" + user.Age);
        }

        Spinner spinner = (Spinner)findViewById(R.id.gender_content);
        spinner.setSelection(user.Gender / 2);
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
        EditText ageField = (EditText) findViewById(R.id.user_age_content);
        final int age = Integer.valueOf(ageField.getText().toString());
        if (age < 0 || age > 100) {
            Toast.makeText(this, "Age must be between 0 and 100.", Toast.LENGTH_LONG).show();
        } else {
            EditText nameField = (EditText) findViewById(R.id.name_content);
            String name = nameField.getText().toString();
            int gender = spinner.getSelectedItemPosition() + 1;
            EditText numberField = (EditText) findViewById(R.id.number_content);
            String number = numberField.getText().toString();

            MpsgStarter starter = new MpsgStarter(this);
            User user = CRDataManager.getInstance().currentUser;
            starter.initializeMPSG(user.Email,
                    "person.name::" + name +
                    ",person.age::" + age +
                    ",person.gender::" + gender +
                    ",person.number::" + number +
                    ",person.email::" + user.Email +
                    ",person.location::nil" +
                    ",person.destination::nil" +
                    ",person.gender_preference::" + user.Gender_preference +
                    ",person.age_max::" + user.Age_max +
                    ",person.age_min::" + user.Age_min +
                    ",person.group::eight"
                    , "PERSON");

            user.Age = age;
            user.Gender = gender;
            user.Name = name;
            user.Phone_number = number;

            CRDataManager.getInstance().storeUserDataToFile(SettingActivity.this);
            Intent intent = new Intent(SettingActivity.this, HomePageActivity.class);
            startActivity(intent);
        }
    }
}
