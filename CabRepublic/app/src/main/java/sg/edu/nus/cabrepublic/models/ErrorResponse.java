package sg.edu.nus.cabrepublic.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhixing on 14.11.04.
 */
public class ErrorResponse {
    @SerializedName("errors")
    public String[] errors;
}
