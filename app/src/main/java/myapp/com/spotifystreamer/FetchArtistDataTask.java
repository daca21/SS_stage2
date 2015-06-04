package myapp.com.spotifystreamer;

import android.os.AsyncTask;

import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Dak on 6/2/15.
 */
public class FetchArtistDataTask extends AsyncTask<String, Void, ArtistsPager>{

    //TODO with asyncTask + spotify api
    @Override
    protected ArtistsPager doInBackground(String... params) {
        return null;
    }
}
