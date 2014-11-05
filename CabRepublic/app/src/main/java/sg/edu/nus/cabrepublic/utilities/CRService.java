package sg.edu.nus.cabrepublic.utilities;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import sg.edu.nus.cabrepublic.nework_data.UserResponse;

/**
 * Created by zhixing on 14.11.04.
 */
public interface CRService {
    @FormUrlEncoded
    @POST("/users/signin")
    void login(@Field("email") String email,
               @Field("password") String password,
               Callback<UserResponse> cb);

    @FormUrlEncoded
    @PUT("/users")
    void updatePreference(@Header("Authorization") String token, @Field("age_min") int ageMin, @Field("age_max") int ageMax, @Field("gender_preference") int genderPreference, Callback<Object> cb);
}
