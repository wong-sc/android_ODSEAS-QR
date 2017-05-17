package app.app.app.odseasqr;

import android.app.Application;

public class odseasqr extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    SyncActivity syncActivity;
    DeviceDetailFragment deviceDetailFragment;

}
