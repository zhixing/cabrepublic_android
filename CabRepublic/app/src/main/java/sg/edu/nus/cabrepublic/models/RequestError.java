package sg.edu.nus.cabrepublic.models;

/**
 * Created by zhixing on 14.11.04.
 */
public class RequestError {
    public int errorCode;
    public String reason;

    public RequestError(int errorCode) {
        this.errorCode = errorCode;
        reason = "";
    }

    public RequestError(int errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }
}
