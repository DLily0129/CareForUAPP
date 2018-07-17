package com.example.mybuttonview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by de on 2018/2/15.
 */

public class PatientChoiceAdapter extends BaseAdapter {
    protected ArrayList<PatientItemChoice> medicines;
    private Activity activity;
    private LayoutInflater inflater;
    private TextView itemMedName, itemUseNum;
    private CheckBox choiceCheck;
    public PatientChoiceAdapter(){
        super();
    }
    public PatientChoiceAdapter(Activity activity, ArrayList<PatientItemChoice> medicines) {
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
            convertView = inflater.inflate(R.layout.patient_listview_choices, null);

        itemMedName= (TextView) convertView.findViewById(R.id.choiceName);
        itemUseNum = (TextView) convertView.findViewById(R.id.choiceNum);
        choiceCheck=(CheckBox) convertView.findViewById(R.id.choiceCheck);
        itemMedName.setText((CharSequence) medicines.get(position).getName());
        itemUseNum.setText(""+(CharSequence) medicines.get(position).getUseNum());
        if(medicines.get(position).getIscheck().equals("y")){
            choiceCheck.setChecked(true);
            choiceCheck.setText("已服用");
        }else {
            choiceCheck.setChecked(false);
            choiceCheck.setText("未服用");
        }
        return convertView;
    }
}
