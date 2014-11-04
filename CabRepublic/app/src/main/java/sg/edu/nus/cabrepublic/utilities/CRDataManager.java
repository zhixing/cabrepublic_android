package sg.edu.nus.cabrepublic.utilities;

import android.content.Context;
import android.os.Message;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import sg.edu.nus.cabrepublic.models.ErrorResponse;
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

    public void loginWithCompletion(final String userEmail, String userPassword, final Handler completion) {

        Callback<UserResponse> callback = new Callback<UserResponse>() {
            @Override
            public void success(UserResponse user, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
                currentUser = user.User;
                currentUser.Email = userEmail;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                RequestError err = resolveRequestFailure(retrofitError);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));
            }
        };
        crService.login(userEmail, userPassword, callback);
    }

    public void logout(Context context){

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

}
