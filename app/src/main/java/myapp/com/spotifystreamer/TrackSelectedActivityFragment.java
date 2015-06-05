package myapp.com.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackSelectedActivityFragment extends Fragment {

    private String LOg_TAG = TrackSelectedActivityFragment.class.getSimpleName();

    @InjectView(R.id.artist_name)
    protected TextView artist_name;

    @InjectView(R.id.album_name)
    protected TextView album_name;

    @InjectView(R.id.album_artwork)
    protected ImageView artwork_url;

    @InjectView(R.id.textView_track_name)
    protected TextView track_name;

    @InjectView(R.id.textView_duration_max)
    protected TextView duratiton_max;

    @InjectView(R.id.textView_duration_min)
    protected TextView duratiton_min;

    private String data;

    public TrackSelectedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_selected, container, false);
        ButterKnife.inject(this, rootView);



        // The display Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constant.NAME_ARTIST_ENTER_KEY)) {
            data = intent.getStringExtra(Constant.NAME_ARTIST_ENTER_KEY);
            artist_name.setText(data);

            if (intent.hasExtra(Constant.ARTIST_NAME_KEY)){
                String data_album_name = intent.getStringExtra(Constant.ARTIST_NAME_KEY);
                album_name.setText(data_album_name);
            }
            if (intent.hasExtra(Constant.URL_IMAGE_KEY)){
                String data = intent.getStringExtra(Constant.URL_IMAGE_KEY);
                Picasso.with(getActivity())
                        .load(data).into(artwork_url);
            }
            if (intent.hasExtra(Constant.TRACK_NAME_KEY)){
                String data = intent.getStringExtra(Constant.TRACK_NAME_KEY);
                track_name.setText(data);
            }

//            if (intent.hasExtra(Constant.DURATION_MS_KEY)){
//                String data = intent.getStringExtra(Constant.DURATION_MS_KEY);
//                duratiton_max.setText(data);
//            }
        }







        return rootView;
    }
}
