package com.example.mybuttonview;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MonitorFragmentMyInfo extends Fragment {
    private SharedPreferences monitorSP ;
    TextView monitorName;
    EditText monitorTel;
    Button rewTel,logout;
    String infourl = "http://www.dengrong.xin:3001/monitor/showOneMonitor";
    String rewriteurl="http://www.dengrong.xin:3001/monitor/rewriteTel";
    String name,tel,mnc;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monitor_myinfo_fragment, container, false);
        logout = view.findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("提示")
                        .setMessage("确认退出？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                LoginActivity.monitorLogin=false;
                                startActivity(intent);
                                getActivity().finish();
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
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showMonitorNaT();
        rewriteTel();
    }

    //显示用户名及电话号码
    public void showMonitorNaT(){
        monitorSP=this.getActivity().getSharedPreferences("Monitor",0);
        monitorName=(TextView) getView().findViewById(R.id.monitorName);
        monitorTel=(EditText)getView().findViewById(R.id.monitorTel);
        mnc=monitorSP.getString("mnc","未获取");
        Log.i("MonitorFragmentHome", mnc + "------------------");
        if(!mnc.equals("未获取")) {
            getMonitorNaT();
        }
    }

    public void getMonitorNaT(){
        StringRequest strReq = new StringRequest(Request.Method.POST, infourl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            name=jsonArray.getJSONObject(0).getString("xm");
                            tel=jsonArray.getJSONObject(0).getString("tel");
                            monitorName.setText(name);
                            monitorTel.setText(tel);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mnc",mnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void rewriteTel() {
        rewTel=getActivity().findViewById(R.id.rewriteTel);
        rewTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel=""+monitorTel.getText();
                rewriteTnum();
            }
        });
    }
    private void rewriteTnum(){
        StringRequest strReq = new StringRequest(Request.Method.POST, rewriteurl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int affectedRows = jsonObject.getInt("affectedRows");
                            if(affectedRows == 1){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("修改成功!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("修改失败!")
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
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mnc",mnc);
                params.put("tel",tel);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
