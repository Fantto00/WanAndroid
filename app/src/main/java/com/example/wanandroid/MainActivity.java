package com.example.wanandroid;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import adapter.ArticleListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.ArticleInfo;
import pojo.BannerImageInfo;

public class MainActivity extends AppCompatActivity {

    private ArticleListAdapter articleListAdapter;
    private RecyclerView recyclerView;
    private BannerImageInfo bannerImageInfo;

    private List<String> urls_image;
    private Banner banner;
    //触底加载
    int page =0;
    //加载锁
    private boolean isLoading = false;
    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //从onResponse取数据
            if (msg.what==1){
                String data =(String) msg.obj;
                //将json转为实体
                ArticleInfo articleInfo = new Gson().fromJson(data, ArticleInfo.class);
                if (articleListAdapter != null) {
                    if (page == 1){
                        articleListAdapter.setListData(articleInfo.getData().getDatas());
                    }else{
                        Log.d("----------", "触底加载page:第"+page+"页");
                        articleListAdapter.addListData(articleInfo.getData().getDatas());
                    }
                    //解锁
                    isLoading = false;
                }
            }
            if (msg.what==2){
                Log.d("-------", "消息接收: ");
                //轮播图
                String data =(String) msg.obj;
                //json转实体
                bannerImageInfo = new Gson().fromJson(data, BannerImageInfo.class);
                Log.d("------", "json转完实体: "+bannerImageInfo);
                setBannerImage();
                Log.d("----------", "setbannerimage完成");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        View headerView = LayoutInflater.from(this).inflate(R.layout.header_banner, null);
        banner= headerView.findViewById(R.id.banner);
        articleListAdapter=new ArticleListAdapter(MainActivity.this,headerView);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager( this));
        recyclerView.setAdapter(articleListAdapter);
        //滚动监听
        ScrollListening();

        //耗时操作--handler
        //传入页码
        getHttpData();

        getHttpBannerImage();


    }

    //获取网络轮播图图片 耗时操作
    private void getHttpBannerImage(){
        OkHttpClient okHttpClient= new OkHttpClient();
        Request request = new Request.Builder().url("https://www.wanandroid.com/banner/json")
                .get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Log.d("----------", "轮播图data"+data);
                Log.d("--------", "json长度： "+data.length());
                Message message = new Message();
                message.what = 2;
                message.obj = data;
                mHandler.sendMessage(message);
                Log.d("--------", "消息发送 ");
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("------getBannerImage", "onFailure: "+e.toString());
            }
        });
    }

    private void setBannerImage(){
        if (bannerImageInfo != null&&bannerImageInfo.getData()!=null){
            urls_image = new ArrayList<>();
            for (BannerImageInfo.DataDTO dataDTO : bannerImageInfo.getData()){
                urls_image.add(dataDTO.getImagePath());
            }
            Log.d("------", "urls_image"+urls_image);
        }else{
            Log.d("-----------", "bannerimageinfo/getdata==null ");
            Log.d("--------", "banneriamgeinfo :"+bannerImageInfo);//null?
            Log.d("--------", "bannerImageInfo.getData(): "+bannerImageInfo.getData());
        }

        Log.d("-----------", "开始glide:  urls_image:"+urls_image);
        Log.d("-------", "banner状态height "+banner.getHeight()+  "width"+banner.getWidth());
        //glide
        banner.setAdapter(new BannerImageAdapter<String>(urls_image) {
            @Override
            public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                Glide.with(holder.imageView).load(data).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                Log.d("------", "图片加载失败 ");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                Log.d("-------", "图片加载成功");
                                return false;
                            }
                        })
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }


        }).addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(this));
    }



    //获取网络文章数据
    private void getHttpData() {
        OkHttpClient okHttpClient= new OkHttpClient();
        //String testurl = urlBuilder();
        //Log.d("------", "testurl: "+testurl);
        Request request = new Request.Builder().url(urlBuilder())
                //url不要写死
                //用urlBuilder
                .get().build();
        //通过OkHttpClient对象和request创建Call对象
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Log.d("----------", response.body().toString());

                String data = response.body().string();

                //耗时操作 --- handler
                //不能在这里设置ui操作数据
                Message message = new Message();
                message.what = 1;//标识/版本
                message.obj = data;
                mHandler.sendMessage(message);
                //网络请求成功
                page++;
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("---------------", "don't get,e: "+e.toString());
            }
        });
    }


    //触底加载
    private void ScrollListening(){

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){

            //onScrolledStateChanged监听滚动起止

            //监听滚动偏移量
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                if (layoutManager!=null&&!isLoading){
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    int timing = 3;
                    if(lastVisibleItemPosition>=itemCount-timing&&dy>0){
                        Log.d("----------", "触底加载触发");
                        isLoading = true;
                        getHttpData();
                    }

                }
            }
        });
    }

    private String urlBuilder(){

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.wanandroid.com/article/list/")
                .append(page).append("/json");
        String url= urlBuilder.toString();
        return url;
    }

}