package com.example.mybuttonview;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by de on 2018/2/13.
 */

public class PatientActivityListMedicine extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener,AdapterView.OnItemClickListener{
    private SharedPreferences patientSP ;
    TextView sicknessName;
    ListView listMedicine;
    RadioGroup radioGroup;
    PatientMedicineAdapter adapter;
    String Sickname,level="0",sickno;
    String [] mednos={"","","","","","","",""};
    String url = "http://www.dengrong.xin:3001/patient/listBySickness";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_list_medicine);
        sicknessName=(TextView)findViewById(R.id.sicknessName);
        listMedicine=(ListView)findViewById(R.id.listMedicine);
        radioGroup= (RadioGroup) findViewById(R.id.radioGroup);
        patientSP=getSharedPreferences("Sickness",0);
        Sickname=patientSP.getString("Sickname","未获取");
        sicknessName.setText(Sickname);
        getPatientNaT();
        radioGroup.setOnCheckedChangeListener(this);
    }

    //获取指定病种的编号及推荐药名称及用法
    public void getPatientNaT(){
        //每次从volley获取数据时重建ArrayList实现刷新
        final ArrayList<PatientItemMedicine> medicines=new ArrayList<PatientItemMedicine>();
        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){
                                try {
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    sickno=object.getString("sickno");
                                    PatientItemMedicine medicine=new PatientItemMedicine();
                                    medicine.setName(object.getString("name"));
                                    medicine.setComuse(object.getString("comuse"));
                                    mednos[i]=object.getString("medno");
                                    medicines.add(medicine);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //注意刷新数据
                            adapter=new PatientMedicineAdapter(PatientActivityListMedicine.this,medicines);
                            listMedicine.setAdapter(adapter);
                            listMedicine.setOnItemClickListener(PatientActivityListMedicine.this);
                            adapter.notifyDataSetChanged();
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
                params.put("name",Sickname);
                params.put("level",level);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //点击列表里的item时保存药品参数并进入药品设置界面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor=patientSP.edit();
        editor.putString("medno",mednos[position]);
        editor.putString("sickno",sickno);
        editor.apply();
        Intent intent=new Intent(this,PatientActivitySetPill.class);
        startActivity(intent);
    }

    //单选变化时重新加载listview
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.sicknessHeavy:
                level="1";
                getPatientNaT();
                break;
            case R.id.sicknessFree:
                level="0";
                getPatientNaT();
                break;
        }
    }

}
