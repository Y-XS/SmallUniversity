package com.example.app.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;


public class GVAdapter extends BaseAdapter {

    private Context context;
    private List<String> urls = new ArrayList<>();

    public GVAdapter(Context context,List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int i) {
        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if(view == null){
//            view = LayoutInflater.from(context).inflate(R.layout.item_image,null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) view.findViewById(R.id.image);
//            vh.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(vh);
        }
        vh = (ViewHolder) view.getTag();
        if(urls!=null && urls.size()>0){
            Glide.with(context).load("file://"+urls.get(i)).centerCrop().into(vh.imageView);
        }
        return view;
    }

    class ViewHolder{
        ImageView imageView;
//        CheckBox checkBox;
    }
}



//public class GVAdapter extends BaseAdapter {
//    private Context context;
//    private List<String> list;
//    LayoutInflater layoutInflater;
//
//
//    public GVAdapter(Context context, List<String> list) {
//        this.context = context;
//        this.list = list;
//        layoutInflater = LayoutInflater.from(context);
//    }
//    @Override
//    public int getCount() {
//        return list.size();//注意此处
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        CViewHolder holder;
//
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.content_img_item, parent, false);
//            convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            holder = new CViewHolder();
//            holder.imageView = (ImageView) convertView.findViewById(R.id.content_img_item);
//            convertView.setTag(holder);
//        } else {
//            convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            holder = (CViewHolder) convertView.getTag();
//        }
//
////        Log.d("TAG", "getView: "+position+" "+list.size()+list.get(position));
//        Glide.with(context)
//                .load(list.get(position))
//                .placeholder(R.mipmap.error)
//                .error(R.mipmap.error)
//                .into(holder.imageView);
////        holder.imageView.setImageResource(R.mipmap.error);
//
//        return convertView;
//    }
//
//    static class CViewHolder{
//        ImageView imageView;
//    }
//}
