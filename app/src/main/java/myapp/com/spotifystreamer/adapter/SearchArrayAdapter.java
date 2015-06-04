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
import myapp.com.spotifystreamer.models.ArtistResult;

/**
 * Created  on 5/28/15.
 */
public class SearchArrayAdapter extends ArrayAdapter<ArtistResult> {

    private final LayoutInflater inflater;
    private Context context;

    ViewHolder holder = null;

    public SearchArrayAdapter(Context context, int resourceId, List<ArtistResult> artist_result) {
        super(context, resourceId, artist_result);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        ArtistResult search = getItem(position);

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.list_item_search_result, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        // Populate the data into the template view using the data object
        holder.name.setText(search.name);
        String imageUrl = search.thumbnail;
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(holder.img);


        // Return the completed view to render on screen
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.list_item_title_textview) TextView name;
        @InjectView(R.id.list_item_thumbnail) ImageView img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
