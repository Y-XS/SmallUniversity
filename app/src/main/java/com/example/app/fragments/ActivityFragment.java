package com.example.app.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.app.activities.R;
import com.example.app.adapters.PostingItemAdapter;
import com.example.app.beans.Address;
import com.example.app.beans.PostingItem;
import com.example.app.beans.PostingItemData;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class ActivityFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "group";
    private String netAddr = "http://1.15.115.24/getMsg";
    private Address addr;
    private String url,token,responseDta,group;
    private MyApplication myApplication;
    private RecyclerView recyclerView;
    private GridView gridView;
    private List<String> a=new ArrayList<>();

    private String status,msg;
    private int count=1,count_up=1,temp=0;
    private List<PostingItemData.ItemData> data;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean mIsPrepare = false;		//视图还没准备好
    boolean mIsVisible= false;		//不可见
    boolean mIsFirstLoad = true;	//第一次加载

    private boolean loading = false;
    private RecyclerView.OnScrollListener loadMoreListener;
    private SwipeRefreshLayout swipeRefresh;
    private List<PostingItem> PostingItems = new ArrayList<>();
    private PostingItemAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public ActivityFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ActivityFragment newInstance(String param1) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if (getArguments() != null) {
            bundle = this.getArguments();
            group = bundle.getString("group");
        }
        myApplication = (MyApplication)(this.getActivity().getApplication());
        token = myApplication.getLoginUser().getToken();
        Log.d("TAG", "onCreate: 当前group ==================================== "+group);
        a.add("http://1.15.115.24/images/pic_13.jpeg");
        a.add("http://1.15.115.24/images/pic_13.jpeg");
    }

    private void loadMoreItems(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    count+=1;
                    temp = count;
                    loadData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                loading = false;
            }
        }).start();

    }
    private void refreshItems(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        temp = count;
                        count=count_up;
                        Log.d("TAG", "**************************data: " + PostingItems+" "+count+" "+temp);
                        PostingItems.clear();
                        Log.d("TAG", "**************************data: " + data);
                        loadData();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_fa);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.
                OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
//                loadData();
            }
        });
        loadMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!loading && !recyclerView.canScrollVertically(1)){
                    loading = true;
                    loadMoreItems();
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fa);
        recyclerView.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL,false));
        adapter = new PostingItemAdapter(PostingItems);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(loadMoreListener);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.divider_rclist));
        recyclerView.addItemDecoration(divider);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsPrepare = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            mIsVisible = true;
            lazyLoad();
        } else {
            mIsVisible = false;
        }
    }

    private void loadMoreDate(){
        Log.d("TAG", "loadMoreDate: loading...");
        loadMoreItems();
        loading = false;
    }


    private void lazyLoad() {
        //这里进行三个条件的判断，如果有一个不满足，都将不进行加载
        if (!mIsPrepare || !mIsVisible||!mIsFirstLoad) {
            return;
        }
        loadData();
        //数据加载完毕,恢复标记,防止重复加载
        mIsFirstLoad = false;
    }

    private void loadData() {
        //这里进行网络请求和数据装载
        Log.d("TAG", "111111111111111111"+count);
        String n = String.valueOf(count);

        Log.d("TAG", "222222222222222222 "+count);
        addr = new Address(netAddr);

        addr.add(true,"group",group)
                .add(false, "num", n)
                .add(false, "token", token);

        url = addr.getAddress();
        HttpUtil.getData(url,new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseDta = response.body().string();
                Log.d("TAG", "onResponse: " + responseDta);
                Log.d("TAG", "onCreate: 当前group ==================================== "+group);
                parseJSONWithGSON(responseDta);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });
    }

    private void parseJSONWithGSON(String jsonData){
        int visibility;
        String ImgUrl,headImgUrl;
        List<PostingItemData.Pics> pics;
        List<String> imgUrls=new ArrayList<>();
        Gson gson = new Gson();
        PostingItemData datas = gson.fromJson(jsonData,PostingItemData.class);

        status = datas.getStatus();
        msg = datas.getMsg();
        if("1".equals(status)){
            data = datas.getData();
            if(data.size()==0){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"没有更多数据了...",Toast.LENGTH_SHORT).show();
                    }
                });
                return;}
            Log.d("TAG", "test Pic: "+data.get(0).getPic());
            for(PostingItemData.ItemData itemData:data){
                if(itemData.getPic().size()!=0){
//                    ImgUrl = itemData.getPic().get(0).getUrl();
                    pics = itemData.getPic();
                    for(PostingItemData.Pics pic:pics){
                        imgUrls.add(pic.getUrl());
                        Log.d("AF", "parseJSONWithGSON: --------->imgUrl"+pic.getUrl()+" "+"imgUrls.size="+imgUrls.size());
                    }
                    visibility=View.VISIBLE;
                }else {
//                    ImgUrl = "noImg";
                    visibility=View.GONE;}
                if(itemData.getFace()!=null){
                    headImgUrl = itemData.getFace().getUrl();
                }else {
                    headImgUrl = "noHeadImg";
                }
                Log.d("TAG", "***************************ImgUrl:"+imgUrls);
                PostingItems.add(new PostingItem(itemData.getName(),itemData.getTitle(),itemData.getTime(),headImgUrl,imgUrls,itemData.getMsg(),visibility));
                imgUrls.clear();
            }
            Log.d("TAG", "parseJSONWithGSON: "+data.get(0));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsFirstLoad=true;
        mIsPrepare=false;
        mIsVisible = false;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
