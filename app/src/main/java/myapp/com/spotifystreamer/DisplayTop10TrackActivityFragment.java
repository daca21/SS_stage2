package myapp.com.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import myapp.com.spotifystreamer.adapter.TopTrackAdapter;
import myapp.com.spotifystreamer.models.TrackResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class DisplayTop10TrackActivityFragment extends Fragment {

    public static String data_artist_name;
    private String LOG_TAG = DisplayTop10TrackActivity.class.getSimpleName();

    private static final String NAME_ARTIST_ENTER_KEY = "name_artist_enter";
    private static final String TOP_TRACK_KEY = "name_artist_enter";


    public String data;
    ArrayList<TrackResult> arrayOfTracks;
    TrackResult _trackResult;
    TopTrackAdapter mAdapter;
    private Handler handler = new Handler();


    @InjectView(android.R.id.list)
    protected ListView _listView;

    public DisplayTop10TrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_display_top10_track, container, false);
        ButterKnife.inject(this, rootView);

        // The display Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            data = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (intent.hasExtra(NAME_ARTIST_ENTER_KEY)){
//                Log.d(LOG_TAG, "success getintent "+ intent.getStringExtra(NAME_ARTIST_ENTER_KEY) );
                data_artist_name = intent.getStringExtra(NAME_ARTIST_ENTER_KEY);

            }
//            To see debug spotify id
//            ((TextView) rootView.findViewById(R.id.detail_text))
//                    .setText(data);
        }

        if(savedInstanceState != null) {
            // read arrayOfTracks list from the saved state
            arrayOfTracks = savedInstanceState.getParcelableArrayList(TOP_TRACK_KEY);
            mAdapter = new TopTrackAdapter(getActivity(),
                    android.R.id.list, arrayOfTracks);
            _listView.setAdapter(mAdapter);
        } else {
            // load the top 10 track
            arrayOfTracks = new ArrayList<>();
            searchTop10Track();
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TOP_TRACK_KEY, arrayOfTracks);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        To debug
//        serachTop10Track();
    }

    private void searchTop10Track() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> mTrackoption = new HashMap<>();

        if(location.isEmpty()) {
            mTrackoption.put("country", "US"); //  set US by default if country is empty
        }
        else{
            mTrackoption.put("country", location);
        }


        spotify.getArtistTopTrack(data, mTrackoption, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                Log.d(LOG_TAG, "success" + tracks.tracks.toString());

                List<Track> tracksList = tracks.tracks;

                Track obj = null;
                String track_name = null;
                String album_name = null;
                String thumdnail_Large = null;
                String thumdnail_Small = null;
                String previewURL = null;

                if(tracksList.size() == 0){
                    Utils.showToastFromBackground(getResources().getString(R.string.tracks_empty), getActivity());
                }

                for (int i = 0; i < tracksList.size(); i++) {

                    obj = tracksList.get(i);
                    track_name = obj.name;
                    album_name = obj.album.name;
                    previewURL = obj.preview_url;

                    for (Image imtemp : obj.album.images) {
                        if (imtemp.height > 400 ) {
                            thumdnail_Large = imtemp.url.toString();
                        }
                        else if (imtemp.height > 200)
                            thumdnail_Small = imtemp.url.toString();
                    }
                    _trackResult = new TrackResult(track_name, album_name, thumdnail_Large, thumdnail_Small, previewURL);
                    arrayOfTracks.add(_trackResult);

                }

                new Thread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Do something
                        mAdapter = new TopTrackAdapter(getActivity(),
                                android.R.id.list, arrayOfTracks);
                        handler.post( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                _listView.setAdapter(mAdapter);
                            }
                        } );
                    }
                } ).start();
            }

            @Override
            public void failure(RetrofitError error) {
//                Log.d(LOG_TAG, error.getMessage().toString());
                Utils.showToastFromBackground(error.toString(), getActivity());
            }
        });

    }

    /**
     * Sets the Action Bar for new Android versions.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void actionBarSetup() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            ActionBar ab  = ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle("dd");
//            ab.setSubtitle(s);
//        }
//    }
}
