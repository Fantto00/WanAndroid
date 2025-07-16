package com.example.wanandroid;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import View.Fragment.HomeFragment;
import View.Fragment.MyselfFragment;
import View.Fragment.QAFragment;
import View.Fragment.SystemFragment;
import adapter.ArticleListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.ArticleInfo;
import pojo.BannerImageInfo;

public class MainActivity extends AppCompatActivity {

    //初始化底部导航
    private BottomNavigationView navView;

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

        navView=findViewById(R.id.bottom_navigation);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        MyselfFragment myselfFragment = new MyselfFragment();
//        transaction.add(R.id.fragment_container,myselfFragment);
//        transaction.commit();


        initBottomNavigationView();

        //检查是否需要显示MyselfFragment
        String showFragment = getIntent().getStringExtra("page");
        if ("MyselfFragment".equals(showFragment)) {
            navView.setSelectedItemId(R.id.bottom_navigation_myself);
        } else {
            //默认选中首页
            navView.setSelectedItemId(R.id.bottom_navigation_home);
        }

    }


    //初始化底部导航
    private void initBottomNavigationView(){
        navView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_navigation_home){
                Log.d("----", "new了HomeFragment");
                fragment = new HomeFragment();
            }else if(itemId == R.id.bottom_navigation_QA){
                fragment = new QAFragment();
                Log.d("----", "new了QAFragment");
            }else if(itemId == R.id.bottom_navigation_system){
                fragment = new SystemFragment();
                Log.d("----", "new了SystemFragment");
            }else if(itemId == R.id.bottom_navigation_myself){
                fragment = new MyselfFragment();
                Log.d("----", "new了MyselfFragment");
            }

            if (fragment!=null){
                Log.d("-----", "尝试将fragment替换到容器 ");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                Log.d("-----", "成功将fragment替换到容器 ");
                return true;
            }
            Log.d("-----", "将fragment替换到容器失败  ");
            return false;

        });
        //默认选中第一个
        Log.d("----", "尝试选中第一个 home 首页");
        navView.setSelectedItemId(R.id.bottom_navigation_home);
    }

}