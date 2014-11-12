package sg.edu.nus.cabrepublic.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import sg.edu.nus.cabrepublic.models.ErrorResponse;
import sg.edu.nus.cabrepublic.models.MatchPollResponse;
import sg.edu.nus.cabrepublic.models.PickUpLocation;
import sg.edu.nus.cabrepublic.models.RequestError;
import sg.edu.nus.cabrepublic.models.User;
import sg.edu.nus.cabrepublic.nework_data.UserResponse;

/**
 * Created by zhixing on 14.11.04.
 */
public class CRDataManager {

    private static final String ROOT_ADDRESS = "http://128.199.143.166:8081";
    private static CRDataManager sharedManager;
    public static final int EMPTY_INT = -1;
    public static final String EMPTY_STRING = "EMPTY_STRING";

    public static final int NETWORK_FAILURE = 1;
    public static final int UNAUTHORIZED = 2;
    public static final int REASONED_ERROR = 3;
    public static final int INTERNAL_ERROR = 4;
    public static final int NOT_FOUND = 5;
    public static final int UNKNOWN = 6;

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

    public User currentUser;
    private CRService crService;

    private CRDataManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        crService = (new RestAdapter.Builder()
                .setEndpoint(ROOT_ADDRESS)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("cabrepublic"))
                .build()).create(CRService.class);
    }

    public static CRDataManager getInstance() {
        if (sharedManager == null) {
            sharedManager = new CRDataManager();
        }
        return sharedManager;
    }

    public static void clearData() {
        sharedManager = null;
    }

    public void loginWithCompletion(final String userEmail, String userPassword, final Handler completion) {

        Callback<UserResponse> callback = new Callback<UserResponse>() {
            @Override
            public void success(UserResponse user, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
                currentUser = user.User;
                currentUser.Email = userEmail;

                // TODO: these should be retrieved from coalition server or GPS:
                currentUser.Name = "Peter Lim";
                currentUser.pickUpLocation = CRDataManager.getInstance().getPickUpLocations().get(0);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                RequestError err = resolveRequestFailure(retrofitError);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));
            }
        };
        crService.login(userEmail, userPassword, callback);
    }

    public void updatePreferenceWithCompletion(int ageMin, int ageMax, int gender, final Handler completion) {
        Callback<Object> callback = new Callback<Object>() {
            @Override
            public void success(Object user, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                RequestError err = resolveRequestFailure(retrofitError);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));
            }
        };
        Log.d("cabrepublic", "Access token is " + currentUser.Access_token);
        crService.updatePreference(currentUser.Access_token, ageMin, ageMax, gender, callback);
    }

    public void pollMatchStatusWithCompletion(final Handler completion) {
        Callback<MatchPollResponse> callback = new Callback<MatchPollResponse>() {
            @Override
            public void success(MatchPollResponse matchPollResponse, Response response) {
                completion.sendMessage(Message.obtain(null, 0, matchPollResponse.Email));
            }

            @Override
            public void failure(RetrofitError error) {
                RequestError err = resolveRequestFailure(error);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));

            }
        };
        crService.pollMatchingStatus(currentUser.Access_token, callback);
    }

    public void deleteMatchingWithCompletion(final Handler completion) {
        Callback<Object> callback = new Callback<Object>() {
            @Override
            public void success(Object deleteResponse, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
            }

            @Override
            public void failure(RetrofitError error) {
                RequestError err = resolveRequestFailure(error);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));

            }
        };
        crService.deleteMatching(currentUser.Access_token, callback);
    }

    public void logout(Context context) {

    }

    private RequestError resolveRequestFailure(RetrofitError retrofitError) {
        try {
            ErrorResponse resp = (ErrorResponse) retrofitError.getBodyAs(ErrorResponse.class);
            if (resp != null) {
                return new RequestError(REASONED_ERROR, resp.errors[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (retrofitError.getResponse() != null) {
            int responseCode = retrofitError.getResponse().getStatus();
            switch (responseCode) {
                case 401: {
                    return new RequestError(UNAUTHORIZED);
                }

                case 404: {
                    return new RequestError(NOT_FOUND);
                }

                case 500: {
                    return new RequestError(INTERNAL_ERROR);
                }

                default: {
                    return new RequestError(UNKNOWN);
                }
            }
        } else {
            return new RequestError(UNKNOWN);
        }
    }

    public ArrayList<PickUpLocation> getPickUpLocations(){
        ArrayList<PickUpLocation> pickUpLocations = new ArrayList<PickUpLocation>();
        pickUpLocations.add(new PickUpLocation("Computer Center", 103.772808, 1.297348));
        pickUpLocations.add(new PickUpLocation("COM1", 103.773593, 1.294796));
        pickUpLocations.add(new PickUpLocation("Central Library", 103.772475, 1.296619));
        pickUpLocations.add(new PickUpLocation("Raffles Hall", 103.774309, 1.299139));
        pickUpLocations.add(new PickUpLocation("UCC", 103.771632, 1.301236));
        pickUpLocations.add(new PickUpLocation("Utown", 103.774325, 1.303548));
        pickUpLocations.add(new PickUpLocation("UHC", 103.776246, 1.298882));
        pickUpLocations.add(new PickUpLocation("University Hall", 103.778032, 1.29753));
        pickUpLocations.add(new PickUpLocation("Faculty of Science", 103.78072, 1.297402));
        pickUpLocations.add(new PickUpLocation("Faculty of Dentistry", 103.782045, 1.297064));
        pickUpLocations.add(new PickUpLocation("NUH", 103.784904, 1.293814));
        pickUpLocations.add(new PickUpLocation("PGP", 103.781202, 1.290945));
        pickUpLocations.add(new PickUpLocation("Temasek Life Sciences Lab", 103.776675, 1.293803));
        pickUpLocations.add(new PickUpLocation("Business School", 103.773971, 1.292275));
        pickUpLocations.add(new PickUpLocation("Shears Hall", 103.775409, 1.291803));
        pickUpLocations.add(new PickUpLocation("Temasek Hall", 103.771761, 1.293101));

        return pickUpLocations;
    }
}
