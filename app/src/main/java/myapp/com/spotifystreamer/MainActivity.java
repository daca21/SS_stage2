package myapp.com.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import myapp.com.spotifystreamer.models.ArtistResult;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {

//    private static final String FRAGMENT = "frament";
//    private Fragment mFragment;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.artist_details_container) != null) {
            mTwoPane = true;

            ((MainActivityFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.artist_search_container))
                    .setActivateOnItemClick(true);


        }else {
            mTwoPane = false;
            if (savedInstanceState == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, MainActivityFragment.getInstance())
                        .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(ArtistResult artist) {
        if (mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artist_details_container, DisplayTop10TrackActivityFragment.getInstance(artist.spotifyId, mTwoPane))
                    .commit();
        }else {
            Intent intent = new Intent(this,DisplayTop10TrackActivity.class);
            intent.putExtra(Constant.ARTIST_NAME_KEY,artist.name);
            intent.putExtra(Constant.ARTIST_ID_KEY,artist.spotifyId);
            intent.putExtra(Constant.IS_TWO_PANE,mTwoPane);
            this.startActivity(intent);

        }
    }
}
