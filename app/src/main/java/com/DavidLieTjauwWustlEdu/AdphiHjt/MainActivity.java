package com.DavidLieTjauwWustlEdu.AdphiHjt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.DavidLieTjauwWustlEdu.AdphiHjt.estimote.BeaconID;
import com.DavidLieTjauwWustlEdu.AdphiHjt.estimote.EstimoteCloudBeaconDetails;
import com.DavidLieTjauwWustlEdu.AdphiHjt.estimote.EstimoteCloudBeaconDetailsFactory;
import com.DavidLieTjauwWustlEdu.AdphiHjt.estimote.ProximityContentManager;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.cloud.model.Color;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;


    private static final String TAG = "MainActivity";

    //private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();

    private DatabaseReference mDatabase;
    private DatabaseReference loginState;




    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private ProximityContentManager proximityContentManager;

     int state;
    final int usersState = 1;

    //final String[] track = new String[2];



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("statePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("state", usersState);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Identification");
        loginState = FirebaseDatabase.getInstance().getReference().child("State");


        loginState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String firebaseState = dataSnapshot.getValue().toString();
                state = Integer.parseInt(firebaseState);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "There was an error when trying to retrieve data from firebase");
            }
        });





        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 17318, 30884)),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                Integer backgroundColor;
                if (content != null) {


                    setContentView(R.layout.signin);
                    final int userState = sharedpreferences.getInt("state",6);


                    Button submit = (Button) findViewById(R.id.submit);
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //int currentState = prefs.getInt("stateVal",0); //gets value

                            if(state == userState) { //if both are 1
                                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                String[] tokens = date.split("-");

                            ////////////////////
                                TextView txt = (TextView) findViewById(R.id.eltNumber);
                                String msg = txt.getText().toString();
                            /////////////////////////

                                mDatabase.child(tokens[2]).setValue("3");
                                txt.setText(" "); //clears text
                               // editor.putInt("stateVal", 0); //changes stateVal to 0
                                setContentView(R.layout.outofrange);
                            }
                           else{
                                setContentView(R.layout.activity_main);
                           }

                        }
                    });

                } else {
                    setContentView(R.layout.activity_main);
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }
}
