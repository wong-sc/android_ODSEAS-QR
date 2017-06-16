package app.app.app.odseasqr;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String ACTION_SYNC_FILE = "com.example.android.wifidirect.SYNC";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String COURSE_CODE = "course_id";
    SyncBroadCast syncBroadCast;
    IntentFilter intentFilter;
    SharedPreferences preferences;

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        Log.d(SyncActivity.TAG, intent.getAction());
        syncBroadCast = new SyncBroadCast();
        intentFilter = new IntentFilter("com.odseasqr.android.SYNC");
        registerReceiver(syncBroadCast, intentFilter);
        String result = intent.getExtras().getString(EXTRAS_FILE_PATH);
        String course_code = intent.getExtras().getString(COURSE_CODE);
        String result_key = course_code + "@"  + result;
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
        Socket socket;
        DataOutputStream stream = null;
        DataInputStream inputStream = null;

        switch (intent.getAction()) {

            case ACTION_SEND_FILE:


                Log.d(SyncActivity.TAG, "Addr: " + host);
                socket = new Socket();

                Log.d(SyncActivity.TAG, result_key);

                if (result_key != null) {

                    try {
                        Log.d(SyncActivity.TAG, "Opening client socket - ");
                        socket.bind(null);
                        socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                        Log.d(SyncActivity.TAG, "Client socket - " + socket.isConnected());
                        stream = new DataOutputStream(socket.getOutputStream());
                        byte[] info = result_key.getBytes("UTF-8");
                        stream.writeInt(info.length);
                        stream.write(info);

                        inputStream = new DataInputStream(socket.getInputStream());
                        int lengths = inputStream.readInt();
                        byte[] data=new byte[lengths];
                        inputStream.readFully(data);
                        String str=new String(data,"UTF-8");
                        Log.d(SyncActivity.TAG, "Message from server: " + str);

                        String[] code = str.split("@", 2);
                        Log.d("course_id", code[0]);
                        Log.d("result", code[1]);

                        if(str.length() != 0){
                            Intent intents = new Intent("com.odseasqr.android.SYNC");
                            intents.putExtra("status", "Sync");
                            intents.putExtra("result", code[1]);
                            sendBroadcast(intents);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (socket != null) {
                            if (socket.isConnected()) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;

            case ACTION_SYNC_FILE:

                Log.d(SyncActivity.TAG, "Addr: " + host);
                socket = new Socket();
                Log.d(SyncActivity.TAG, result);

                if (result != null) {

                    try {
                        Log.d(SyncActivity.TAG, "Opening client socket - ");
                        socket.bind(null);
                        socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                        Log.d(SyncActivity.TAG, "Client socket - " + socket.isConnected());
                        Log.d(SyncActivity.TAG, "Client data - " + result);
                        stream = new DataOutputStream(socket.getOutputStream());
                        stream.writeUTF(result);
                        stream.flush();

                        inputStream = new DataInputStream(socket.getInputStream());
                        int lengths = inputStream.readInt();
                        byte[] data=new byte[lengths];
                        inputStream.readFully(data);
                        String str=new String(data,"UTF-8");
                        Log.d(SyncActivity.TAG, "Message from server: " + str);

                        if(str.equals("true")){
                            Intent intents = new Intent("com.odseasqr.android.SYNC");
                            intents.putExtra("status", "Validated");
                            sendBroadcast(intents);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (socket != null) {
                            if (socket.isConnected()) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

    @Override
    public void onDestroy() {
        unregisterReceiver(syncBroadCast);
        super.onDestroy();
    }
}
