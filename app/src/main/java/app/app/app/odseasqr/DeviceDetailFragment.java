package app.app.app.odseasqr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.genasync12.MainActivity;

import app.app.app.odseasqr.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Timer;

public class DeviceDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    OfflineDatabase mydb;
    ArrayList<JSONObject> courseData = new ArrayList<>();
    SharedPreferences preferences;
    ProgressDialog loading;
    ServerSocket serverSocket;
    Socket mSocket;
    String clientIP;
    int clientPort;
    public Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        odseasqr od_seas = (odseasqr) getActivity().getApplicationContext();
        od_seas.deviceDetailFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mydb = new OfflineDatabase(getActivity());
        loading = new ProgressDialog(getActivity());
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(true);
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (preferences.getString(Dashboard.POSITION, "null").equals(Config.CHIEF)) {
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
                } else {
                    showMessage("Action Restricted", "Only Chief Invigilator can select a device to connect");
                }
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
                        loading.setMessage("Validating course information...");
                        loading.show();
//                        syncClient();
                        getUnsyncData();
                    }
                });

        preferences = getActivity().getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        return mContentView;
    }

    public void successValidated(){
        loading.setMessage("Getting Unsynced Data...");
        getUnsyncData();
    }

    /* search in the database to get unsynced data (where status = 0), status = 0, mean that the data
    * has been modified by invigilator, by default, the status is 1 */
    private void getUnsyncData() {
        Cursor cursor = mydb.getUnsyscData(preferences.getString(Config.COURSE_ID, "null"));
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

    public void getSyncedResult() {
        Cursor cursor = mydb.getAttendance(preferences.getString(Config.COURSE_ID, "null"));
        courseData.clear();
        Log.d(SyncActivity.TAG, DatabaseUtils.dumpCursorToString(cursor));
        if(cursor == null) {
            Toast.makeText(getActivity(), "All data up to date, No Sync Require", Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
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
//        Toast.makeText(getActivity(), courseData.size()+"", Toast.LENGTH_SHORT).show();
//        new ServerAsyncTask(getActivity()).execute();
//        syncClient();
    }

    public void syncClient(){
        Log.d(SyncActivity.TAG, "Enter send to chief " + info.groupOwnerAddress.getHostAddress());
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SYNC_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, preferences.getString(Config.COURSE_ID, "null"));
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    private void sendToChief(String result){
        loading.setMessage("Sending to chief...");
        Log.d(SyncActivity.TAG, "Enter send to chief " + info.groupOwnerAddress.getHostAddress());
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, result);
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.COURSE_CODE, preferences.getString(Config.COURSE_ID, "null"));
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    public void sync(Context context){
        if(preferences.getString(Dashboard.POSITION, "null").equals(Config.CHIEF)) {
                Intent startsync = new Intent(context, SyncService.class);
                context.startService(startsync);
        }
    }

    public void Finished(){
        Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        Log.d(SyncActivity.TAG, info.toString());

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
        ((DeviceListsFragment.DeviceActionListener) getActivity()).receiveconnection();
    }

    public void start(){
//        Toast.makeText(getActivity(), "Start", Toast.LENGTH_LONG).show();
        if(info.isGroupOwner){
            getSyncedResult();
        }
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        Log.d(SyncActivity.TAG, "device: " + device.toString());
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    public void renewDatabase(String result){
        String status = mydb.localSync(result);
        Toast.makeText(getActivity(), "Success" + status, Toast.LENGTH_SHORT).show();
        loading.dismiss();
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;
        OfflineDatabase mydb;
        ProgressDialog loading2;

        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
            mydb = new OfflineDatabase(context);
            loading2 = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(Void... params) {

            ServerSocket serverSocket = null;
            Socket client = null;
            DataInputStream inputstream = null;
            DataOutputStream outputStream = null;
            Handler h = new Handler(Looper.getMainLooper());

                try {
                    serverSocket = new ServerSocket(8988);
                    client = serverSocket.accept();

                    clientIP = client.getInetAddress().toString().replace("/","");
                    clientPort = client.getPort();
                    Log.d("clientIP2: ", client.getRemoteSocketAddress().toString());
                    Log.d("clientIP3: ", ""+client.getPort());
                    Log.d("clientIP", clientIP + "");
                    inputstream = new DataInputStream(client.getInputStream());

                    int lengths = inputstream.readInt();
                    byte[] input = new byte[lengths];
                    inputstream.readFully(input);
                    String str=new String(input,"UTF-8");
                    Log.d(SyncActivity.TAG, "Message from server: " + str);
                    final String[] key_code = str.split("@", 2);
                    Log.d(SyncActivity.TAG, "Key Code: " + key_code[0]);
                    Log.d(SyncActivity.TAG, "Key Code: " + key_code[1]);

                    h.post(new Runnable() {
                        public void run() {
                            loading2.setMessage("Receiving record...");
                            loading2.show();
                        }
                    });

                    if(preferences.getString(Config.COURSE_ID, "null").equals(key_code[0])) {
                        if (mydb.check_course_status(key_code[0])){
                            String status = mydb.insertDataFrom_(key_code[1]);
                                Log.d("item", status);
                                if (status.equals("fail")) {

                                    h.post(new Runnable() {
                                        public void run() {
                                            showMessage("Wrong Course", "Please select the same course with CHIEF Invigilator");
                                        }
                                    });

                                    } else if (str.length() != 0) {

                                    h.post(new Runnable() {
                                        public void run() {
//                                            loading2.setMessage("Receiving record...");
//                                            loading2.show();
                                            sync(context);
                                        }
                                    });

//                                    h.post(new Runnable() {
//                                        public void run() {
//
//                                        }
//                                    });

                                    try {
                                        Thread.sleep(8000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    getSyncedResult();
                                    outputStream = new DataOutputStream(client.getOutputStream());
                                    String key_result = key_code[0] + "@" + courseData.toString();
                                    byte[] data = key_result.getBytes("UTF-8");
                                    outputStream.writeInt(data.length);
                                    outputStream.write(data);
                                    h.post(new Runnable() {
                                        public void run() {
                                            loading.dismiss();
                                        }
                                     });

                                    } else {

                                        h.post(new Runnable() {
                                            public void run() {
                                                showMessage("Message Empty", "No data from client");
                                            }
                                        });

                                    }
                                } else {
                                    h.post(new Runnable() {
                                        public void run() {
                                            showMessage("Cannot Sync", "This course: " + key_code[0] + "has been closed by CHIEF INVIGILATOR");
                                        }
                                    });
                                 }
                    } else {

                        h.post(new Runnable() {
                            public void run() {
                                showMessage("Course Mismatch: "+ key_code[0], "Sync with wrong CHIEF: " + preferences.getString(Config.COURSE_ID, "null"));
                            }
                        });
                    }
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
                loading2.dismiss();
                if(loading.isShowing())
                    loading.dismiss();
            }
        }
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder Adialog = new AlertDialog.Builder(getActivity());
        Adialog.setTitle(title);
        Adialog.setMessage(message).setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        Adialog.show();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }
}
