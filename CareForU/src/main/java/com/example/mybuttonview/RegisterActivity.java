package com.example.mybuttonview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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


public class RegisterActivity extends AppCompatActivity {
    TextView back;
    ImageView img_back;
    EditText in_nc,in_name, in_pwd, confirm_pwd;
    String in_nc_str,in_name_str, in_pwd_str, confirm_pwd_str;
    boolean ispatient = true;
    RadioButton patient;
    RadioButton monitor;
    String check_url="http://www.dengrong.xin:3001/login/isPatientExist",
            add_url="http://www.dengrong.xin:3001/login/addPatient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        back = findViewById(R.id.tv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        patient = findViewById(R.id.rb_patient);
        monitor = findViewById(R.id.rb_monitor);
        in_nc=findViewById(R.id.input_nc);
        in_name = findViewById(R.id.input_name);
        in_pwd = findViewById(R.id.input_password);
        confirm_pwd = findViewById(R.id.input_password_confirm);
    }

    public void onRadioClicked(View view){
        if(patient.isChecked()){
            ispatient = true;
            check_url="http://www.dengrong.xin:3001/login/isPatientExist";
            add_url="http://www.dengrong.xin:3001/login/addPatient";
        }
        else if(monitor.isChecked()){
            ispatient = false;
            check_url="http://www.dengrong.xin:3001/login/isMonitorExist";
            add_url="http://www.dengrong.xin:3001/login/addMonitor";
        }
    }

    public void onBtnRegisterClicked(View view){
        in_nc_str=in_nc.getText().toString();
        in_name_str = in_name.getText().toString();
        in_pwd_str = in_pwd.getText().toString();
        confirm_pwd_str = confirm_pwd.getText().toString();

        if(in_name_str.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("请输入用户名")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
        }
        else if(in_nc_str.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("请输入昵称")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
        }
        else if(in_pwd_str.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("请输入密码")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
        }
        else if(confirm_pwd_str.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("请确认密码")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
        }
        else if(!in_pwd_str.equals(confirm_pwd_str)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("密码确认有误，请重新输入")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alterDialog = alertDialogBuilder.create();
            alterDialog.show();
            in_pwd.getText().clear();
            confirm_pwd.getText().clear();
            in_pwd.requestFocus();
        }
        else {
            checkRegister();
        }
    }

    public void checkRegister(){
        StringRequest strReq = new StringRequest(Request.Method.POST, check_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            int success = jsonArray.getJSONObject(0).getInt("success");
                            Log.i("Login_Response","checkRegister:"+String.valueOf(success)+"---------------------------");
                            if(success == 0){
                                addRegister();
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("该用户已存在，请重新输入")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                                in_nc.getText().clear();
                                in_nc.requestFocus();
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
                params.put("nc", in_nc_str);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void addRegister(){
        StringRequest strReq = new StringRequest(Request.Method.POST, add_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int affectedRows = jsonObject.getInt("affectedRows");
                            Log.i("Login_Response","addRegister:"+String.valueOf(affectedRows)+"---------------------------");
                            if(affectedRows == 1){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("注册成功!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                finish();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                alertDialogBuilder.setTitle("提示")
                                        .setMessage("注册失败!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alterDialog = alertDialogBuilder.create();
                                alterDialog.show();
                                in_nc.getText().clear();
                                in_name.getText().clear();
                                in_pwd.getText().clear();
                                confirm_pwd.getText().clear();
                                in_nc.requestFocus();
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
                params.put("nc",in_nc_str);
                params.put("xm", in_name_str);
                params.put("passwd", in_pwd_str);
                if(patient.isChecked()){
                    params.put("mnc", "default");
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
