package View.Fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.wanandroid.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.PrimitiveIterator;

public class MyselfFragment extends Fragment {

    //初始化控件
    private Toolbar toolbar;
    private ShapeableImageView head_image;
    private TextView login_text;


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

        //点击事件监听
        //登录接口

    }

    private void loginListening(){
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转登录页面
            }
        });
    }

}
