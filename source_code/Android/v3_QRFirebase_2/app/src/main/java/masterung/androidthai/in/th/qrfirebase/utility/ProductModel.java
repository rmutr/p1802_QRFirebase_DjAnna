package masterung.androidthai.in.th.qrfirebase.utility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Slump on 03/09/2018.
 */

public class ProductModel implements Parcelable {

//    Explicit
    private String dateTimeString, codeString;

    public ProductModel() {

    }   // Constructor Setter

    public ProductModel(String dateTimeString, String codeString) {
        this.dateTimeString = dateTimeString;
        this.codeString = codeString;
    }   // Constructor Getter

    protected ProductModel(Parcel in) {
        dateTimeString = in.readString();
        codeString = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateTimeString);
        dest.writeString(codeString);
    }
}   // Main Class
