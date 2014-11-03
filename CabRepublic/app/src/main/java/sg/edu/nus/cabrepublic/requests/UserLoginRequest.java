package sg.edu.nus.cabrepublic.requests;

import sg.edu.nus.cabrepublic.utilities.UserCredential;

/**
 * Created by zhixing on 14.11.04.
 */
public class UserLoginRequest {
    public UserCredential user;

    public UserLoginRequest(UserCredential credentials) {
        this.user = credentials;
    }
}