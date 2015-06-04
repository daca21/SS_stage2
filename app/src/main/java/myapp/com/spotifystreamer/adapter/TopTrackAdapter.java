package myapp.com.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myapp.com.spotifystreamer.R;
import myapp.com.spotifystreamer.models.TrackResult;

/**
 * Created  on 5/30/15.
 */
public class TopTrackAdapter extends ArrayAdapter<TrackResult> {

    private final LayoutInflater inflater;
    private Context context;

    ViewHolder holder = null;

    public TopTrackAdapter(Context context, int resourceId, List<TrackResult> trackResultList) {
        super(context, resourceId, trackResultList);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        TrackResult search_item = getItem(position);

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.list_item_search_selected, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        // Populate the data into the template view using the data object
        holder.track_name.setText(search_item.track_name);
        holder.album_name.setText(search_item.album_name);
        String imageUrl = search_item.thumbnail_small;
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(holder.img);


        // Return the completed view to render on screen
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.list_item_track_name)
        TextView track_name;
        @InjectView(R.id.list_item_album_name)
        TextView album_name;
        @InjectView(R.id.list_item_img)
        ImageView img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
