package sg.edu.nus.cabrepublic.responses;

import sg.edu.nus.cabrepublic.models.User;

/**
 * Created by zhixing on 14.11.04.
 */
public class UserResponse {
    public User user;

    public UserResponse(User mUser) {
        this.user = mUser;
    }
}
