package com.example.wanandroid;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import adapter.CollectAdapter;
import adapter.SearchResultAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pojo.CollectArticleInfo;
import pojo.SearchInfo;
import utils.OkHttpUtils;

public class MyCollectionActivity extends AppCompatActivity {

    private TextView back_textview;
    private CollectAdapter collectAdapter;
    private RecyclerView recyclerView_mycollection;
    private SharedPreferences sharedPreferences;
    private SharedPreferences cookies_sp;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 8) {
                String data = (String) msg.obj;
                Log.d("----", "获取收藏文章数据: " + data);
                CollectArticleInfo collectArticleInfo = new Gson().fromJson(data, CollectArticleInfo.class);
                if (collectArticleInfo.getData()==null){
                    Log.d("-----", " collectArticleInfo.getData()为空");

                    //提示用户没有收藏文章
                    Toast.makeText(MyCollectionActivity.this, "暂无收藏文章", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (collectArticleInfo.getData().getDatas()==null){
                    Log.d("-----", " searchInfo.getData().getDatas()为空");
                    return;
                }
                List<CollectArticleInfo.DataDTO.DatasDTO> datas = collectArticleInfo.getData().getDatas();
                collectAdapter.setCollectData(datas);
                Log.d("-----", "获取收藏文章数据成功");
            } else if (msg.what == 9) {
                String data = (String) msg.obj;
                Log.d("-----", "取消收藏文章接口返回数据: " + data);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_collection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //初始化SharedPreferences
        sharedPreferences = getSharedPreferences("collected_articles", MODE_PRIVATE);
        cookies_sp = getSharedPreferences("cookies", MODE_PRIVATE);

        back_textview = findViewById(R.id.back_mycollection);
        recyclerView_mycollection = findViewById(R.id.mycollection_recycler_view);
        recyclerView_mycollection.setLayoutManager(new LinearLayoutManager(this));
        collectAdapter = new CollectAdapter();

        //跳转网页点击事件
        collectAdapter.setOnItemClickListener(url -> {
            Intent intent = new Intent(MyCollectionActivity.this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        //取消收藏点击事件
        collectAdapter.setOnCollectClickListener(position -> {

            List<CollectArticleInfo.DataDTO.DatasDTO> dataList = collectAdapter.getCollectData();
            if (dataList != null && position < dataList.size()) {
                String articleId = String.valueOf(dataList.get(position).getId());
                cancelCollect(articleId);
                removeCollectedArticle(articleId);
                dataList.remove(position);
                collectAdapter.setCollectData(dataList);
                Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView_mycollection.setAdapter(collectAdapter);
        backListening();

//        Set<String> collectedIds = sharedPreferences.getStringSet("collected_ids", new HashSet<>());
//        if (collectedIds.isEmpty()) {
//            Toast.makeText(this, "暂无收藏文章", Toast.LENGTH_SHORT).show();
//            return;
//        }else {
//            getCollectedArticles();
//        }

        SharedPreferences sp = this.getSharedPreferences("collected_articles", Context.MODE_PRIVATE);

        //获取所有存储的键
        Map<String, ?> allEntries = sp.getAll();
        Set<String> collectedIds = new HashSet<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String id = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Boolean && (Boolean) value) {
                collectedIds.add(id);
            }
        }
        if (collectedIds.isEmpty()){
            Toast.makeText(this, "暂无收藏文章", Toast.LENGTH_SHORT).show();
            return;
        } else  {
            getCollectedArticles();
        }


    }


    //网络请求获取收藏文章数据
    //需要cookie
    //https://www.wanandroid.com/lg/collect/list/0/json
    private void getCollectedArticles() {
        String cookie = cookies_sp.getString("cookie", "");
        Log.d("-----", "请求收藏文章数据前 cookie: " + cookie);
        OkHttpClient client = OkHttpUtils.getInstance();
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/lg/collect/list/0/json")
                .addHeader("Cookie", cookies_sp.getString("cookie", ""))
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("----", "成功获取收藏文章数据 ");

                Message message = new Message();
                message.what = 8;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("----", "获取收藏文章数据失败");
            }
        });
    }

    //todo:调接口取消收藏
    //https://www.wanandroid.com/lg/uncollect/2805/json
    //
    //方法：POST
    //参数：
    //	id:拼接在链接上
    //	originId:列表页下发，无则为-1
    private void cancelCollect(String articleId){
        RequestBody body = RequestBody.create("", null);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://www.wanandroid.com/lg/uncollect/"+articleId+"/json")
                .addHeader("Cookie", cookies_sp.getString("cookie", ""))
                .post(body).build();
        Call call = client.newCall( request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("-----", "我的收藏界面 文章取消收藏成功");
                Message message = new Message();
                message.what = 9;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("-----", "我的收藏界面 文章取消收藏失败"+ e.getMessage());
            }
        });
    }



    //本地缓存中 移除收藏文章
    private void removeCollectedArticle(String articleId) {
        Set<String> collectedIds = new HashSet<>(sharedPreferences.getStringSet("collected_ids", new HashSet<>()));
        collectedIds.remove(articleId);
        sharedPreferences.edit().putStringSet("collected_ids", collectedIds).apply();
    }

    private void backListening() {
        back_textview.setOnClickListener(v -> finish());
    }
}