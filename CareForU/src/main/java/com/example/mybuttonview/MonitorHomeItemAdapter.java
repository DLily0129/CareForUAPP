package com.example.mybuttonview;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MonitorHomeItemAdapter extends RecyclerView.Adapter<MonitorHomeItemAdapter.MyViewHolder>{
    // 填充数据的list
    private ArrayList<MonitorHomeItem> list;
    // 用来导入布局
    private LayoutInflater inflater = null;
    private Context context;
    public interface OnItemOnClickListener{
        void onItemOnClick(View view,int pos);
        void onItemLongOnClick(View view ,int pos);
    }
    private OnItemOnClickListener mOnItemOnClickListener;
    public void setOnItemClickListener(OnItemOnClickListener listener){
        this.mOnItemOnClickListener = listener;
    }
    MonitorHomeItemAdapter(ArrayList<MonitorHomeItem> list, Context context){
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.monitor_home_item,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        if(list.size() != 0){
            holder.tv_nc.setText(list.get(position).getPnc());
            holder.tv_name.setText(list.get(position).getPname());
            if(list.get(position).getIscheck().equals("n")){
                Resources resources = context.getResources();
                Drawable drawable = resources.getDrawable(R.drawable.alert);
                holder.image.setImageDrawable(drawable);
            }
            else{
                holder.image.setImageDrawable(null);
            }
            if(mOnItemOnClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemOnClickListener.onItemOnClick(view, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemOnClickListener.onItemLongOnClick(view, position);
                        return false;
                    }
                });
                holder.tv_nc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemOnClickListener.onItemOnClick(view, position);
                    }
                });
                holder.tv_nc.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemOnClickListener.onItemLongOnClick(view, position);
                        return false;
                    }
                });
                holder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemOnClickListener.onItemOnClick(view, position);
                    }
                });
                holder.tv_name.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemOnClickListener.onItemLongOnClick(view, position);
                        return false;
                    }
                });
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemOnClickListener.onItemOnClick(view, position);
                    }
                });
                holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemOnClickListener.onItemLongOnClick(view, position);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_nc;
        TextView tv_name;
        ImageView image;
        public MyViewHolder(View view)
        {
            super(view);
            tv_nc = view.findViewById(R.id.tv_pnc);
            tv_name = view.findViewById(R.id.tv_pname);
            image = view.findViewById(R.id.imageView_alert);
        }
    }
}
