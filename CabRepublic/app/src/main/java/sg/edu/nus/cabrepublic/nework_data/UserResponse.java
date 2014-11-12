package sg.edu.nus.cabrepublic.nework_data;

import sg.edu.nus.cabrepublic.models.User;

/**
 * Created by zhixing on 14.11.04.
 */
public class UserResponse {
    public User User;

    public UserResponse(User mUser) {
        this.User = mUser;
    }
}
