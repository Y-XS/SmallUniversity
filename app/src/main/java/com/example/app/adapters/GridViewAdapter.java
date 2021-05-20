package com.example.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.app.activities.R;
import com.example.app.utils.MyApplication;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    public static int TYPE_PUB = 1;
    public static int TYPE_CONTENT = 2;

    private Context context;
    private List<String> list;
    LayoutInflater layoutInflater;
    private ImageView mImageView;
    private int type;

    public GridViewAdapter(Context context, List<String> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size()+1;//注意此处
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.pubimg_item, parent, false);
            convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.pub_img_item);
            convertView.setTag(holder);
        } else {
            convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            holder = (ViewHolder) convertView.getTag();
        }

        if(type==TYPE_PUB){
            if (position == list.size()) {
                holder.imageView.setImageResource(R.mipmap.ic_add);
            } else {
                Glide.with(MyApplication.getContext())
                        .load(list.get(position))
                        .into(holder.imageView);
            }
        }else {
            if(list.size()>0&position<list.size()){
                ViewGroup.LayoutParams para1=holder.imageView.getLayoutParams();
                para1.width=1600;
                para1.height=1600;
                holder.imageView.setLayoutParams(para1);

                Log.d("TAG", "getView: "+position+" "+list.size());
                Glide.with(MyApplication.getContext())
                        .load(list.get(position))
                        .into(holder.imageView);
            }else {
                holder.imageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

static class ViewHolder{
    ImageView imageView;
}

}
