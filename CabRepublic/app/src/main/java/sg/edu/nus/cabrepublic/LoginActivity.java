package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.os.Handler;

import sg.edu.nus.cabrepublic.requests.UserLoginRequest;
import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.UserCredential;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    public void onLoginButtonClicked(View view){
        EditText userEmail = (EditText) findViewById(R.id.email_address);
        EditText userPassword = (EditText) findViewById(R.id.password);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText()).matches()) {
            showSignUpErrorMessage("Have you input the email correctly?");
        } else if (userEmail.getText().toString().equals("")) {
            showSignUpErrorMessage("Please input an email.");
        } else if (userPassword.getText().toString().equals("")) {
            showSignUpErrorMessage("Please input a password.");
        } else {

            Handler loginHandler = new Handler() {
                @Override
                public void handleMessage(Message userMsg) {
                    if (userMsg.what == 0) {
                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                        intent.putExtra("ENTRANCE", 1);
                        intent.putExtra("NEW_USER", true);
                        Toast toast = Toast.makeText(LoginActivity.this, "Successfully Logged in", Toast.LENGTH_LONG);
                        toast.show();
                        startActivity(intent);
                    } else {
                        Log.d("ERROR", "Login fail");
                        //ViewHelper.getInstance().handleRequestFailure(LoginActivity.this, userMsg.what, (String) userMsg.obj);
                    }

                }
            };
            UserCredential credentials = new UserCredential(userEmail.getText().toString(), userPassword.getText().toString());
            CRDataManager.getInstance().loginWithCompletion(credentials, loginHandler);
        }
    }

    public void showSignUpErrorMessage(String message) {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("SignUpFail")
                .setMessage(message)
                .setPositiveButton("Ok", null).show();
    }
}
