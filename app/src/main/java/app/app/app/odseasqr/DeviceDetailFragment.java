package com.example.pethoalpar.odseasqr;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DeviceDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    OfflineDatabase mydb;
    ArrayList<JSONObject> courseData = new ArrayList<>();
    SharedPreferences preferences;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mydb = new OfflineDatabase(getActivity());
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
                );
                ((DeviceListsFragment.DeviceActionListener) getActivity()).connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListsFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.d(SyncActivity.TAG, "Enter onclick");
                        getUnsyncData();
                    }
                });

        preferences = getActivity().getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        return mContentView;
    }

    /* search in the database to get unsynced data (where status = 0), status = 0, mean that the data
    * has been modified by invigilator, by default, the status is 1 */
    private void getUnsyncData() {
        Cursor cursor = mydb.getUnsyscData();
        Log.d(SyncActivity.TAG, DatabaseUtils.dumpCursorToString(cursor));
        if(cursor == null)
            Toast.makeText(getActivity(), "All data up to date, No Sync Require", Toast.LENGTH_SHORT).show();
        else if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    /*After retrieved from the database, result taken will be stored as JSONObject*/
                    jsonObject.put("enroll_handler_id", cursor.getString(0));
                    jsonObject.put("student_id", cursor.getString(1));
                    jsonObject.put("course_id", cursor.getString(2));
                    jsonObject.put("ischecked", cursor.getString(3));
                    jsonObject.put("checkin_time", cursor.getString(4));
                    jsonObject.put("checkout_time", cursor.getString(5));
                    jsonObject.put("checkin_staffID", cursor.getInt(6));
                    jsonObject.put("checkout_staffID", cursor.getInt(7));
                    jsonObject.put("checkin_style_id", cursor.getString(8));
                    jsonObject.put("checkout_style_id", cursor.getString(9));
                    jsonObject.put("status", cursor.getString(10));
                    jsonObject.put("created_date", cursor.getString(11));
                    jsonObject.put("updated_date", cursor.getString(12));

                    courseData.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        Toast.makeText(getActivity(), courseData.size()+"", Toast.LENGTH_SHORT).show();
        sendToChief(courseData.toString());
    }

    public void sync(){

        if(preferences.getString(Config.WIFI_STATUS, "").equals("Wifi enabled") ||
                preferences.getString(Config.WIFI_STATUS, "").equals("Mobile data enabled")){
            Intent startsync = new Intent(getActivity(), SyncService.class);
            getActivity().startService(startsync);
        }
    }

    private void sendToChief(String result){
        Log.d(SyncActivity.TAG, "Enter send to chief");
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, result);
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        // User has picked an image. Transfer it to group owner i.e peer using
//        // FileTransferService.
//        Uri uri = data.getData();
//        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
//        statusText.setText("Sending: " + uri);
//        Log.d(SyncActivity.TAG, "Intent----------- " + uri);
//        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
//        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
//        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
//        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//                info.groupOwnerAddress.getHostAddress());
//        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
//        getActivity().startService(serviceIntent);
//    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        String owner = getResources().getString(R.string.group_owner_text) + ((info.isGroupOwner) ? "Yes" : "No");
        view.setText(owner);

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        String group_owner_ip = "Group Owner IP - " + info.groupOwnerAddress.getHostAddress();
        view.setText(group_owner_ip);

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }
        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText("");
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("");
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText("");
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText("");
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;
        OfflineDatabase mydb;

        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
            mydb = new OfflineDatabase(context);
        }

        @Override
        protected String doInBackground(Void... params) {

            ServerSocket serverSocket = null;
            Socket client = null;
            DataInputStream inputstream = null;

            try {
                serverSocket = new ServerSocket(8988);
                client = serverSocket.accept();
                inputstream = new DataInputStream(client.getInputStream());
                String str = inputstream.readUTF();
                serverSocket.close();
                return str;
            } catch (IOException e) {
                Log.e(SyncActivity.TAG, e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(SyncActivity.TAG, "Result: "+ result);
                String status = mydb.insertDataFrom_(result);
                if(status.equals("success update")){
                    Toast.makeText(context.getApplicationContext(), "Successfully update data", Toast.LENGTH_SHORT).show();
                    sync();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Error occur", Toast.LENGTH_SHORT).show();
                }
            }

        }
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();

        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);

        } catch (IOException e) {
            Log.d(SyncActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}