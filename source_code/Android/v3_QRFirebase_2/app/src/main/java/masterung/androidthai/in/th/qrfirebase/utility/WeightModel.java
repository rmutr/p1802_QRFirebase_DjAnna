package masterung.androidthai.in.th.qrfirebase.utility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by masterung on 4/3/2018 AD.
 */

public class WeightModel implements Parcelable{

    private String uidString, dateTimeString, weightString;

    public WeightModel() {
    }

    public WeightModel(String uidString,
                       String dateTimeString,
                       String weightString) {
        this.uidString = uidString;
        this.dateTimeString = dateTimeString;
        this.weightString = weightString;
    }

    protected WeightModel(Parcel in) {
        uidString = in.readString();
        dateTimeString = in.readString();
        weightString = in.readString();
    }

    public static final Creator<WeightModel> CREATOR = new Creator<WeightModel>() {
        @Override
        public WeightModel createFromParcel(Parcel in) {
            return new WeightModel(in);
        }

        @Override
        public WeightModel[] newArray(int size) {
            return new WeightModel[size];
        }
    };

    public String getUidString() {
        return uidString;
    }

    public void setUidString(String uidString) {
        this.uidString = uidString;
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    public String getWeightString() {
        return weightString;
    }

    public void setWeightString(String weightString) {
        this.weightString = weightString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uidString);
        dest.writeString(dateTimeString);
        dest.writeString(weightString);
    }
}   // Main Method
