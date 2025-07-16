package com.example.wanandroid;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import View.Fragment.MyselfFragment;
import adapter.SettingItemAdapter;
import pojo.SettingItem;

public class SettingActivity extends AppCompatActivity {

    //初始化
    private RecyclerView recyclerView;
    private SettingItemAdapter settingItemAdapter;
    private List<SettingItem> settingItems;
    private TextView backto_myself_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.setting_recyclerview);
        backto_myself_textview =findViewById(R.id.back_to_myself_textview);

        backListening();

        Log.d("-----", "成功跳转至设置界面");
        initRecyclerView();


    }

    //初始化recyclerview和适配器
    private void initRecyclerView() {
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("主题模式","亮色>"));
        settingItems.add(new SettingItem("网页拦截","不拦截>"));
        settingItems.add(new SettingItem("清除缓存",null));
        settingItems.add(new SettingItem("关于我们",null));
        settingItems.add(new SettingItem("隐私政策",null));

        //检查登陆状态
        if (checkLogin()){
            //已经登录
            settingItems.add(new SettingItem("退出登录",null));
        }
        else {
            settingItems.add(new SettingItem(null,null));
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        settingItemAdapter = new SettingItemAdapter( settingItems, this);
        recyclerView.setAdapter(settingItemAdapter);


        settingItemAdapter.setOnItemClickListener(position -> {
            switch (position) {
                case 0: //主题模式
                    //todo:进入主题设置activity


                    break;

                case 1: //网页拦截
                    Toast.makeText(this, "还妹做好", Toast.LENGTH_SHORT).show();
                    break;

                case 2: //清除缓存
                    Toast.makeText(this, "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case
                    3: //关于我们
                    Toast.makeText(this, "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 4: //隐私政策
                    Toast.makeText(this, "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 5: //todo:退出登录
                    //清除用户信息
                    SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                    //返回我的页面
                    Intent intent = new Intent(this, MainActivity.class);
                    //给mainActivity传递参数
                    intent.putExtra("page", "MyselfFragment");
                    //清除任务栈中的其他活动
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;

            }
        });

    }
    //返回键的点击事件监听
    private void backListening(){
        backto_myself_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                //给mainActivity传递参数
                intent.putExtra("page", "MyselfFragment");
                startActivity(intent);
                finish();
            }
        });
    }


    //检查登陆状态
    //检查登陆状态
    private Boolean checkLogin(){
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", null);
        if(username!=null){
            //已经登陆
            return true;
        }else{
            return false;
        }
    }

}