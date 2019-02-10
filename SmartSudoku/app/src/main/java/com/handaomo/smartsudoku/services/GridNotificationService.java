package com.handaomo.smartsudoku.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.handaomo.smartsudoku.Config;
import com.handaomo.smartsudoku.api_services.GamePreferences;
import com.handaomo.smartsudoku.dtos.GridDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.socket.emitter.Emitter;

import static com.handaomo.smartsudoku.services.SocketIoService.mSocket;

public class GridNotificationService extends IntentService {
    private static Context mContext;
    private final String TAG = "GridNotificationService";
    private static GridNotificationHelper gridNotificationHelper;

    private Emitter.Listener onNewGrid = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    GridDto grid = gson.fromJson((String) args[0], GridDto.class);
                    gridNotificationHelper.createNotification(grid.configuration.replaceAll("[u']",""));

                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // save last update date
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                    df.setTimeZone(tz);
                    String newDate = df.format(new Date());
                    GamePreferences.getInstance().setLastUpdateDate(mContext, newDate);
                }
            };
            thread.start();
        }
    };

    private Emitter.Listener onNewGridUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    GridDto grid = gson.fromJson((String) args[0], GridDto.class);
                    gridNotificationHelper.createNotification(grid.configuration.replaceAll("[u']",""));
                }
            };
            thread.start();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroyed grid notification service");
    }

    public GridNotificationService() {
        super("GridNotificationService");
    }

    public static void start(Context context) {
        mContext = context;
        Intent intent = new Intent(context, GridNotificationService.class);
        context.startService(intent);
        gridNotificationHelper = new GridNotificationHelper(context);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, GridNotificationService.class);
        context.stopService(intent);

        mSocket.off(Config.EVENT_NEW_GRID);
        mSocket.off(Config.EVENT_NEW_GRID_UPDATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSocket.on(Config.EVENT_NEW_GRID, onNewGrid);
            mSocket.on(Config.EVENT_NEW_GRID_UPDATE, onNewGridUpdate);
        }
    }
}