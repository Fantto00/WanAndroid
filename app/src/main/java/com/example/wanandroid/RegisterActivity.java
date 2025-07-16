package com.example.wanandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.RegisterInfo;
import utils.OkHttpUtils;

public class RegisterActivity extends AppCompatActivity {

    private TextView gotoLogin_textview;
    private EditText username_edittext;
    private EditText password_edittext;
    private EditText repassword_edittext;
    private Button register_button;
    private ImageView back_imageview;
    String gettedUsername;
    String gettedPassword;
    String gettedRepassword;
    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==4){
                String data  = (String) msg.obj;
                Log.d("---", "注册接口返回数据: "+ data);
                RegisterInfo registerInfo = new Gson().fromJson(data, RegisterInfo.class);
                //todo:注册成功后的逻辑
                Toast.makeText(RegisterActivity.this, "注册成功！去登录", Toast.LENGTH_SHORT).show();
                Log.d("----", "注册成功 ");
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //找到控件
        gotoLogin_textview = findViewById(R.id.goto_login);
        username_edittext = findViewById(R.id.register_username_edittext);
        password_edittext = findViewById(R.id.register_password_edittext);
        repassword_edittext = findViewById(R.id.register_confirmed_password_edittext);
        register_button = findViewById(R.id.register_button);
        back_imageview= findViewById(R.id.backto_myself_image_register);


        backListening();
        loginButtonListening();
        registerButtonListening();

    }

    //返回监听
    private void backListening(){
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("page", "MyselfFragment");
                startActivity(intent);
            }
        });
    }

    //todo:跳转登陆界面
    private void loginButtonListening(){
        gotoLogin_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    //获取内容并验重密码
    private void getEditTextContent() {
        gettedUsername = username_edittext.getText().toString();
        gettedPassword = password_edittext.getText().toString();
        gettedRepassword = repassword_edittext.getText().toString();
        //验重 两次密码一致
        if (!gettedPassword.equals(gettedRepassword)) {
            Toast.makeText(this, "两次密码不一致 请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
    }



        //todo:注册按钮监听
        private void registerButtonListening() {
            register_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取edittext内容
                    getEditTextContent();
                    //发送请求
                    registerRequest();
                }
            });
        }

        //todo: 注册接口网络请求
        private void registerRequest() {

            //拼接表单
            FormBody formbody = new FormBody.Builder()
                    .add("username", gettedUsername)
                    .add("password", gettedPassword)
                    .add("repassword", gettedRepassword)
                    .build();
            OkHttpClient client = OkHttpUtils.getInstance();
            Request request = new Request.Builder()
                    .url("https://www.wanandroid.com/user/register")
                    .post(formbody).build();

            //发送请求
            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.d("---", "注册接口请求成功 ");
                    String data = response.body().string();
                    Message message = new Message();
                    message.what = 4;
                    message.obj = data;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("---", "注册接口请求失败 e: " + e.getMessage());
                }
            });
        }


    }

