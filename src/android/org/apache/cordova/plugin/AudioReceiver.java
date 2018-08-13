package org.apache.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class to handle audio jack.
 */
public class AudioReceiver extends BroadcastReceiver {
    public Runnable connect;
    public Runnable disconnect;

    /**
     * Receiver of audio jack connection/disconnection.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    if(disconnect!=null)
                        disconnect.run();
                    break;
                case 1:
                    if(connect!=null)
                        connect.run();
                    break;
            }
        }
    }
}