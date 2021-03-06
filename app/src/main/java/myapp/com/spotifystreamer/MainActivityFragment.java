package myapp.com.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import myapp.com.spotifystreamer.adapter.SearchArrayAdapter;
import myapp.com.spotifystreamer.models.ArtistResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private String LOG_TAG = MainActivityFragment.class.getSimpleName();


    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(ArtistResult artist);
    }

    public Callbacks mCallBack;

    ArrayAdapter<ArtistResult> mAdapter;
    ArtistResult art_reslt;
    ArrayList<ArtistResult> arrayOfSearchArtist;
    private int mPosition = ListView.INVALID_POSITION;

    public MainActivityFragment() {
    }

    @InjectView(R.id.listview_search_result)
    protected ListView _listView;

    @InjectView(R.id.editText_search)
    protected EditText editText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (Callbacks) activity;
        }catch (IllegalStateException e){

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    public static MainActivityFragment getInstance (){
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        return mainActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        if(savedInstanceState != null) {
            // read the artistresult list from the saved state
            arrayOfSearchArtist = savedInstanceState.getParcelableArrayList(Constant.LIST_ARTIST_STATE);
            mAdapter = new SearchArrayAdapter(getActivity(),
                    R.layout.list_item_search_result, arrayOfSearchArtist);
            _listView.setAdapter(mAdapter);
//            if (mPosition != ListView.INVALID_POSITION) {
//                // If we don't need to restart the loader, and there's a desired position to restore
//                // to, do so now.
//                _listView.smoothScrollToPosition(mPosition);
//            }
        } else {
            // load the  list
            arrayOfSearchArtist = new ArrayList<>();
        }

        //Get text from EditextField to search the artists
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mAdapter = new SearchArrayAdapter(getActivity(),
                            R.layout.list_item_search_result, arrayOfSearchArtist);
                    mAdapter.clear();
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Log.d(LOG_TAG, "Clicked");
                mPosition = position;
                ArtistResult artist_data = mAdapter.getItem(position);
//
//                Intent intent = new Intent(getActivity(), DisplayTop10TrackActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, artist_data.spotifyId)
//                        .putExtra(Constant.NAME_ARTIST_ENTER_KEY, artist_data.name );
//                startActivity(intent);
                mCallBack.onItemSelected(artist_data);

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(Constant.SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constant.LIST_ARTIST_STATE, arrayOfSearchArtist);
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(Constant.SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //performSearch();
//      mAdapter.notifyDataSetChanged();

    }

    private void performSearch() {
        //http://stackoverflow.com/questions/9854618/hide-keyboard-after-user-searches
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        String search_artist = editText.getText().toString();
        if (search_artist.isEmpty()){
            ToastText(getResources().getString(R.string.edittext_is_empty));
//            editText.findFocus();
        }
        else{
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();

            spotify.searchArtists(search_artist, new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {

                    List<Artist> items = artistsPager.artists.items;
                    Artist obj = null;
//                    Log.d("Album success", items.toString());
                    String name = null;
                    String img_url = null;
                    String spotify_id = null;

                    if (items.size() == 0){
//                        Log.d("items is emplty", items.toString());
                        Utils.showToastFromBackground(getResources().getString(R.string.artist_not_found) + items.toString(), getActivity());
                    }

                    for (int i = 0; i < items.size(); i++) {
                        obj = items.get(i);
                        name = obj.name;
                        spotify_id = obj.id;
//                        Log.d(LOG_TAG, name + " - id :" + spotify_id);
                        for (Image imtemp : obj.images) {
                            if (imtemp.width > 75 ) {
//                                Log.d(LOG_TAG, imtemp.url.toString());
                                img_url = imtemp.url.toString();
                            }
                        }
                        art_reslt = new ArtistResult(name, spotify_id, img_url);
                        arrayOfSearchArtist.add(art_reslt);

                    }

//                    PopulateListviewFromBackground();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Do something
                            _listView.setAdapter(mAdapter);

                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    //If error display a toast
                    Utils.showToastFromBackground(error.toString(), getActivity());
                }
            });
        }
    }

    void ToastText(String s){
        Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
    }


    public void setActivateOnItemClick(boolean activateOnItemClick) {
        _listView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);

    }
}
