package com.example.mybuttonview;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class LongRunningService extends Service {
    public int broadcastSpace; //记录间隔时间
    String NoticeSet;//json格式字符串
    private MediaPlayer mediaPlayer = new MediaPlayer();
    AlarmManager manager;//用于广播一个指定的Intent
    PendingIntent pi;//Intent的封装包


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                        Log.e("bai","ring");
                    }
                    break;
                case 2:
                    Vibrator vib=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(1000);
                    Log.e("bai","vibrate");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NoticeSet="notset";
        //获取系统铃声
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        //获取系统铃声的Uri
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try {
            //设置mediaPlayer
            mediaPlayer.setDataSource(getApplicationContext(),alert);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //间隔唤醒的分钟数
        int time =1;
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //获取intent传送的json格式字符串
        if(NoticeSet=="notset")
            NoticeSet=intent.getStringExtra("NoticeSet");
        else
            resolveJson(NoticeSet);
        //设置间隔时间
        broadcastSpace = time*60*1000;
        Log.e("bai","broadcastSpace:"+ broadcastSpace);
        //elapsedRealTime()返回的是系统从启动到现在的时间
        long triggerAtTime = SystemClock.elapsedRealtime()+(broadcastSpace);
        Intent i = new Intent(this,AlarmReceiver.class);
        //第4个参数flag为0代表改intent不带参数
        pi = PendingIntent.getBroadcast(this,0,i,0);
        //该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //解析json格式字符串并处理每一个提醒任务
    private void resolveJson(String ns)  {
        String name,pernum,statime,spatime,pertime,notimeth,notrep,setime;
        try{
            JSONArray jsonArray = new JSONArray(ns);
            for(int i=0;i<jsonArray.length();i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    name=object.getString("name");
                    pernum=object.getString("pernum");
                    statime=object.getString("statime");
                    spatime=object.getString("spatime");
                    pertime=object.getString("pertime");
                    notimeth=object.getString("notimeth");
                    notrep=object.getString("notrep");
                    setime=object.getString("setime");
                    if(setime.equals("notset"))
                        DayTask(name,pernum,statime,spatime,pertime,notimeth,notrep);
                    else
                        WeekTask(name,pernum, statime,spatime,pertime,notimeth, notrep,setime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //对单个每天的提醒任务处理
    private void DayTask(String name, String pernum, String statime, String spatime, String pertime, final String notimeth, String notrep){
        int hour,minute,spacetime,timeofday;
        final int noticerepeate;
        Calendar calendar = Calendar.getInstance();
        hour=Integer.parseInt(statime.split(":")[0]);
        minute=Integer.parseInt(statime.split(":")[1]);
        spacetime=Integer.parseInt(spatime);
        timeofday=Integer.parseInt(pertime.substring(2,3));
        noticerepeate=Integer.parseInt(notrep);
        Log.e("bai","hour:"+ hour+"minute:"+ minute+"spacetime:"+ spacetime
                +"timeofday:"+ timeofday+"executed at " + new Date().toString());
        for(int i=0;i<timeofday;i++){
            if(calendar.get(Calendar.HOUR_OF_DAY)==(hour+i*spacetime)
                    &&calendar.get(Calendar.MINUTE)==minute){
                    Log.e("bai","righttime");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int j=0;j<noticerepeate;j++) {
                                if (notimeth.equals("铃声"))
                                    mHandler.sendEmptyMessage(1);
                                else
                                    mHandler.sendEmptyMessage(2);
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.pause();
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                NotificationUtils notificationUtils = new NotificationUtils(this);
                notificationUtils.sendNotification("服药提醒", "服用药物"+name+pernum+"后请标记为已服用");
                Log.e("notice", "sendNotification: " );
            }
        }
    }

    //对单个每周的提醒任务处理
    private void WeekTask(String name,String pernum,String statime,String spatime,String pertime,final String notimeth, String notrep,String setime){
        int hour,minute,spacetime,timeofweek,dayofweek;
        final int noticerepeate;
        Calendar calendar = Calendar.getInstance();
        hour=Integer.parseInt(statime.split(":")[0]);
        minute=Integer.parseInt(statime.split(":")[1]);
        spacetime=Integer.parseInt(spatime);
        timeofweek=Integer.parseInt(pertime.substring(2,3));
        noticerepeate=Integer.parseInt(notrep);
        dayofweek=Integer.parseInt(setime);
        Log.e("bai","hour:"+ hour+"minute:"+ minute+"spacetime:"+ spacetime
                +"timeofweek:"+ timeofweek+"dayodweek:"+ timeofweek);
        for(int i=0;i<timeofweek;i++){
            if(calendar.get(Calendar.HOUR_OF_DAY)==hour
                    &&calendar.get(Calendar.MINUTE)==minute
                    &&(calendar.get(Calendar.DAY_OF_WEEK)-dayofweek)%7==(i*spacetime)){
                Log.e("bai","righttime");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int j=0;j<noticerepeate;j++) {
                            if (notimeth.equals("铃声"))
                                mHandler.sendEmptyMessage(1);
                            else
                                mHandler.sendEmptyMessage(2);
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.pause();
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                NotificationUtils notificationUtils = new NotificationUtils(this);
                notificationUtils.sendNotification("服药提醒", "服用药物"+name+pernum+"后请标记为已服用");
                Log.e("notice", "sendNotification: " );
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        manager.cancel(pi);
    }
}

