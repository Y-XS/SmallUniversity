package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.activities.ChatActivity;
import com.example.app.activities.ContentActivity;
import com.example.app.activities.MainActivity;
import com.example.app.activities.R;
import com.example.app.beans.PostingItem;
import com.example.app.utils.MyApplication;

import java.util.List;

public class PostingItemAdapter extends RecyclerView.Adapter<PostingItemAdapter.ViewHolder> {

    private Context mContext;
    private List<PostingItem> mlistItems;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        RelativeLayout relativeLayout;
        ImageView headImg,image;
        TextView textContent,userName,publishTime;
        public ViewHolder(View view){
            super(view);
            itemView = view;
            relativeLayout = (RelativeLayout)view;
            headImg = (ImageView)view.findViewById(R.id.head_img);
            image = (ImageView)view.findViewById(R.id.item_part_img);
            userName = (TextView)view.findViewById(R.id.name);
            publishTime = (TextView)view.findViewById(R.id.time);
            textContent = (TextView)view.findViewById(R.id.item_part_text);
        }
    }
    public PostingItemAdapter(List<PostingItem> listItems){mlistItems=listItems;
        Log.d("PIAdapter", "PostingItemAdapter: -----listitems.size="+listItems.size());}

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public PostingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        final PostingItemAdapter.ViewHolder holder = new PostingItemAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PostingItem item = mlistItems.get(position);
//                Toast.makeText(v.getContext(),"you clicked view"+item.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyApplication.getContext(), ContentActivity.class);
                intent.putExtra("item",item);
//                intent.putExtra("name",item.getName());
//                intent.putExtra("headImgUrl",item.getHeadImgUrl());
//                intent.putExtra("textContent",item.getText());
////                intent.putStringArrayListExtra("imgUrls",item.getImageUrl());
//                intent.putExtra("time",item.getTime());
                mContext.startActivity(intent);
            }
        });
        holder.headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PostingItem item = mlistItems.get(position);
//                Toast.makeText(v.getContext(),"you clicked img"+item.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyApplication.getContext(), ChatActivity.class);
                intent.putExtra("id",item.getUserId());
                intent.putExtra("userName",item.getName());
                intent.putExtra("headImg",item.getHeadImgUrl());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(PostingItemAdapter.ViewHolder holder, int position) {
        PostingItem item = mlistItems.get(position);
        Log.d("PIAdapter", "BindViewHolder: ----------->position="+position);
        holder.userName.setText(item.getName());
        holder.publishTime.setText(item.getTime());
        holder.textContent.setText(item.getText());
        if(item.getVisibility()==View.VISIBLE){
            Glide.with(mContext).load(item.getImageUrl().get(0)).into(holder.image);
            Log.d("PIAdapter", "+++++++++++++++++++++item: "+item.getImageUrl().get(0)+" "+item.getName());
        }
        Log.d("TAG", "+++++++++++++++++++++>>>>>>>>>>> "+item.getHeadImgUrl());
        Glide.with(mContext).load(item.getHeadImgUrl()).placeholder(R.mipmap.error).error(R.mipmap.error).centerCrop().into(holder.headImg);
        holder.image.setVisibility(item.getVisibility());
    }

    @Override
    public int getItemCount() {
        return mlistItems.size();
    }

}
