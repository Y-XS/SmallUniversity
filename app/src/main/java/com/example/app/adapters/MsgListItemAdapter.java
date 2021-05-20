package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.activities.MainActivity;
import com.example.app.beans.MsgListItem;
import com.example.app.activities.ChatActivity;
import com.example.app.activities.R;
import com.example.app.utils.MyApplication;

import java.util.List;

public class MsgListItemAdapter extends RecyclerView.Adapter<MsgListItemAdapter.ViewHolder>{

    private Context mContext;
    private List<MsgListItem> msgListItems;

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        ImageView headImg;
        TextView textContent,userName,publishTime;
        public ViewHolder(View view){
            super(view);
            relativeLayout = (RelativeLayout)view;
            headImg = (ImageView)view.findViewById(R.id.msg_head_img);
            userName = (TextView)view.findViewById(R.id.msg_user_name);
            publishTime = (TextView)view.findViewById(R.id.msg_time);
            textContent = (TextView)view.findViewById(R.id.msg_text);
        }
    }
    public MsgListItemAdapter(List<MsgListItem> listItems){msgListItems=listItems;}

    @NonNull
    @Override
    public MsgListItemAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.msg_item,parent,false);
        final MsgListItemAdapter.ViewHolder holder = new MsgListItemAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MsgListItem item = msgListItems.get(position);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("id",item.getUserId());
                intent.putExtra("userName",item.getName());
                intent.putExtra("headImg",item.getHeadImgUrl());
                mContext.startActivity(intent);
            }
        });
//        holder.headImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyApplication.getContext(), ChatActivity.class);
//                MyApplication.getContext().startActivity(intent);
//            }
//        });


        return holder;
    }

    @Override
    public void onBindViewHolder(MsgListItemAdapter.ViewHolder holder, int position) {
        MsgListItem MsgListItem = msgListItems.get(position);
        holder.userName.setText(MsgListItem.getName());
        holder.publishTime.setText(MsgListItem.getTime());
        holder.textContent.setText(MsgListItem.getText());
        Glide.with(mContext).load(MsgListItem.getHeadImgUrl()).error(R.mipmap.error).into(holder.headImg);
    }

    @Override
    public int getItemCount() {
        return msgListItems.size();
    }
}
