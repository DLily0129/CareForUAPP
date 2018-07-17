package com.example.mybuttonview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by de on 2018/2/15.
 */

public class PatientMedicineAdapter extends BaseAdapter {
    protected ArrayList<PatientItemMedicine> medicines;
    private Activity activity;
    private LayoutInflater inflater;
    private TextView itemMedName,itemComUse;
    public PatientMedicineAdapter(){
        super();
    }
    public PatientMedicineAdapter(Activity activity, ArrayList<PatientItemMedicine> medicines) {
        this.activity=activity;
        this.medicines = medicines;
    }

    @Override
    public int getCount() {
        return medicines.size();
    }

    @Override
    public Object getItem(int position) {
        return medicines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.patient_listview_meds, null);

        itemMedName= (TextView) convertView.findViewById(R.id.itemMedName);
        itemComUse= (TextView) convertView.findViewById(R.id.itemComUse);
        itemMedName.setText((CharSequence) medicines.get(position).getName());
        itemComUse.setText("用法:"+(CharSequence) medicines.get(position).getComuse());

        return convertView;
    }
}
