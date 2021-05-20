package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.app.activities.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> urls = new ArrayList<>();
    LayoutInflater layoutInflater;

    public ImageAdapter(Context context,List<String> urls) {
        this.context = context;
        this.urls = urls;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = layoutInflater.inflate(R.layout.gv_content_item,viewGroup,false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        if(urls!=null && urls.size()>0){
            Glide.with(context).load(urls.get(position)).into(holder.imageView);
        }
        return view;
    }

    class ViewHolder{
        ImageView imageView;
    }
}
