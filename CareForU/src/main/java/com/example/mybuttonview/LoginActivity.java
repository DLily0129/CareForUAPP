package com.example.mybuttonview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    boolean ispatient = true;
    RadioButton patient;
    RadioButton monitor;
    String name, pwd;
    EditText in_name, in_pwd;
    TextView sign_up;
    String url = "http://www.dengrong.xin:3001/login/checkPatient";
    ProgressDialog progressDialog;
    static boolean patientLogin=false;
    static boolean monitorLogin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(patientLogin) {
            Intent intent = new Intent(LoginActivity.this,PatientMainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        if(monitorLogin) {
            Intent intent = new Intent(LoginActivity.this,MonitorMainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }

        setContentView(R.layout.activity_login);
        patient = findViewById(R.id.rb_patient);
        monitor = findViewById(R.id.rb_monitor);
        in_name = findViewById(R.id.input_name);
        in_pwd = findViewById(R.id.input_password);
        sign_up = findViewById(R.id.link_signup);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onRadioClicked(View view){
        if(patient.isChecked()){
            ispatient = true;
            url = "http://www.dengrong.xin:3001/login/checkPatient";
        }
        else if(monitor.isChecked()){
            ispatient = false;
            url = "http://www.dengrong.xin:3001/login/checkMonitor";
        }
    }

    public void onBtnLoginClicked(View view){
        name = in_name.getText().toString();
        pwd = in_pwd.getText().toString();

        if(name.isEmpty() || pwd.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示")
                    .setMessage("请输入昵称或密码")
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
            progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("登录中...");
            progressDialog.show();

            checkLogin();

        }
    }

    public void checkLogin(){
        StringRequest strReq = new StringRequest(Method.POST, url,
                new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    int success = jsonArray.getJSONObject(0).getInt("success");
                    Log.i("Login_Response",String.valueOf(success)+"---------------------------");
                    if(success == 1){
                        if(ispatient){
                            SharedPreferences patientSP=getSharedPreferences("Patient",0);
                            SharedPreferences.Editor editor=patientSP.edit();
                            editor.putString("pnc",name);
                            editor.apply();
                            progressDialog.dismiss();
                            patientLogin=true;
                            //进入病人主界面
                            Intent intent = new Intent(LoginActivity.this,PatientMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            SharedPreferences monitorSP=getSharedPreferences("Monitor",0);
                            SharedPreferences.Editor editor=monitorSP.edit();
                            editor.putString("mnc",name);
                            editor.apply();
                            progressDialog.dismiss();
                            monitorLogin=true;
                            //进入监管人主界面
                            Intent intent = new Intent(LoginActivity.this,MonitorMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertDialogBuilder.setTitle("提示")
                                .setMessage("昵称或密码错误")
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
                params.put("nc", name);
                params.put("passwd", pwd);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq);
    }
}
