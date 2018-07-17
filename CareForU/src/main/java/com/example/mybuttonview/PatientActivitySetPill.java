package com.example.mybuttonview;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by de on 2018/2/13.
 */

public class PatientActivitySetPill extends AppCompatActivity implements TimeDialogFragment.NoticeDialogListener {
    ImageView imageView;
    TextView medName,medUse,dORh;
    NumberPicker numOfTime,spaceTime,noticeTime;
    Spinner spinnerTime, Per,notMeth;
    EditText perNum;
    Button pickTime,checkPost;
    private SharedPreferences patientSP ;
    int flag=0;
    String pnc,medno,sickno,notice,detail,pernum, statime ="8:00", spatime,pertime="",
            notrep,spinnertime="天",per="片",notimeth="铃声",setime="notset";
    String imageUrl = "http://www.dengrong.xin:3002/images/";
    String dataUrl = "http://www.dengrong.xin:3001/patient/showOneMedicine";
    String choiceUrl="http://www.dengrong.xin:3001/patient/addChoice";
    String checkUrl="http://www.dengrong.xin:3001/patient/checkChoice";
    String deleteUrl="http://www.dengrong.xin:3001/patient/deleteChoice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_set_pill);
        //实例化各个需要的控件
        imageView=(ImageView)findViewById(R.id.imageView);
        medName=(TextView)findViewById(R.id.medName);
        medUse=(TextView)findViewById(R.id.medUse);
        dORh=(TextView)findViewById(R.id.dOrh);
        numOfTime=(NumberPicker)findViewById(R.id.numOfTime);
        spaceTime=(NumberPicker)findViewById(R.id.spaceTime);
        noticeTime=(NumberPicker)findViewById(R.id.noticeTime);
        spinnerTime=(Spinner)findViewById(R.id.spinnerTime);
        Per =(Spinner)findViewById(R.id.per);
        notMeth=(Spinner)findViewById(R.id.notMeth);
        perNum=(EditText)findViewById(R.id.perNum);
        pickTime=(Button)findViewById(R.id.pickTime);
        checkPost=(Button)findViewById(R.id.checkPost);
        //获得medno,sickno,pnc
        patientSP=getSharedPreferences("Sickness",0);
        medno=patientSP.getString("medno","未获取");
        sickno=patientSP.getString("sickno","未获取");
        patientSP=getSharedPreferences("Patient",0);
        pnc=patientSP.getString("pnc","未获取");
        //获取指定药品的信息
        getOneMedicine();
        //加载药品图片
        imageUrl=imageUrl+medno+".jpg";
        ImageLoader loader = new ImageLoader(AppController.getRequestQueue(), new BitmapCash());
        ImageLoader.ImageListener listener =ImageLoader.getImageListener(imageView,R.drawable.ic_dashboard_black_24dp, R.drawable.ic_dashboard_black_24dp);
        loader.get(imageUrl, listener);
        //确定该choice是否已存在
        isChoiceExist();
        //实现数字选择
        initNumPicker();
        //实现spinnerTime下拉列表选择
        spinnerOfTime();
        //实现per下拉列表选择
        spinnerOfPer();
        //实现notMeth下拉列表选择
        spinnerOfMeth();
    }

    public void getOneMedicine(){
        StringRequest strReq = new StringRequest(Request.Method.POST, dataUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            notice=jsonArray.getJSONObject(0).getString("notice");
                            detail=jsonArray.getJSONObject(0).getString("detail");
                            medName.setText(jsonArray.getJSONObject(0).getString("name"));
                            medUse.setText("用法："+jsonArray.getJSONObject(0).getString("comuse"));
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
                params.put("medno",medno);
                params.put("sickno",sickno);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //缓存图片
    public class BitmapCash implements ImageLoader.ImageCache {

        public LruCache<String, Bitmap> cache;
        //将缓存图片的大小设置为10M
        public int max = 10*1024*1024;
        public BitmapCash () {
            cache = new LruCache<String,Bitmap>(max){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes()*value.getHeight();
                }
            };

        }
        //这里会获取到图片
        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            // TODO Auto-generated method stub
            cache.put(url, bitmap);
        }
    }

    //点击注意事项弹出对话框
    public void medNoticeClicked(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("注意事项")
                .setMessage(notice)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alterDialog = alertDialogBuilder.create();
        alterDialog.show();
    }

    //点击详细信息弹出对话框
    public void medDetailClicked(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("详细信息")
                .setMessage(detail)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alterDialog = alertDialogBuilder.create();
        alterDialog.show();
    }

    public void isChoiceExist(){
        StringRequest strReq = new StringRequest(Request.Method.POST, checkUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            flag=jsonArray.getJSONObject(0).getInt("success");
                            if(flag==1) checkPost.setText("确认删除");
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
                params.put("medno",medno);
                params.put("sickno",sickno);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void initNumPicker(){
        numOfTime.setMinValue(1);
        numOfTime.setMaxValue(9);
        numOfTime.setValue(1);
        spaceTime.setMinValue(1);
        spaceTime.setMaxValue(24);
        spaceTime.setValue(12);
        noticeTime.setMinValue(1);
        noticeTime.setMaxValue(9);
        noticeTime.setValue(3);
    }


    public void spinnerOfTime(){
        List<String> dataList;
        final ArrayAdapter<String> adapter;
        dataList = new ArrayList<String>();
        dataList.add("天");
        dataList.add("周");
        adapter = new ArrayAdapter<String>(PatientActivitySetPill.this,android.R.layout.simple_spinner_item,dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //选择后存储并相应改变对应textview显示
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnertime=adapter.getItem(position);
                if(spinnertime=="周")
                    dORh.setText("天");
                else
                    dORh.setText("小时");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void spinnerOfPer(){
        List<String> dataList;
        final ArrayAdapter<String> adapter;
        dataList = new ArrayList<String>();
        dataList.add("片");
        dataList.add("粒");
        dataList.add("包");
        dataList.add("mg");
        dataList.add("ml");
        adapter = new ArrayAdapter<String>(PatientActivitySetPill.this,android.R.layout.simple_spinner_item,dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Per.setAdapter(adapter);
        Per.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                per=adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void spinnerOfMeth(){
        List<String> dataList;
        final ArrayAdapter<String> adapter;
        dataList = new ArrayList<String>();
        dataList.add("铃声");
        dataList.add("震动");
        adapter = new ArrayAdapter<String>(PatientActivitySetPill.this,android.R.layout.simple_spinner_item,dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notMeth.setAdapter(adapter);
        notMeth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notimeth=adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //点击提交或删除choice信息
    public void choicePostClicked(View view){
        boolean isRight=false;
        //判断频率单位及检错
        if(spinnertime.equals("周")){
            Calendar calendar = Calendar.getInstance();
            setime=""+calendar.get(Calendar.DAY_OF_WEEK);
            if((numOfTime.getValue()-1)*spaceTime.getValue()<7) isRight=true;
        }else{
            if((numOfTime.getValue()-1)*spaceTime.getValue()<24) isRight=true;
        }
        pernum=""+perNum.getText()+per;
        spatime=""+spaceTime.getValue();
        pertime="每"+spinnertime+numOfTime.getValue()+"次";
        notrep=""+noticeTime.getValue();
        if(isRight) {
            if (flag == 0)
                //将用户选择存储在数据库中
                addChoice();
            else
                //将用户选择从数据库中删除
                deleteChoice();
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientActivitySetPill.this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("设置错误!")
                    .setPositiveButton("间隔时间与服药频率数值不合理", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
        }
    }


    public void addChoice(){
        StringRequest strReq = new StringRequest(Request.Method.POST, choiceUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int affectedRows = jsonObject.getInt("affectedRows");
                            if(affectedRows == 1) {
                                flag=1;
                                checkPost.setText("确认删除");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientActivitySetPill.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("设置成功!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientActivitySetPill.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("设置失败!")
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
                params.put("sickno",sickno);
                params.put("medno",medno);
                params.put("pernum",pernum);
                params.put("statime",statime);
                params.put("spatime",spatime);
                params.put("pertime",pertime);
                params.put("notimeth",notimeth);
                params.put("notrep",notrep);
                params.put("setime",setime);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


    public void deleteChoice(){
        StringRequest strReq = new StringRequest(Request.Method.POST, deleteUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int affectedRows = jsonObject.getInt("affectedRows");
                            if(affectedRows == 1) {
                                flag=0;
                                checkPost.setText("确认提交");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientActivitySetPill.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("操作成功!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientActivitySetPill.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("取消失败!")
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
                params.put("sickno",sickno);
                params.put("medno",medno);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //点击弹出时间选取对话框
    public void timePickClicked(View view){
        DialogFragment newFragment = new TimeDialogFragment();
        newFragment.show(getSupportFragmentManager(), "起始时间");
    }

    //实现时间选取接口
    @Override
    public void onTimeDialogTimeSet(DialogFragment dialog) {
        TimeDialogFragment dialogFragment=(TimeDialogFragment) dialog;
        statime =dialogFragment.getTimeString();
        pickTime.setText(statime);
    }

}
