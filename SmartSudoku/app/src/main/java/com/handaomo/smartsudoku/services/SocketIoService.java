package com.handaomo.smartsudoku.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.handaomo.smartsudoku.Config;
import com.handaomo.smartsudoku.api_services.GamePreferences;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketIoService extends IntentService {
    private final String TAG = "SocketIOService";
    static Socket mSocket;
    static Context mContext;

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Log.e(TAG, "On Disconnect");
                }
            };
            thread.start();
        }
    };

    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Log.e("SOCKETSS", "On Reconnect");
                    GridNotificationService.start(mContext);
                    checkGridUpdates();
                    Log.i("SOCKETSS","success");
                }
            };
            thread.start();
        }
    };

    public SocketIoService() {
        super("SocketIoService");
    }

    private static void checkGridUpdates() {
        String oldDate = GamePreferences.getInstance().getLastUpdateDate(mContext);

        // save last update date
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        df.setTimeZone(tz);
        String newDate = df.format(new Date());
        GamePreferences.getInstance().setLastUpdateDate(mContext, newDate);

        mSocket.emit(Config.EMIT_SOCKET_CHECK_UPDATES, oldDate);
    }


    public static void start(Context context) {
        mContext = context;
        Intent intent = new Intent(mContext, SocketIoService.class);
        IO.Options opts = new IO.Options();
        try {
            mSocket = IO.socket(Config.baseURL, opts);
            GridNotificationService.start(mContext);
            checkGridUpdates();
            Log.i("SOCKETSS","success");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.i("SOCKETSS","failed");
        }
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, SocketIoService.class);
        context.stopService(intent);
        mSocket.off(Socket.EVENT_DISCONNECT);
        mSocket.off(Socket.EVENT_RECONNECT);
        mSocket.off();
        mSocket.disconnect();
        mSocket.close();
        mSocket = null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSocket.connect();
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroyed socketio service");
    }
}
