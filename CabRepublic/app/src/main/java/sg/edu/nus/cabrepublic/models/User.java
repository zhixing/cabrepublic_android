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
    public int age;

    public User(String email, String access_token, int age_min, int age_max, int gender, int age) {
        this.email = email;
        this.access_token = access_token;
        this.age_min = age_min;
        this.age_max = age_max;
        this.gender = gender;
        this.age = age;
    }

    public User(Parcel parcel) {
        this.email = parcel.readString();
        this.access_token = parcel.readString();
        this.age_min = parcel.readInt();
        this.age_max = parcel.readInt();
        this.gender = parcel.readInt();
        this.age = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.access_token);
        parcel.writeInt(this.age_min);
        parcel.writeInt(this.age_max);
        parcel.writeInt(this.gender);
        parcel.writeInt(this.age);
    }
}
