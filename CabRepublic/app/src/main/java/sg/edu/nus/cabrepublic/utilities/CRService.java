package sg.edu.nus.cabrepublic.utilities;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import sg.edu.nus.cabrepublic.requests.UserLoginRequest;
import sg.edu.nus.cabrepublic.responses.UserResponse;

/**
 * Created by zhixing on 14.11.04.
 */
public interface CRService {
    @POST("/users/signin")
    void login(@Body UserCredential user, Callback<UserResponse> cb);
}
