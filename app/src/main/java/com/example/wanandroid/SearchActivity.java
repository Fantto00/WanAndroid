package com.example.wanandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.logging.Logger;

import adapter.SearchResultAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Http2Reader;
import pojo.SearchHotKeyInfo;
import pojo.SearchInfo;
import utils.OkHttpUtils;

public class SearchActivity extends AppCompatActivity {
    private ChipGroup chipGroup;
    private SearchView searchView;
    private ImageView search_btn;
    private TextView back_textview;
    private int page = 0;
    private Handler mHandler;
    private String gettedSearchData;
    private SearchHotKeyInfo searchHotKeyInfo;
    private TextView tvHotSearchTitle;
    private RecyclerView searchResult;
    private SearchResultAdapter searchResultAdapter;
    private boolean isLoading = false;
    private String currentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5){
                    searchHotKeyInfo = new Gson().fromJson((String) msg.obj, SearchHotKeyInfo.class);
                    Log.d("----", "获取搜索热词成功 ");
                    addHotKeyChips();
                } else if (msg.what == 6) {
                    gettedSearchData = (String) msg.obj;
                    Log.d("-----", "搜索接口返回数据: " + gettedSearchData);
                    parseSearchData();
                }
            }
        };


        //找到控件
        tvHotSearchTitle = findViewById(R.id.tv_hot_search_title);
        chipGroup = findViewById(R.id.chip_group_hot_search);
        searchView = findViewById(R.id.searchview);
        search_btn = findViewById(R.id.btn_search);
        back_textview = findViewById(R.id.tv_back);
        searchResult = findViewById(R.id.search_result_recycler_view);

        initSearchView();
        initRecyclerView();
        backListening();
        scrollListening();
        getHttpSearchHotKey();
        searchButtonListening();


    }

    //添加热词到ChipGroup
    private void addHotKeyChips(){
        if (searchHotKeyInfo != null && searchHotKeyInfo.getData() != null) {
            chipGroup.removeAllViews(); //清除现有的chips

            for (SearchHotKeyInfo.DataDTO hotKey : searchHotKeyInfo.getData()) {
                Chip chip = new Chip(this);
                chip.setText(hotKey.getName());
                chip.setClickable(true);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(getResources().getColor(R.color.black));
                chip.setOnClickListener(v -> performSearch(hotKey.getName()));
                chipGroup.addView(chip);
            }
        }
    }

    //传入文本实现搜索
    //api:https://www.wanandroid.com/article/query/0/json
    //方法：POST
    //参数：
    //页码：拼接在链接上，从0开始。
    //k ： 搜索关键词
    private void performSearch(String query) {
        if (!query.equals(currentQuery)) {
            page = 0;
            currentQuery = query;
            searchResultAdapter.setSearchResultData(null);
        }
        searchView.setQuery(query, false);
        Log.d("SearchActivity", "执行搜索: " + query + " page:" + page);
        isLoading = true;
        //发送搜索请求post
        //构建请求体
        FormBody formBody = new FormBody.Builder().
                add("k", query).build();
        OkHttpClient client = OkHttpUtils.getInstance();
        Request request = new Request.Builder().url(urlbuilder()).post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("-----", "搜索接口请求成功");
                Message message = new Message();
                message.what = 6;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("-----", "搜索接口请求成功");
            }
        });

    }
    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //对接口返回的数据进行解析并进行渲染
    private void parseSearchData(){
        SearchInfo searchInfo = new Gson().fromJson(gettedSearchData, SearchInfo.class);
        if (searchInfo != null && searchInfo.getData() != null && searchInfo.getData().getDatas() != null) {
            //隐藏热搜
            tvHotSearchTitle.setVisibility(View.GONE);
            chipGroup.setVisibility(View.GONE);
            //显示recyclerview
            searchResult.setVisibility(View.VISIBLE);
            if (page==0){
                searchResultAdapter.setSearchResultData(searchInfo.getData().getDatas());
            } else {
                searchResultAdapter.addSearchResultData(searchInfo.getData().getDatas());
            }
            page++;
        }
        isLoading = false;

    }

    private void initRecyclerView() {
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchResultAdapter = new SearchResultAdapter();
        //点击事件监听器
        searchResultAdapter.setOnItemClickListener(url -> {
            Intent intent = new Intent(SearchActivity.this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });
        searchResult.setAdapter(searchResultAdapter);
    }




    private String urlbuilder(){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.wanandroid.com/article/query/").append(page).append("/json");
        return urlBuilder.toString();
    }

    //发送请求获取搜索热词
    private void getHttpSearchHotKey() {
        OkHttpClient client = OkHttpUtils.getInstance();
        Request request = new Request.Builder().url("https://www.wanandroid.com//hotkey/json").get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("----", "获取网络搜索热词失败: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("----", "获取搜索热词成功");
                String data = response.body().string();
                Message message = new Message();
                message.what=5;
                message.obj = data;
                mHandler.sendMessage(message);
            }
        });
    }

    //返回按钮监听
    private void backListening(){
        back_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //搜素按钮监听
    private void searchButtonListening(){
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框内容
                String query = searchView.getQuery().toString();
                //发送请求
                if (!query.isEmpty()){
                    performSearch(query);
                }
            }
        });
    }

    //滚动监听

    private void scrollListening() {
        searchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    if (lastVisibleItemPosition >= itemCount - 3 && dy > 0) {
                        performSearch(currentQuery);
                    }
                }
            }
        });
    }
}