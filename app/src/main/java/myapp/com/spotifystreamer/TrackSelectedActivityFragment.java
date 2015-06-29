package myapp.com.spotifystreamer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myapp.com.spotifystreamer.service.myPlayService;



/**
 * A placeholder fragment containing a simple view.
 */
public class TrackSelectedActivityFragment extends Fragment  implements OnSeekBarChangeListener{

    private String LOg_TAG = TrackSelectedActivityFragment.class.getSimpleName();

    Intent serviceIntent;
//    private ImageButton buttonPlayStop;

    // -- PUT THE NAME OF YOUR AUDIO FILE HERE...URL GOES IN THE SERVICE
    String strAudioLink = "10.mp3";

    private boolean isOnline;
    private boolean boolMusicPlaying = false;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;

    // --Seekbar variables --
//    private SeekBar seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;

    // --Set up constant ID for broadcast of seekbar position--
    public static final String BROADCAST_SEEKBAR = "comyapp.com.spotifystreamer.sendseekbar";
    Intent intent;

    // Progress dialogue and broadcast receiver variables
    boolean mBufferBroadcastIsRegistered;
    private ProgressDialog pdBuff = null;




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

    @InjectView(R.id.btnPlay)
    protected ImageButton buttonPlayStop;

    @InjectView(R.id.seekBar_duration)
    protected SeekBar seekBar;

    private String data;

    public TrackSelectedActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            serviceIntent = new Intent(getActivity(), myPlayService.class);

            // --- set up seekbar intent for broadcasting new position to service ---
            intent = new Intent(BROADCAST_SEEKBAR);

//            initViews();
//            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
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

//            initViews();
            setListeners();

//            if (intent.hasExtra(Constant.DURATION_MS_KEY)){
//                String data = intent.getStringExtra(Constant.DURATION_MS_KEY);
//                duratiton_max.setText(data);
//            }
        }

        return rootView;
    }



    // -- Broadcast Receiver to update position of seekbar from service --
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        if (songEnded == 1) {
//            buttonPlayStop.setBackgroundResource(R.drawable.ic_pause_black_48dp);
        }
    }


    // --- Set up initial screen ---
    private void initViews() {
//        buttonPlayStop = (ImageButton) getView().findViewById(R.id.btnPlay);
//        buttonPlayStop.setBackgroundResource(R.drawable.ic_pause_black_48dp);

        // --Reference seekbar in main.xml
//        seekBar = (SeekBar)getView(). findViewById(R.id.seekBar_duration);
    }

    // --- Set up listeners ---
    private void setListeners() {
        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlayStopClick();
            }
        });
        seekBar.setOnSeekBarChangeListener(this);

    }

    // --- invoked from ButtonPlayStop listener above ----
    private void buttonPlayStopClick() {
        if (!boolMusicPlaying) {
            buttonPlayStop.setBackgroundResource(R.drawable.ic_pause_black_48dp);
            playAudio();
            boolMusicPlaying = true;
        } else {
            if (boolMusicPlaying) {
                buttonPlayStop.setBackgroundResource(R.drawable.ic_play_arrow_black_48dp);
                stopMyPlayService();
                boolMusicPlaying = false;
            }
        }
    }

    // --- Stop service (and music) ---
    private void stopMyPlayService() {
        // --Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            try {
                getActivity().unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {
                // Log.e(TAG, "Error in Activity", e);
                // TODO Auto-generated catch block

                e.printStackTrace();
                Toast.makeText(

                        getActivity(),

                        e.getClass().getName() + " " + e.getMessage(),

                        Toast.LENGTH_LONG).show();
            }
        }

        try {
            getActivity().stopService(serviceIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        boolMusicPlaying = false;
    }

    // --- Start service and play music ---
    private void playAudio() {

        checkConnectivity();
        if (isOnline) {
            stopMyPlayService();

            serviceIntent.putExtra("sentAudioLink", strAudioLink);

            try {
                getActivity().startService(serviceIntent);
            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(

                        getActivity(),

                        e.getClass().getName() + " " + e.getMessage(),

                        Toast.LENGTH_LONG).show();
            }

            // -- Register receiver for seekbar--
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                    myPlayService.BROADCAST_ACTION));
            ;
            mBroadcastIsRegistered = true;

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.setIcon(R.mipmap.ic_launcher);
            buttonPlayStop.setBackgroundResource(R.drawable.ic_play_arrow_black_48dp);
            alertDialog.show();
        }
    }

    // Handle progress dialogue for buffering...
    private void showPD(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);

        // When the broadcasted "buffering" value is 1, show "Buffering"
        // progress dialogue.
        // When the broadcasted "buffering" value is 0, dismiss the progress
        // dialogue.

        switch (bufferIntValue) {
            case 0:
                // Log.v(TAG, "BufferIntValue=0 RemoveBufferDialogue");
                // txtBuffer.setText("");
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;

            case 1:
                BufferDialogue();
                break;

            // Listen for "2" to reset the button to a play button
            case 2:
                buttonPlayStop.setBackgroundResource(R.drawable.ic_play_arrow_black_48dp);
                break;

        }
    }

    // Progress dialogue...
    private void BufferDialogue() {

        pdBuff = ProgressDialog.show(getActivity(), "Buffering...",
                "Acquiring song...", true);
    }

    // Set up broadcast receiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };

    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())
            isOnline = true;
        else
            isOnline = false;
    }

    // -- onPause, unregister broadcast receiver. To improve, also save screen data ---
    @Override
    public void onPause() {
        // Unregister broadcast receiver
        if (mBufferBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }
        super.onPause();
    }


    // -- onResume register broadcast receiver. To improve, retrieve saved screen data ---
    @Override
    public void onResume() {
        // Register broadcast receiver
        if (!mBufferBroadcastIsRegistered) {
            getActivity().registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    myPlayService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
        super.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int seekPos = seekBar.getProgress();
            intent.putExtra("seekpos", seekPos);
            getActivity().sendBroadcast(intent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
