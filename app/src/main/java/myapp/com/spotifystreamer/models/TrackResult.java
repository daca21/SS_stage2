package myapp.com.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created  on 5/29/15.
 */
public class TrackResult implements Parcelable {

    public String track_name;
    public String album_name;
    public String thumbnail_large; // 600px
    public String thumbnail_small; //200px
    public String preview_url; //use to stream audio
    public Long duration_ms;
    public String artist_name;

    //setter
    public TrackResult(String name, String album_name, String thumbnail_large,String thumbnail_small, String preview_url, Long duration_ms, String artist_name ) {
        this.track_name = name;
        this.album_name = album_name;
        this.thumbnail_large = thumbnail_large;
        this.thumbnail_small = thumbnail_small;
        this.preview_url = preview_url;
        this.duration_ms = duration_ms;
        this.artist_name = artist_name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.track_name);
        dest.writeString(this.album_name);
        dest.writeString(this.thumbnail_large);
        dest.writeString(this.thumbnail_small);
        dest.writeString(this.preview_url);
        dest.writeLong(this.duration_ms);
        dest.writeString(this.artist_name);
    }

    private TrackResult(Parcel in) {
        this.track_name = in.readString();
        this.album_name = in.readString();
        this.thumbnail_large = in.readString();
        this.thumbnail_small = in.readString();
        this.preview_url = in.readString();
        this.duration_ms = in.readLong();
        this.artist_name = in.readString();
    }

    public static final Parcelable.Creator<TrackResult> CREATOR = new Parcelable.Creator<TrackResult>() {
        public TrackResult createFromParcel(Parcel source) {
            return new TrackResult(source);
        }

        public TrackResult[] newArray(int size) {
            return new TrackResult[size];
        }
    };
}

