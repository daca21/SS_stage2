package myapp.com.spotifystreamer;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by  on 6/1/15.
 */
public class Utils {


//    final public void ToastText(final String s, Context mcontext){
//        Toast.makeText(mcontext, s, Toast.LENGTH_SHORT).show();
//    }

    final static public void showToastFromBackground(final String message, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
