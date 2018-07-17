package com.example.mybuttonview;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;


public class MonitorFragmentHome extends Fragment {
    FloatingActionButton addbtn;
    RecyclerView recyclerView;
    String monitornc;
    SharedPreferences monitorSP;
    ArrayList<MonitorHomeItem> list = new ArrayList<MonitorHomeItem>();
    MonitorHomeItemAdapter adapter;
    Boolean isupdate = true;
    //信号量
    Semaphore semaphore;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.monitor_home_fagment, container, false);
        addbtn = (FloatingActionButton)view.findViewById(R.id.fab_add);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_patientlist);

        //获取监管人昵称
        monitorSP = getActivity().getSharedPreferences("Monitor",0);
        monitornc = monitorSP.getString("mnc","未获取");
        Log.i("MonitorFragmentHome", monitornc + "------------------");

        getPatientList();
        adapter = new MonitorHomeItemAdapter(list, getContext());
        adapter.setOnItemClickListener(new MonitorHomeItemAdapter.OnItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int pos) {
                Intent intent = new Intent(getContext(), MonitorPatientDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("pnc",list.get(pos).getPnc());
                bundle.putCharSequence("tel",list.get(pos).getTel());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongOnClick(View view, int pos) {
                showPopMenu(view, pos);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //为添加按钮设置监听事件
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View addpatient = LayoutInflater.from(getContext()).inflate(R.layout.monitor_add_dialog, null);
                AlertDialog.Builder addpatientDialog = new AlertDialog.Builder(getActivity());
                addpatientDialog.setTitle("新添病人")
                        .setView(addpatient)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView pnc = (TextView)addpatient.findViewById(R.id.edit_pnc);
                                String patientnc = pnc.getText().toString();
                                Log.i("addpatient", patientnc+"---------------");
                                addPatient(patientnc);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog addDialog = addpatientDialog.create();
                addDialog.show();
            }
        });
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        isupdate = true;
//        semaphore = new Semaphore(0);
        startUpdate();
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                adapter.notifyDataSetChanged();
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
                        getPatientDetail();
                        Thread.sleep(600);
                        msg.what = 1;
                        msg.sendToTarget();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void addPatient(final String pnc){
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://www.dengrong.xin:3001/login/isPatientExist",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            int success = jsonArray.getJSONObject(0).getInt("success");
                            Log.i("MonitorFragmentHome_isPatientExist", String.valueOf(success)+"---------------------------");
                            if(success == 1) {
                                StringRequest strReq2 = new StringRequest(Request.Method.POST, "http://www.dengrong.xin:3001/monitor/updateM4P",
                                        new Response.Listener<String>(){
                                            @Override
                                            public void onResponse(String response){
                                                try{
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    int affectedRows = jsonObject.getInt("affectedRows");
                                                    if(affectedRows == 1) {
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                                        alertDialogBuilder.setTitle("提示")
                                                                .setMessage("添加成功!")
                                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.cancel();
                                                                        getPatientList();
                                                                    }
                                                                });
                                                        AlertDialog alterDialog = alertDialogBuilder.create();
                                                        alterDialog.show();
                                                    }
                                                    else{
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                                        alertDialogBuilder.setTitle("提示")
                                                                .setMessage("添加失败!")
                                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.cancel();
                                                                    }
                                                                });
                                                        AlertDialog alterDialog = alertDialogBuilder.create();
                                                        alterDialog.show();
                                                    }
                                                }catch (JSONException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        Log.i("Login_Error", error.getMessage()+"-----------------------");
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() {
                                        // Posting parameters to login url
                                        Map<String, String> params2 = new HashMap<String, String>();
                                        params2.put("mnc", monitornc);
                                        params2.put("pnc", pnc);
                                        return params2;
                                    }
                                };
                                AppController.getInstance().addToRequestQueue(strReq2);
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("该病人不存在!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("Login_Error", error.getMessage()+"-----------------------");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nc", pnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void getPatientList(){
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://www.dengrong.xin:3001/monitor/listPatientsByMonitor",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            Log.i("getPatientList", response+"------------------------");
                            list.clear();
                            for(int i=0;i<jsonArray.length();i++){
                                MonitorHomeItem item = new MonitorHomeItem();
                                item.setPnc(jsonArray.getJSONObject(i).getString("pnc"));
                                Log.i("MonitorFragmentHome_getPatientList", jsonArray.getJSONObject(i).getString("pnc")+"---------------------");
                                item.setPname(jsonArray.getJSONObject(i).getString("xm"));
                                item.setTel(jsonArray.getJSONObject(i).getString("tel"));
                                item.setIscheck("y");
                                list.add(item);
                            }
//                            getPatientDetail();
                            adapter.notifyDataSetChanged();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("Login_Error", error.getMessage()+"-----------------------");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mnc", monitornc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void getPatientDetail(){
        for(int i=0;i<list.size();i++){
            try {
                URL url = new URL("http://www.dengrong.xin:3001/patient/listByPatient");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                String data = "pnc="+list.get(i).getPnc();

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
                    int j;
                    for(j=0;j<jsonArray.length();j++){
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        if(jsonObject.getString("ischeck").equals("n")){
                            list.get(i).setIscheck("n");
                            break;
                        }
                    }
                    if(j==jsonArray.length()){
                        list.get(i).setIscheck("y");
                    }
                    //对list操作完成，释放锁
//                    semaphore.release();
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    public void showPopMenu(View view,final int pos){
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_longclicked, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("提示")
                        .setMessage("确认删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePatient(list.get(pos).getPnc());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alterDialog = alertDialogBuilder.create();
                alterDialog.show();
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener(){
            @Override
            public void onDismiss(PopupMenu menu){
            }
        });
        popupMenu.show();
    }
    public void deletePatient(final String pnc){
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://www.dengrong.xin:3001/monitor/updateM4P",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int affectedRows = jsonObject.getInt("affectedRows");
                            if(affectedRows == 1) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("删除成功!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                getPatientList();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("删除失败!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("Login_Error", error.getMessage()+"-----------------------");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mnc", "default");
                params.put("pnc", pnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
    @Override
    public void onPause(){
        super.onPause();
        isupdate = false;
//        semaphore.release();
    }
}
