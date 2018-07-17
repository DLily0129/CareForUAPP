package com.example.mybuttonview;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by de on 2018/2/13.
 */

public class PatientFragmentMyInfo extends Fragment{
    private SharedPreferences patientSP ;
    TextView patientName;
    EditText patientTel;
    Button staService, stoService,rewTel,logot;
    String infourl = "http://www.dengrong.xin:3001/patient/showOnePatient";
    String choiceurl = "http://www.dengrong.xin:3001/patient/listByPatient";
    String rewriteurl="http://www.dengrong.xin:3001/patient/rewriteTel";
    String name,tel,pnc;
    String NoticeSet="notset";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.patient_my_info, container, false);
        logot = view.findViewById(R.id.btn_logout);
        logot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("提示")
                        .setMessage("确认退出？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                LoginActivity.patientLogin=false;
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
        showPatientNaT();
        rewriteTel();
        getService();
    }

    //显示用户名及电话号码
    public void showPatientNaT(){
        patientSP=this.getActivity().getSharedPreferences("Patient",0);
        patientName=(TextView) getView().findViewById(R.id.patientName);
        patientTel=(EditText)getView().findViewById(R.id.patientTel);
        pnc=patientSP.getString("pnc","未获取");
        if(!pnc.equals("未获取")) {
           getPatientNaT();
        }
    }

    //获取web数据库中对应病人名称及电话号码
    public void getPatientNaT(){
        StringRequest strReq = new StringRequest(Request.Method.POST, infourl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            name=jsonArray.getJSONObject(0).getString("xm");
                            tel=jsonArray.getJSONObject(0).getString("tel");
                            patientName.setText(name);
                            patientTel.setText(tel);
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
                params.put("pnc",pnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //初始化更改电话信息按钮
    private void rewriteTel() {
        rewTel=getActivity().findViewById(R.id.rewriteTel);
        rewTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel=""+patientTel.getText();
                rewriteTnum();
            }
        });
    }

    //更改电话信息
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
                params.put("pnc",pnc);
                params.put("tel",tel);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //将提醒服务开启与关闭与按钮点击绑定
    private void getService() {
        staService=(Button)getActivity().findViewById(R.id.startService);
        stoService =(Button)getActivity().findViewById(R.id.stopService);
        staService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(),LongRunningService.class);
                if(!NoticeSet.equals("notset")) {
                    //通过Intent将时间间隔传递给Service
                    startIntent.putExtra("NoticeSet", NoticeSet);
                    Toast.makeText(getActivity(), "开始提醒", Toast.LENGTH_SHORT).show();
                    getActivity().startService(startIntent);
                }else{
                    Toast.makeText(getActivity(), "未获取提醒数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
        stoService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(getActivity(),LongRunningService.class);
                Toast.makeText(getActivity(),"结束提醒",Toast.LENGTH_SHORT).show();
                getActivity().stopService(stopIntent);
            }
        });
        getChoice();
    }

    //获取choice设置
    private void getChoice(){
        StringRequest strReq = new StringRequest(Request.Method.POST, choiceurl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            NoticeSet=response;
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
                params.put("pnc",pnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
