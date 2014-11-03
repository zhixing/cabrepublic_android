package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


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

    public void onSignUpButtonClicked(View view){
        EditText userEmail = (EditText) findViewById(R.id.email_address);
        EditText userPassword = (EditText) findViewById(R.id.password);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText()).matches()) {
            showSignUpErrorMessage("Have you input the email correctly?");
        } else if (userEmail.getText().toString().equals("")) {
            showSignUpErrorMessage("Please input an email.");
        } else if (userPassword.getText().toString().equals("")) {
            showSignUpErrorMessage("Please input a password.");
        } else {
            /*
            Handler signupHandler = new Handler() {
                @Override
                public void handleMessage(Message userMsg) {
                    stopLoadingAnimation();
                    if (userMsg.what == 0) {
                        DataManager.getInstance().persistData(SignupActivity.this);
                        Intent intent = new Intent(SignupActivity.this, GenreActivity.class);
                        intent.putExtra("ENTRANCE", 1);
                        intent.putExtra("NEW_USER", true);
                        ViewHelper.getInstance().toastMessage(SignupActivity.this, R.string.signup_success_message);
                        startActivity(intent);
                    } else {
                        ViewHelper.getInstance().handleRequestFailure(SignupActivity.this, userMsg.what, (String) userMsg.obj);
                    }

                }
            };
            startLoadingAnimation();
            if (mode == MODE_NORMAL) {
                DataManager.getInstance().signupWithCompletion(new UserSignupInfo(userEmail.getText().toString(), userPassword.getText().toString(), username.getText().toString(), "", "-1", "", gender, DEFAULT_BIRTHDAY), signupHandler);
            } else if (mode == MODE_RENREN) {
                DataManager.getInstance().signupWithCompletion(new UserSignupInfo(userEmail.getText().toString(), userPassword.getText().toString(), username.getText().toString(), "renren", uid, avatar, gender, DEFAULT_BIRTHDAY), signupHandler);
            } else if (mode == MODE_WEIBO) {
                DataManager.getInstance().signupWithCompletion(new UserSignupInfo(userEmail.getText().toString(), userPassword.getText().toString(), username.getText().toString(), "weibo", uid, avatar, gender, DEFAULT_BIRTHDAY), signupHandler);
            } else if (mode == MODE_QQ) {
                DataManager.getInstance().signupWithCompletion(new UserSignupInfo(userEmail.getText().toString(), userPassword.getText().toString(), username.getText().toString(), "qq_connect", uid, avatar, gender, DEFAULT_BIRTHDAY), signupHandler);
            }
            */

        }
    }

    public void showSignUpErrorMessage(String message) {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("SignUpFail")
                .setMessage(message)
                .setPositiveButton("Ok", null).show();
    }
}
