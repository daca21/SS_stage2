package myapp.com.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created  on 5/28/15.
 */
public class ArtistResult implements Parcelable{


    public String name;
    public String spotifyId;
    public String thumbnail;

    /**
     * Constructs a ArtistResult from values
     */
    public ArtistResult(String name, String spotifyId, String thumbnail) {
        this.name = name;
        this.spotifyId = spotifyId;
        this.thumbnail = thumbnail;
    }

    /**
     * Constructs a ArtistResult from a Parcel
     * @param parcel
     */
    public  ArtistResult (Parcel parcel){
        this.name = parcel.readString();
        this.spotifyId = parcel.readString();
        this.thumbnail = parcel.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(spotifyId);
        dest.writeString(thumbnail);
    }

    //http://stackoverflow.com/questions/22446359/android-class-parcelable-with-arraylist
    //https://guides.codepath.com/android/Using-Parcelable
    // Method to recreate a Question from a Parcel
    public static Creator<ArtistResult> CREATOR = new Creator<ArtistResult>() {
        @Override
        public ArtistResult createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public ArtistResult[] newArray(int size) {
            return new ArtistResult[0];
        }
    };

}
