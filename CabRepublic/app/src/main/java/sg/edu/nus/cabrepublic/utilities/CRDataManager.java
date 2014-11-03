package sg.edu.nus.cabrepublic.utilities;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import sg.edu.nus.cabrepublic.models.User;
import sg.edu.nus.cabrepublic.responses.UserResponse;
import sg.edu.nus.cabrepublic.requests.UserLoginRequest;

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

    public User currentUser;
    private CRService crService;

    private CRDataManager(){
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

    public void loginWithCompletion(UserCredential credentials, final Handler completion) {

        Callback<UserResponse> callback = new Callback<UserResponse>() {
            @Override
            public void success(UserResponse user, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
                currentUser = user.user;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("cat", "login fail");
                //RequestError err = resolveRequestFailure(retrofitError);
                //completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));
            }
        };
        crService.login(credentials, callback);
    }



}
