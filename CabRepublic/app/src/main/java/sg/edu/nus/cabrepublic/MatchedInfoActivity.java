package sg.edu.nus.cabrepublic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MatchedInfoActivity extends Activity {
    private final static int CAB_FOUND = 0;
    private final static int CAB_NOT_FOUND = 1;

    private GoogleMap map;
    private RoundedImageView profilePicture;
    private TextView nameAndAgeTextView;
    private ImageButton callButton;
    private ImageButton messageButton;
    private TextView countDownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_info);

        // Retrieve matched info from Intent object
        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String urlOfProfilePicture = intent.getStringExtra("urlOfProfilePicture");
        String handphoneNumber = intent.getStringExtra("number");
        String pickUpName = intent.getStringExtra("pickup_name");
        double pickUpLongtitude = intent.getDoubleExtra("pickup_longitude", 0.0);
        double pickUpLatitude = intent.getDoubleExtra("pickup_latitude", 0.0);

        this.initializeNameAndAgeTextView(name, age);
        this.intializeProfilePicFromURL(urlOfProfilePicture);
        this.initializeContactButtons(handphoneNumber);
        this.initializeGoogleMap(pickUpName, pickUpLongtitude, pickUpLatitude);
        final CountDownTimer countDownTimer = this.initializeCountDownTextView();
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancelBtn);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == CAB_FOUND){ //Event when cab is found
                    countDownTimer.cancel();
                    countDownTextView.setText("Taxi Car Plate: SDE2123Z");
                    vibratePhone(500);
                }else { //Event when cab is not found

                }
            }
        };

        final Thread threadToFindCab = new Thread() {
            @Override
            public void run(){
                //do shit
                synchronized (this) {
                    try {
                        wait(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(CAB_FOUND);
            }
        };
        threadToFindCab.start();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                threadToFindCab.interrupt();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matched_info, menu);
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

    private void initializeGoogleMap(String pickUpName, double longtitude, double latitude) {
        Location pickUpLocation = new Location("");
        pickUpLocation.setLongitude(longtitude);
        pickUpLocation.setLatitude(latitude);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.pickUpLocationMap)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        this.drawMarker(pickUpName, pickUpLocation);
    }

    private void intializeProfilePicFromURL(String url) {
        profilePicture = (RoundedImageView) findViewById(R.id.matchedProfilePicture);
        profilePicture.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.man));
    }

    private void initializeNameAndAgeTextView(String name, String age) {
        nameAndAgeTextView = (TextView) findViewById(R.id.matchedNameAndAge);

        nameAndAgeTextView.setText(name + ", " + age);
    }

    private void initializeContactButtons(String handphoneNumber) {
        final String phoneNumber = handphoneNumber;
        callButton = (ImageButton) findViewById(R.id.callButton);
        messageButton = (ImageButton) findViewById(R.id.messageButton);

        View.OnTouchListener vibrateOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    vibratePhone(100);
                }
                return false;
            }
        };

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });

        callButton.setOnTouchListener(vibrateOnTouchListener);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri= "smsto:"+phoneNumber;
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                intent.putExtra("compose_mode", true);
                startActivity(intent);
                finish();
            }
        });

        messageButton.setOnTouchListener(vibrateOnTouchListener);
    }

    private CountDownTimer initializeCountDownTextView() {
        countDownTextView = (TextView) findViewById(R.id.countDownTextView);
        final CountDownTimer countDownTimer = new CountDownTimer(300000, 1000) {
            int numberOfDots = 0;
            public void onTick(long millisUntilFinished) {
                //countDownTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
                long secondsRemaining = millisUntilFinished/1000;
                String mins = "" + secondsRemaining/60;
                String secs = "" + secondsRemaining%60;

                if (secs.length() < 2) {
                    secs = "0" + secs;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Searching for cab");
                for (int i = 0; i < numberOfDots; ++i) {
                    sb.append(".");
                }
                for (int i = 0; i < 3-numberOfDots; ++i) {
                    sb.append(" ");
                }
                sb.append("(" + mins + ":" + secs + ")");
                countDownTextView.setText(sb.toString());
                ++numberOfDots;
                if (numberOfDots > 3) {
                    numberOfDots = 0;
                }
            }

            public void onFinish() {
                countDownTextView.setText("No cabs available");
            }
        }.start();

        return countDownTimer;
    }

    private void drawMarker(String title, Location location){
        map.clear();
        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        // zoom to the current location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

        // add a marker to the map indicating our current position
        map.addMarker(new MarkerOptions()
                .title(title)
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude()));
    }

    private void vibratePhone(int miliseconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(miliseconds);
    }

//    class downloadImageTask extends AsyncTask<Void,Void,Void>
//    {
//        @Override
//        protected Void doInBackground(Void... params) {
//            URL newUrl = new URL(string);
//            Bitmap profilePicBitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
//
//            profilePicture.setImageBitmap(profilePicBitmap);
//        }
//    }
}
