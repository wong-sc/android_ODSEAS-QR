package app.app.app.odseasqr;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.app.app.odseasqr.R;

public class SyncActivity extends AppCompatActivity implements DeviceListsFragment.DeviceActionListener, WifiP2pManager.ChannelListener, View.OnClickListener {

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    Button search;
    SharedPreferences preferences;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinate);

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(this);
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadCastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onChannelDisconnected() {

        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void resetData(){
        DeviceListsFragment fragmentList = (DeviceListsFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.search:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(SyncActivity.this, "Enable P2P from action bar button above or system settings",
                            Toast.LENGTH_SHORT).show();
                    WifiManager wManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wManager.setWifiEnabled(true);
                }

                final DeviceListsFragment fragment = (DeviceListsFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_list);
                fragment.onInitiateDiscovery();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Initializing discovery.....", Snackbar.LENGTH_LONG);
                snackbar.show();
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {

                        Toast.makeText(SyncActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i) {

                        Toast.makeText(SyncActivity.this, "Discovery Failed",
                                Toast.LENGTH_SHORT).show();

                    }
                });
                break;
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Select Connect Button", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListsFragment fragment = (DeviceListsFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(SyncActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(SyncActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {

            config.groupOwnerIntent = 15;

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Opening Socket Server", Snackbar.LENGTH_LONG);
            snackbar.show();

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    search.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Connected Successful", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(SyncActivity.this, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
 }

 @Override
 public void receiveconnection(){
     search.setVisibility(View.GONE);
     Snackbar snackbar = Snackbar
             .make(coordinatorLayout, "Connection Received", Snackbar.LENGTH_LONG);
     snackbar.show();
 }

    @Override
    public void disconnect() {

        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {

                fragment.getView().setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Disconnected", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        });
   }
}
