package myapp.com.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

//    private String data;
//    private static String artistName = null;
    ArrayList<TrackResult> arrayOfTracks;
    TrackResult _trackResult;
    TopTrackAdapter mAdapter;
    private boolean mTwoPane;
    private String artistID;


    @InjectView(android.R.id.list)
    protected ListView _listView;

    public static DisplayTop10TrackActivityFragment getInstance(String spotifyId, boolean mTwoPane) {
        DisplayTop10TrackActivityFragment newTop10Frament = new DisplayTop10TrackActivityFragment();
        Bundle bundle =  new Bundle();
        bundle.putString(Constant.ARTIST_ID_KEY, spotifyId);
        bundle.putBoolean(Constant.IS_TWO_PANE, mTwoPane);
        newTop10Frament.setArguments(bundle);

        return  newTop10Frament;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_display_top10_track, container, false);
        ButterKnife.inject(this, rootView);


        mTwoPane = getArguments().getBoolean(Constant.IS_TWO_PANE);
        artistID = getArguments().getString(Constant.ARTIST_ID_KEY);
//        if(artistID != null) {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("artID", artistID);
//            editor.apply();
//        }

        if(savedInstanceState != null) {
            // read arrayOfTracks list from the saved state
            arrayOfTracks = savedInstanceState.getParcelableArrayList(Constant.TOP_TRACK_KEY);
            mAdapter = new TopTrackAdapter(getActivity(),
                    android.R.id.list, arrayOfTracks);
            _listView.setAdapter(mAdapter);
        } else {
            // load the top 10 track
            arrayOfTracks = new ArrayList<>();
//            if(artistID == null){
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                artistID = preferences.getString("artID", "");
//            }
            searchTop10Track(artistID);

        }

        //onClick item show the track selected
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
//                Log.d(LOG_TAG, "Clicked on postion: " + position);
//                TrackResult track_data = mAdapter.getItem(position);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DialogFragment newFragment = new TrackSelectedActivityFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("tracks", arrayOfTracks);
                newFragment.setArguments(bundle);

                if ( mTwoPane){
                    newFragment.show(fragmentManager, "dialog");
                }
                else{
                    Intent intent = new Intent(getActivity(), TrackSelectedActivity.class)
                            .putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constant.TOP_TRACK_KEY, arrayOfTracks);
        outState.putString("artistID", artistID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        To debug
//        serachTop10Track();

    }

    private void searchTop10Track(final String id) {

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

        spotify.getArtistTopTrack(id, mTrackoption, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                Log.d(LOG_TAG, "success" + tracks.tracks.toString());

                List<Track> tracksList = tracks.tracks;

                Track obj;
                String track_name;
                String album_name;
                String thumbnail_Large = null;
                String thumbnail_Small = null;
                String previewURL;
//                String artistname = null;

                if (tracksList.size() == 0) {
                    Utils.showToastFromBackground(getResources().getString(R.string.tracks_empty), getActivity());
                }

                for (int i = 0; i < tracksList.size(); i++) {

                    obj = tracksList.get(i);
                    track_name = obj.name;
                    album_name = obj.album.name;
                    previewURL = obj.preview_url;

                    for (Image imtemp : obj.album.images) {
                        if (imtemp.height > 400) {
                            thumbnail_Large = imtemp.url.toString();
                        } else if (imtemp.height > 200)
                            thumbnail_Small = imtemp.url.toString();
                    }
                    _trackResult = new TrackResult(track_name, album_name, thumbnail_Large,
                            thumbnail_Small, previewURL, obj.duration_ms, data_artist_name);
                    arrayOfTracks.add(_trackResult);

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do something
                        mAdapter = new TopTrackAdapter(getActivity(),
                                android.R.id.list, arrayOfTracks);
                        _listView.setAdapter(mAdapter);

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
//                Log.d(LOG_TAG, error.getMessage().toString());
                Utils.showToastFromBackground(error.toString(), getActivity());
            }
        });

    }



}
