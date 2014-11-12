package sg.edu.nus.cabrepublic.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zhixing on 14.11.05.
 */
public class PickUpLocation extends CRBaseModel implements Parcelable, Serializable {

    public static final Parcelable.Creator<PickUpLocation> CREATOR = new Parcelable.Creator<PickUpLocation>() {
        public PickUpLocation createFromParcel(Parcel in) {
            return new PickUpLocation(in);
        }

        public PickUpLocation[] newArray(int size) {
            return new PickUpLocation[size];
        }
    };

    public String locationName;
    public double longitude;
    public double latitude;

    public PickUpLocation(String locationName, double longitude, double latitude){
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public PickUpLocation(Parcel parcel){
        this.locationName = parcel.readString();
        this.longitude = parcel.readDouble();
        this.latitude = parcel.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.locationName);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitude);
    }
}
