package com.example.wanandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.LoginInfo;
import utils.OkHttpUtils;

public class LoginActivity extends AppCompatActivity {

    //初始化控件
    private TextView gotoRegister_textview;
    private EditText username_edittext;
    private EditText password_edittext;
    private Button login_button;
    private LoginInfo loginInfo;
    private ImageView back_imageview;

    private String gettedUsername;
    private String gettedPassword;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==3){
                String data = (String) msg.obj;
                Log.d("---------", "登录接口返回数据: " + data);


                loginInfo = new Gson().fromJson(data, LoginInfo.class);

                if (loginInfo!=null&&loginInfo.getErrorCode()==0){
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Log.d("----", "登录成功 ");
                    SharedPreferences sp = getSharedPreferences("user_info",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    //nickname/username/public name???
                    editor.putString("username",loginInfo.getData().getUsername());
                    editor.apply();

                    finish();//跳转至首页
                }else{
                    //主线程显示错误信息
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "登录失败"+loginInfo.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //找到控件
        gotoRegister_textview = findViewById(R.id.goto_register);
        username_edittext = findViewById(R.id.login_username_edittext);
        password_edittext = findViewById(R.id.login_password_edittext);
        login_button = findViewById(R.id.login_button);
        back_imageview= findViewById(R.id.backto_myself_image);

        backListening();
        gotoRegisterActivityListening();
        loginListening();

    }




    //监听返回按钮点击事件
    private void backListening(){
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //传递参数
                intent.putExtra("page", "MyselfFragment");
                startActivity(intent);
            }
        });
    }

    //监听登录按钮点击事件
    private void loginListening(){
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得edittext文本
                gettedUsername = username_edittext.getText().toString();
                gettedPassword = password_edittext.getText().toString();
                //请求登录接口
                loginRequest();
            }
        });
    }


    //登录接口网络请求
    private void loginRequest(){
        //构建登录表单数据
        FormBody formBody = new FormBody.Builder()
                .add("username", gettedUsername)
                .add("password", gettedPassword)
                .build();

        //构建request对象
        Request request = new Request.Builder().url("https://www.wanandroid.com/user/login").post(formBody).build();

        //发送请求 异步
        OkHttpClient client = OkHttpUtils.getInstance();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("---------", "登录接口请求失败     e:"+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Log.d("---------", "登录接口请求成功      response: "+ response.body().string());

                String data = response.body().string();
                Message message = new Message();
                message.what=3;
                message.obj = data;
                mHandler.sendMessage(message);

                if (response.isSuccessful()) {
                    //获取cookie
                    List<String> cookies = response.headers("Set-Cookie");
                    Log.d("------", "登录获取到的cookies: " + cookies);
                    if (cookies != null && !cookies.isEmpty()) {
                        //保存cookie
                        //todo:考虑密码安全性
                        String allCookies = TextUtils.join(";", cookies);
                        SharedPreferences sp = getSharedPreferences("cookies", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("cookie", allCookies);
                        Log.d("------", "保存的cookie: " + allCookies);
                        editor.apply();
                        Log.d("---------", "登录成功");
                    }

                }else {
                    Log.d("---------", "登录失败");
                }

            }
        });

    }


    //跳转至注册activity
    private void gotoRegisterActivityListening(){
        gotoRegister_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}