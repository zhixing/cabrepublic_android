package sg.edu.nus.cabrepublic.utilities;

import android.os.Message;
import android.os.Handler;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.cabrepublic.models.UserResponse;

/**
 * Created by zhixing on 14.11.04.
 */
public class CRDataManager {

    private static final String ROOT_ADDRESS = "https://wanmen.org/api/v1/";
    private static CRDataManager sharedManager;
    public static final int EMPTY_INT = -1;
    public static final String EMPTY_STRING = "EMPTY_STRING";


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
        /*
        Callback<UserResponse> callback = new Callback<UserResponse>() {
            @Override
            public void success(UserResponse user, Response response) {
                completion.sendMessage(Message.obtain(null, 0, null));
                currentUser = user.user;
                currentUser.myObserver = CRDataManager.this;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                RequestError err = resolveRequestFailure(retrofitError);
                completion.sendMessage(Message.obtain(null, err.errorCode, err.reason));
            }
        };
        wanmenService.login(new UserLoginRequest(credentials), callback);
        */
    }


}
