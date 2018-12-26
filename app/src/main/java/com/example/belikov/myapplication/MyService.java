package com.example.belikov.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    IBinder mBinder;      // Интерфейс связи с клиентом
    private Timer timer;
    private TimerTask tTask;
    private long interval = 10000;

    private int temper;
    private float wind;
    private int humidi;
    private int press;

    @Override
    public void onCreate() {
        // Служба создается
        super.onCreate();
        timer = new Timer();
        getParams();
    }

    private void getParams() {
        if (tTask != null)tTask.cancel();


        if (interval > 0) {
            tTask = new TimerTask() {
                @Override
                public void run() {
                    //FIXME
                    Log.d("Service", "interval " + interval);
                    setParams();
                    Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                    intent.putExtra(MainActivity.TEMPER, temper);
                    intent.putExtra(MainActivity.WIND, wind);
                    intent.putExtra(MainActivity.HUMIDITY, humidi);
                    intent.putExtra(MainActivity.PRESSURE, press);

                    sendBroadcast(intent);
                }
            };
            timer.schedule(tTask, 1000, interval);
        }
    }

    private void setParams() {
        temper = (int)(Math.random()*40 -20);
        wind = ((int)(Math.random()*100))/10;
        humidi = (int)(Math.random()*100);
        press = 740 + (int)(Math.random()*40);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Служба стартовала
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Привязка клиента
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // Удаление привязки
        return true;
    }
    @Override
    public void onRebind(Intent intent) {
        // Перепривязка клиента
    }
    @Override
    public void onDestroy() {
        // Уничтожение службы
    }
}
