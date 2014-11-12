package sg.edu.nus.cabrepublic.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zhixing on 14.11.04.
 */
public class User extends CRBaseModel implements Parcelable, Serializable {

    public String Name;
    public String Email;
    public PickUpLocation pickUpLocation;
    public PickUpLocation destinationLocation;

    // Data returned by the server:
    public int Age_min;
    public int Age_max;
    public int Gender_preference;
    public int Gender;
    public int Age;
    public int Type; // (0 for ordinary user, 1 for taxi driver)
    public String Access_token;

    public User(String name, String email, String access_token, int age_min, int age_max, int gender, int age,
                int gender_preference, PickUpLocation pickUpLocation, int type) {
        this.Name = name;
        this.Email = email;
        this.Access_token = access_token;
        this.Age_min = age_min;
        this.Age_max = age_max;
        this.Gender = gender;
        this.Age = age;
        this.Gender_preference = gender_preference;
        this.pickUpLocation = pickUpLocation;
        this.Type = type;
    }

    public User(Parcel parcel) {
        this.Name = parcel.readString();
        this.Email = parcel.readString();
        this.Access_token = parcel.readString();
        this.Age_min = parcel.readInt();
        this.Age_max = parcel.readInt();
        this.Gender = parcel.readInt();
        this.Age = parcel.readInt();
        this.Gender_preference = parcel.readInt();
        this.pickUpLocation = parcel.readParcelable(PickUpLocation.class.getClassLoader());
        this.destinationLocation = parcel.readParcelable(PickUpLocation.class.getClassLoader());
        this.Type = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.Name);
        parcel.writeString(this.Email);
        parcel.writeString(this.Access_token);
        parcel.writeInt(this.Age_min);
        parcel.writeInt(this.Age_max);
        parcel.writeInt(this.Gender);
        parcel.writeInt(this.Age);
        parcel.writeInt(this.Gender_preference);
        parcel.writeParcelable(this.pickUpLocation, 1);
        parcel.writeParcelable(this.destinationLocation, 1);
        parcel.writeInt(this.Type);
    }
}
