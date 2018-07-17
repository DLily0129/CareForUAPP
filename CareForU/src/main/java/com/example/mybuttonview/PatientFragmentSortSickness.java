package com.example.mybuttonview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by de on 2018/2/13.
 */

public class PatientFragmentSortSickness extends Fragment {
    Button tnb,gxy,gxz,gxt,px,fsgjy,mxxs,gyh,ncx,xzb;
    private SharedPreferences patientSP ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.patient_sort_sickness, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        patientSP=this.getActivity().getSharedPreferences("Sickness",0);
        initButton();
        setButtonOnClick(tnb,"糖尿病");
        setButtonOnClick(gxy,"高血压");
        setButtonOnClick(gxz,"高血脂");
        setButtonOnClick(gxt,"高血糖");
        setButtonOnClick(px,"贫血");
        setButtonOnClick(fsgjy,"风湿关节炎");
        setButtonOnClick(mxxs,"慢性心衰");
        setButtonOnClick(gyh,"肝硬化");
        setButtonOnClick(ncx,"脑出血");
        setButtonOnClick(xzb,"心脏病");
    }

    private void initButton() {
        tnb=(Button) getView().findViewById(R.id.tnb);
        gxy=(Button) getView().findViewById(R.id.gxy);
        gxz=(Button) getView().findViewById(R.id.gxz);
        gxt=(Button) getView().findViewById(R.id.gxt);
        px=(Button) getView().findViewById(R.id.px);
        fsgjy=(Button) getView().findViewById(R.id.fsgjy);
        mxxs=(Button) getView().findViewById(R.id.mxxs);
        gyh=(Button) getView().findViewById(R.id.gyh);
        ncx=(Button) getView().findViewById(R.id.ncx);
        xzb=(Button) getView().findViewById(R.id.xzb);
    }

    private void setButtonOnClick(Button button, final String name){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=patientSP.edit();
                editor.putString("Sickname",name);
                editor.apply();
                Intent intent=new Intent(getActivity(),PatientActivityListMedicine.class);
                startActivity(intent);
            }
        });
    }
}
