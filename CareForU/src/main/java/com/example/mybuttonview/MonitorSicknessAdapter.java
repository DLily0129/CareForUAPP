package com.example.mybuttonview;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MonitorSicknessAdapter extends BaseAdapter {
    // 填充数据的list
    private ArrayList<MonitorSicknessItem> list;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public MonitorSicknessAdapter(ArrayList<MonitorSicknessItem> list, Context context){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.monitor_sickness_item,null);
            holder.tv_pillname = (TextView)convertView.findViewById(R.id.tv_pillname);
            holder.tv_starttime= (TextView)convertView.findViewById(R.id.tv_starttime);
            holder.tv_pernum = (TextView)convertView.findViewById(R.id.tv_pernum);
            holder.tv_pertime = (TextView)convertView.findViewById(R.id.tv_pertime);
            holder.tv_state = (TextView)convertView.findViewById(R.id.tv_state);
            holder.image = (ImageView)convertView.findViewById(R.id.iv_alert);
            // 为view设置标签
            convertView.setTag(holder);
        }
        else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        if(list.size()!=0){
            holder.tv_pillname.setText(list.get(position).getPillname());
            holder.tv_starttime.setText(list.get(position).getStarttime());
            holder.tv_pernum.setText(list.get(position).getPernum());
            holder.tv_pertime.setText(list.get(position).getPertime());
            if(list.get(position).getIscheck().equals("n")){
                Resources resources = context.getResources();
                Drawable drawable = resources.getDrawable(R.drawable.alert);
                holder.image.setImageDrawable(drawable);
                holder.tv_state.setText("未按时服用");
            }
            else {
                holder.tv_state.setText("已按时服用");
            }
        }

        return convertView;
    }
    public static class ViewHolder {
        TextView tv_pillname;
        TextView tv_starttime;
        TextView tv_pertime;
        TextView tv_pernum;
        TextView tv_state;
        ImageView image;
    }
}
