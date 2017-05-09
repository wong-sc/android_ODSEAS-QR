package app.app.app.odseasqr;

import android.app.Application;

/**
 * Created by Shi Chee on 08-May-17.
 */

public class odseasqr extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    SyncActivity syncActivity;
    DeviceDetailFragment deviceDetailFragment;

}
