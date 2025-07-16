package View.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.LoginActivity;
import com.example.wanandroid.MyCollectionActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.SettingActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import adapter.MyselfMenuItemAdapter;
import pojo.Menuitem_Myself;


public class MyselfFragment extends Fragment {

    //初始化控件
    private Toolbar toolbar;
    private ShapeableImageView head_image;
    private TextView login_text;
    private RecyclerView recyclerView;
    private List<Menuitem_Myself> menuitems ;
    private MyselfMenuItemAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //找到控件
        toolbar = view.findViewById(R.id.toolbar);
        head_image = view.findViewById(R.id.head_image);
        login_text = view.findViewById(R.id.login_textview);
        recyclerView = view.findViewById(R.id.recyclerview_myself);

        initRecyclerView();

        //检查登陆状态 更新textview
        checkLogin();



    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面恢复时刷新登录状态
        SharedPreferences sp = requireActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", null);
        if(username != null) {
            login_text.setText(username);
            login_text.setOnClickListener(null);
        }
    }

    //初始化recyclerview数据和适配器
    private void initRecyclerView() {
        menuitems = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        menuitems.add(new Menuitem_Myself(R.drawable.my_accumulate_score,"我的积分"));
        menuitems.add(new Menuitem_Myself(R.drawable.my_sharing,"我的分享"));
        menuitems.add(new Menuitem_Myself(R.drawable.my_store,"我的收藏"));
        menuitems.add(new Menuitem_Myself(R.drawable.my_book,"我的书签"));
        menuitems.add(new Menuitem_Myself(R.drawable.my_reading_history,"阅读历史"));
        menuitems.add(new Menuitem_Myself(R.drawable.my_setting,"系统设置"));

        adapter = new MyselfMenuItemAdapter(getContext(), menuitems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            switch(position) {
                case 0: //我的积分
                    Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 1: //我的分享
                    Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 2: //我的收藏
                    //Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    //跳转至收藏页面
                    Intent intent =new Intent(getActivity(), MyCollectionActivity.class);
                    startActivity(intent);
                    break;
                case 3: //我的书签
                    Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 4: //阅读历史
                    Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    break;
                case 5: //系统设置
                    //Toast.makeText(getContext(), "还妹做好", Toast.LENGTH_SHORT).show();
                    //跳转至设置activity
                    Intent intent_setting =new Intent(getActivity(), SettingActivity.class);
                    Log.d("----", "准备跳转至系统设置activity");
                    startActivity(intent_setting);
                    break;
            }
        });
    }

    private void loginListening(){
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转登录页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //检查登陆状态
    private void checkLogin(){
        SharedPreferences sp = requireActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", null);
        if(username!=null){
            //已经登陆
            login_text.setText(username);
        }else{
            login_text.setText("去登录");
            loginListening();
        }
    }



}
