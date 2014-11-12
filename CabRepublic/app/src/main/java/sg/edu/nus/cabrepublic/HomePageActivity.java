package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter.MPSG;
import sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter.MpsgStarter;


public class HomePageActivity extends Activity {
    public static String mpsgName = "MPSGSathiya3";
    public static String StaticContextData = "person.name::qingcheng,person.preference::pc,person.location::ion,person.isBusy::yes,person.speed::nil,person.action::eating,person.power::low,person.mood::happy,person.acceleration::nil,person.gravity::nil,person.magnetism::nil";
    public static String ContextType = "PERSON";
    public static String query = "select person.name,person.gravity,person.preference from person where person.isBusy = \"yes\"";
    public MpsgStarter mpsgStarter;
    public Handler handler;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mpsgStarter = new MpsgStarter(this);
        mpsgStarter.initializeMPSG(mpsgName, StaticContextData, ContextType);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                if (what == 0) {
                    HashMap<String, HashMap<String, String>> result = (HashMap<String, HashMap<String, String>>) msg.obj;

                    StringBuilder sb = new StringBuilder();
                    for (String key : result.keySet()) {
                        sb.append(key + " ");
                        sb.append((result.get(key)).get("person.preference"));
                        sb.append("\n");
                    }

                    updateTextView(sb.toString());
                }
            }
        };

//        Intent intent = new Intent(this, MatchedInfoActivity.class);
//        intent.putExtra("name", "Shi Yu");
//        intent.putExtra("age", "24");
//        intent.putExtra("handphoneNumber", "+65 81869565");
//        intent.putExtra("urlOfProfilePicture", "http://justinjackson.ca/wp-content/uploads/2008/08/justin-jackson-black-and-white-canada-profile.jpg");
//        startActivity(intent);
    }

    public void updateTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.resultTextView);
        textView.setText(text);
    }

    public void sendQuery(View view) {
        mpsgStarter.sendQuery(query, handler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
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
