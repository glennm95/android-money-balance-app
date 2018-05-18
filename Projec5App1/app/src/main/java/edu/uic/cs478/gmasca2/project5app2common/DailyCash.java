package edu.uic.cs478.gmasca2.project5app2common;

/**
 * Created by Glenn on 29-Apr-18.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class DailyCash implements Parcelable {
    public int mDay = 25 ;
    public int mMonth = 4 ;
    public int mYear = 2018 ;
    public int mCash = 8988 ;
    public String mDayOfWeek = "Wednesday" ;

    public DailyCash(int day, int month, int year, int closeBalance, String dayOfWeek) {
        mDay = day;
        mMonth = month;
        mYear = year;
        mCash = closeBalance;
        mDayOfWeek = dayOfWeek;
    }

    public DailyCash(Parcel in) {
        mDay = in.readInt() ;
        mMonth = in.readInt() ;
        mYear = in.readInt() ;
        mCash = in.readInt() ;
        mDayOfWeek = in.readString() ;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mDay);
        out.writeInt(mMonth) ;
        out.writeInt(mYear) ;
        out.writeInt(mCash) ;
        out.writeString(mDayOfWeek) ;
    }

    public static final Parcelable.Creator<DailyCash> CREATOR
            = new Parcelable.Creator<DailyCash>() {

        public DailyCash createFromParcel(Parcel in) {
            return new DailyCash(in) ;
        }

        public DailyCash[] newArray(int size) {
            return new DailyCash[size];
        }
    };

    public int describeContents()  {
        return 0 ;
    }

}