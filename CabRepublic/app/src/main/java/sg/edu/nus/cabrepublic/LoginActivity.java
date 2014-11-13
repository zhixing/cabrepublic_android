package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import sg.edu.nus.cabrepublic.utilities.CRDataManager;
import sg.edu.nus.cabrepublic.utilities.ViewHelper;

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

    public void onLoginButtonClicked(View view) {
        EditText userEmailText = (EditText) findViewById(R.id.email_address);
        EditText userPasswordText = (EditText) findViewById(R.id.password);

        String userEmail = userEmailText.getText().toString();
        String userPassword = userPasswordText.getText().toString();

        if (userEmail.toString().equals("")) {
            showSignUpErrorMessage("Please input an email.");
        } else if (userPassword.equals("")) {
            showSignUpErrorMessage("Please input a password.");
        } else {

            Handler loginHandler = new Handler() {
                @Override
                public void handleMessage(Message userMsg) {
                    if (userMsg.what == 0) {
                        if (CRDataManager.getInstance().currentUser.Type == 0) {
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            ViewHelper.getInstance().toastMessage(LoginActivity.this, "Successfully logged in");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, TaxiHomeActivity.class);
                            ViewHelper.getInstance().toastMessage(LoginActivity.this, "Successfully logged in");
                            startActivity(intent);
                        }

                    } else {
                        ViewHelper.getInstance().handleRequestFailure(LoginActivity.this, userMsg.what, (String) userMsg.obj);
                    }
                }
            };

            CRDataManager.getInstance().loginWithCompletion(userEmail, userPassword, loginHandler);
        }
    }

    public void showSignUpErrorMessage(String message) {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("SignUpFail")
                .setMessage(message)
                .setPositiveButton("Ok", null).show();
    }
}
