package app.app.app.odseasqr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


public class NetworkStatus extends BroadcastReceiver {

    Context context;
    OfflineDatabase mydb;
    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtils.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        preferences = context.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        String position = preferences.getString(Dashboard.POSITION, "null");

        this.context = context;
        mydb = new OfflineDatabase(context);

        /*IF CONNECTION ENABLE, SYNC WITH SERVER and whether the user == CHIEF*/
        if(position.equals(Config.CHIEF)){
            if(status.equals("Wifi enabled") || status.equals("Mobile data enabled")){
                Intent startsync = new Intent(context, SyncService.class);
                startsync.putExtra(Config.WIFI_STATUS, status);
                context.startService(startsync);
            }
        }
    }
}
