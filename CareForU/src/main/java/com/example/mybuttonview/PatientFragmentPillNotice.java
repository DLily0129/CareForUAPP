package com.example.mybuttonview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by de on 2018/2/13.
 */

public class PatientFragmentPillNotice extends Fragment {
    Button getDate,beSure;
    ListView listMedicine;
    private SharedPreferences patientSP ;
    PatientChoiceAdapter adapter;
    ArrayList<PatientItemChoice> medicines;
    String url = "http://www.dengrong.xin:3001/patient/listByPatient";
    String checkurl="http://www.dengrong.xin:3001/patient/setCheck";
    String pnc;
    int currentDayofWeek=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.patient_pill_notice, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDate=(Button)getActivity().findViewById(R.id.getDate);
        beSure=(Button)getActivity().findViewById(R.id.beSure);
        listMedicine=(ListView)getActivity().findViewById(R.id.noticeList);
        getPnc();
        setDate();
        getPatientChoice();
    }

    //获取用户号
    public void getPnc(){
        patientSP=this.getActivity().getSharedPreferences("Patient",0);
        pnc=patientSP.getString("pnc","未获取");
    }

    //使用DialogFragment设置日期
    public void setDate(){
        Calendar calendar = Calendar.getInstance();
        currentDayofWeek=calendar.get(Calendar.DAY_OF_WEEK);
        Log.e("info","init"+currentDayofWeek);
        getDate.setText(""+calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日");
        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DateDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datedialog");
            }
        });
        beSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDayofWeek=Integer.parseInt(patientSP.getString("dayofweek","1"));
                getPatientChoice();
            }
        });
    }



    //获取web数据库中对应病人的choice信息,并显示
    public void getPatientChoice(){
        medicines=new ArrayList<PatientItemChoice>();
        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            final String [] sickno=new String[jsonArray.length()];
                            for(int i=0;i<jsonArray.length();i++) {
                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    if(object.getString("setime").equals("notset")) {
                                        PatientItemChoice medicine = new PatientItemChoice();
                                        medicine.setName(object.getString("name"));
                                        medicine.setMedno(object.getString("medno"));
                                        medicine.setUseNum(object.getString("pertime")+"   每次"+object.getString("pernum")
                                                +"  起始时间"+object.getString("statime"));
                                        medicine.setIscheck(object.getString("ischeck"));
                                        medicines.add(medicine);
                                    }
                                    if(!object.getString("setime").equals("notset")){
                                        for(int j=0;j<Integer.parseInt(object.getString("pertime").substring(2,3));j++){
                                            if((currentDayofWeek-Integer.parseInt(object.getString("setime")))%7
                                                    ==(j*Integer.parseInt(object.getString("spatime")))){
                                                PatientItemChoice medicine = new PatientItemChoice();
                                                medicine.setName(object.getString("name"));
                                                medicine.setMedno(object.getString("medno"));
                                                medicine.setIscheck(object.getString("ischeck"));
                                                medicine.setUseNum(object.getString("pertime")+"   每次"+object.getString("pernum")
                                                        +"  起始时间"+object.getString("statime"));
                                                medicines.add(medicine);
                                            }
                                        }
                                    }
                                    sickno[i]=object.getString("sickno");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //使用getActivity()设置adapter
                            adapter=new PatientChoiceAdapter(getActivity(),medicines);
                            listMedicine.setAdapter(adapter);
                            listMedicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //通过findViewById找到CheckBox,并根据item点击事件设置状态
                                    CheckBox choiceCheck=view.findViewById(R.id.choiceCheck);
                                    if(choiceCheck.isChecked()) {
                                        choiceCheck.setChecked(false);
                                        choiceCheck.setText("未服用");
                                        setIsCheck(medicines.get(position).getMedno(),"n");
                                    }
                                    else{
                                        choiceCheck.setChecked(true);
                                        choiceCheck.setText("已服用");
                                        setIsCheck(medicines.get(position).getMedno(),"y");
                                    }
                                }
                            });
                            listMedicine.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                    SharedPreferences sp=getActivity().getSharedPreferences("Sickness",0);
                                    SharedPreferences.Editor editor=sp.edit();
                                    editor.putString("medno",medicines.get(position).getMedno());
                                    editor.putString("sickno",sickno[position]);
                                    editor.commit();
                                    Intent intent=new Intent(getActivity(),PatientActivitySetPill.class);
                                    startActivity(intent);
                                    return false;
                                }
                            });
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
                params.put("pnc",pnc);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    //设置choice的ischeck字段值
    public void setIsCheck(final String medno, final String ischeck){
        StringRequest strReq = new StringRequest(Request.Method.POST, checkurl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
//                        try{
//                            JSONArray jsonArray = new JSONArray(response);
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
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
                params.put("ischeck",ischeck);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

}
