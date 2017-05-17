package app.app.app.odseasqr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Switch;

public class SyncBroadCast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        DeviceDetailFragment deviceDetailFragment = ((odseasqr) context.getApplicationContext()).deviceDetailFragment;

        switch (intent.getStringExtra("status")){

            case "Finished":
                deviceDetailFragment.Finished();
                break;

            case "Start":
                deviceDetailFragment.start();
                break;

            case "Sync":
                deviceDetailFragment.renewDatabase(intent.getStringExtra("result"));
                break;

            case "Validated":
                deviceDetailFragment.successValidated();
                break;

            default:
                break;
        }

    }
}
