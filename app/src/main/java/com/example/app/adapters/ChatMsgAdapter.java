package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.activities.R;
import com.example.app.beans.ChatMsg;
import com.example.app.utils.MyApplication;

import java.util.List;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatMsg> chatMsgs;

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout leftLayout,rightLayout;
        TextView leftMsg,rightMsg;
        ImageView leftHeadImg,rightHeadImg;
        public ViewHolder(View view){
            super(view);
            leftLayout = (RelativeLayout)view.findViewById(R.id.msg_recv);
            rightLayout = (RelativeLayout)view.findViewById(R.id.msg_send);
            leftMsg = (TextView)view.findViewById(R.id.text_recv);
            rightMsg = (TextView)view.findViewById(R.id.text_send);
            leftHeadImg = (ImageView) view.findViewById(R.id.head_img_recv);
            rightHeadImg = (ImageView) view.findViewById(R.id.head_img_send);
        }
    }
    public ChatMsgAdapter(List<ChatMsg> msgList){
        chatMsgs = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.chatmsg_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMsg msg = chatMsgs.get(position);
        if (msg.getType() == ChatMsg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            Glide.with(mContext).load(msg.getheadImgUrl()).error(R.mipmap.error).into(holder.leftHeadImg);
//            holder.leftHeadImg.setImageResource(msg.getHeadImgId());
        } else if(msg.getType() == ChatMsg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
//            holder.rightHeadImg.setImageResource(msg.getHeadImgId());
            Glide.with(mContext).load(msg.getMyHeadImgUrl()).error(R.mipmap.error).into(holder.rightHeadImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }
}
