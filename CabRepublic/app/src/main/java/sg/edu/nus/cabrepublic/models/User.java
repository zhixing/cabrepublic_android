package sg.edu.nus.cabrepublic.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zhixing on 14.11.04.
 */
public class User extends CRBaseModel implements Parcelable, Serializable {

    public String email;
    public String access_token;
    public int age_min;
    public int age_max;
    public int gender;

    public User(String email, String access_token, int age_min, int age_max, int gender) {
        this.email = email;
        this.access_token = access_token;
        this.age_max = age_max;
        this.age_min = age_min;
        this.gender = gender;
    }

    public User(Parcel parcel) {
        this.email = parcel.readString();
        access_token = parcel.readString();
        gender = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);

    }
}
