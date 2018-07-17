package com.example.mybuttonview;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class MonitorPatientDetailActivity extends AppCompatActivity {
    final static int REQUESTCODE=1234;
    String pnc;
    String tel;
    Boolean isupdate = true;
    ArrayList<MonitorSicknessItem> list = new ArrayList<>(),temp;
    MonitorSicknessAdapter adapter;
    ListView listView;
    Button button;
    View sicknessdetailheader;
    //信号量
    Semaphore semaphore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_patient_deatil_activity);
        Bundle bundle = getIntent().getExtras();
        pnc = bundle.getString("pnc");
        setTitle(pnc);
        sicknessdetailheader = LayoutInflater.from(this).inflate(R.layout.monitor_sickness_title, null);
        listView = (ListView)findViewById(R.id.lv_sicknessdetail);
        button = (Button)findViewById(R.id.button_callhim);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MonitorPatientDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUESTCODE);

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tel));
                try {
                    startActivity(intent);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }
        });
        adapter = new MonitorSicknessAdapter(list,this);
        listView.addHeaderView(sicknessdetailheader);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        pnc = bundle.getString("pnc");
        tel = bundle.getString("tel");
        Log.i("2222222222", pnc+"----------------------");
        isupdate = true;
        semaphore = new Semaphore(0);
        startUpdate();
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                //如果锁的个数为0，则阻塞当前(主)线程
                try{
                    semaphore.acquire();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(temp.size() != 0){
                    list.clear();
                    list.addAll(temp);
                    adapter.notifyDataSetChanged();
                }

            }
            super.handleMessage(msg);
        }
    };
    private void startUpdate(){
        new Thread(new Runnable() {
            public void run() {
                while (isupdate){
                    try{
                        Message msg = mHandler.obtainMessage();
                        getSicknessDetail();
                        Thread.sleep(100);
                        msg.what = 1;
                        msg.sendToTarget();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void getSicknessDetail(){
        try {
            URL url = new URL("http://www.dengrong.xin:3001/patient/listByPatient");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            String data = "pnc="+pnc;

            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", data.length()+"");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                InputStream input = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String str = null;
                StringBuffer stringBuffer = new StringBuffer();
                while ((str = reader.readLine()) != null){
                    stringBuffer.append(str).append("\n");
                }
                Log.i("33333333333", data + " "+stringBuffer.toString()+"---------------------");
                JSONArray jsonArray = new JSONArray(stringBuffer.toString());
//                list.clear();
                temp = new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    MonitorSicknessItem item = new MonitorSicknessItem();
                    item.setPillname(jsonObject.getString("name"));
                    item.setStarttime(jsonObject.getString("statime"));
                    item.setPertime(jsonObject.getString("pertime"));
                    item.setPernum(jsonObject.getString("pernum"));
                    item.setIscheck(jsonObject.getString("ischeck"));
                    temp.add(item);
                }
                //对list操作完成，释放锁
                semaphore.release();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        isupdate = false;
        semaphore.release();
    }
}
