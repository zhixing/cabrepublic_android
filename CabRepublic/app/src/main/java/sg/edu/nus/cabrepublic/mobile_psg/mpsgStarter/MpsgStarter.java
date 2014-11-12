package sg.edu.nus.cabrepublic.mobile_psg.mpsgStarter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

import sg.edu.nus.cabrepublic.HomePageActivity;
import sg.edu.nus.cabrepublic.mobile_psg.sensorMonitor.ContextUpdatingService;

public class MpsgStarter {
	public static MPSG mpsg = null;
    
    private int timeout = 10; //10 seconds timeout for connecting
    private static final int SERVERPORT = 5000;

    private static MpsgStarter mpsgStarter;
    private static Context myContext = null;
    private static Handler mHandler;
    private static String resultStr = "";
    private static String connStatus = "Start MPSG";
    private static String resultString = "";
    private static String queryStatus = "invisible";


    public MpsgStarter(Context context) {
        myContext = context;
        mpsgStarter = this;
    }

    public static MpsgStarter getInstance() {
        return mpsgStarter;
    }

    public void initializeMPSG(String mpsgName, String staticContextData, String contextType) {
        // Register
        mpsg = new MPSG(myContext, SERVERPORT);
        MPSG.setRegistrationContextInformation(mpsgName, staticContextData, contextType);

        long registerStartTime = System.currentTimeMillis();
        long registerEndTime = 0;

        // Search for a proxy and connect to the best proxy
        MPSG.searchProxy(); // Commented temporarily to test the MPSG-proxy-coalition flow
        	
        	/*// Connect to the selected proxy
        	Thread mpsgconnect = new Thread() {
        		public void run() {
        			mpsg.connect();
        		}
        	};
        	mpsgconnect.start();*/

        int i = 0;
        // Wait for a result in registering through proxy
        while (MPSG.statusString.contentEquals("Connecting")) {
            if (i > timeout) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }

        if (MPSG.statusString.contentEquals("Connected")) {
            registerEndTime = System.currentTimeMillis();
            // Start the Service which updates the context information for the MPSG
            Intent contextUpdater = new Intent(myContext, ContextUpdatingService.class);
            myContext.startService(contextUpdater);
        } else {
            registerEndTime = System.currentTimeMillis();
            // TODO: Code for starting MPSG old directly without proxy
        }
        Log.d("EXPERIMENTAL_RESULTS", "Total response time for registration: " + Math.abs(registerEndTime - registerStartTime));
    }

    public void sendQuery(final String query, Handler handler) {
        mHandler = handler;
        //mpsg.setQueryString(queryString);
        // Start a new thread to send out the query
        //format is select.. blah blah blah
        Thread queryThread = new Thread() {
            public void run() {
                mpsg.sendQuery(query);
            }
        };
        queryThread.start();
    }

    // Used by TCP_Session_Handler to return results from coalition server.
    public static void setQueryResult (String result) {
        Log.d("MPSG", "Setting query result to " + result);
        resultStr = result;

        String[] rows = result.split("\n");
        HashMap<String, HashMap<String, String>> hashMapOfResults = new HashMap<String, HashMap<String, String>>();

        for (String s : rows) {
            String emailOfPerson = null;
            HashMap<String, String> hashMapOfAttributes = new HashMap<String,String>();
            String[] columns = s.split(":");

            //after split example: person.name=zhixing
            for (String col : columns) {
                String[] attriValueArray = col.split("=");
                if (attriValueArray[0].equals("person.name")) {
                    emailOfPerson = attriValueArray[1];
                } else {
                    hashMapOfAttributes.put(attriValueArray[0], attriValueArray[1]);
                }
            }

            if (emailOfPerson != null) {
                hashMapOfResults.put(emailOfPerson, hashMapOfAttributes);
            }
        }

        Message resultMessage = Message.obtain(null, 0, hashMapOfResults);
        mHandler.sendMessage(resultMessage);

        Log.d("EXPERIMENTAL_RESULTS", "Time for getting query response:" + Math.abs(System.currentTimeMillis() - MPSG.queryStart));
    }

    public void updateDynamicContextAttribute(String attributeName, String newValue) {
        MPSG.DynamicContextData.put(attributeName, newValue);
    }

    public void disconnect() {
        // Start a new thread to send out the query
        Thread leaveThread = new Thread() {
            public void run() {
                mpsg.disconnect();
            }
        };
        leaveThread.start();

        int i = 0;
        // Wait for a result in registering through proxy
        while (MPSG.leaveStatusString.contentEquals("Disconnecting")) {
            if (i > timeout) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
        if (MPSG.leaveStatusString.contentEquals("Disconnected")) {
            connStatus = "visible";
            queryStatus = "invisible";
            resultString = "";

            // Do cleanup of data structures
            MPSG.conn = null;
            MPSG.datain = null;
            MPSG.dnsSearchStatus = false;
            MPSG.ongoingSession = false;
            MPSG.sessionStatusFlag = false;
            MPSG.DynamicContextData = null;
            MPSG.iplist = null;
            MPSG.proxyIp = null;
            MPSG.prevProxyIP = null;
            MPSG.subnetSearchStatus = false;
        }
    }
}
