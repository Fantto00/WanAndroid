package View.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.wanandroid.R;
import com.example.wanandroid.SearchActivity;
import com.example.wanandroid.WebViewActivity;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.ArticleListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pojo.ArticleInfo;
import pojo.BannerImageInfo;
import utils.OkHttpUtils;

public class HomeFragment extends Fragment {

    private ArticleListAdapter articleListAdapter;
    private RecyclerView recyclerView;
    private BannerImageInfo bannerImageInfo;
    private List<String> urls_image;
    private Banner banner;
    int page = 0;
    private boolean isLoading = false;
    private View headerView;
    private ImageView search_image;
    private ImageView collect_image;

    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (!isAdded()){
                    return;
                }
                if (msg.what == 1) {
                    String data = (String) msg.obj;
                    ArticleInfo articleInfo = new Gson().fromJson(data, ArticleInfo.class);
                    if (articleListAdapter != null) {
                        if (page == 1&&articleInfo.getData().getDatas()!=null) {
                            mergeLocalCollectedStatus(articleInfo.getData().getDatas());
                            articleListAdapter.setListData(articleInfo.getData().getDatas());
                        } else if(articleInfo.getData().getDatas()!=null){
                            Log.d("----------", "触底加载page:第" + page + "页");
                            articleListAdapter.addListData(articleInfo.getData().getDatas());
                        }
                        isLoading = false;
                    }
                } else if (msg.what == 2) {
                    String data = (String) msg.obj;
                    bannerImageInfo = new Gson().fromJson(data, BannerImageInfo.class);
                    setBannerImage();
                } else if (msg.what == 7) {
                    String data = (String) msg.obj;
                    Log.d("----", "收藏成功 收藏接口返回数据: " + data);

                } else if (msg.what==8) {
                    String data = (String) msg.obj;
                    Log.d("----", "取消收藏成功 删除接口返回数据: " + data);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("---", "尝试创建HomeFragment视图 ");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("---", "成功创建HomeFragment视图 尝试创建headerview视图");
        headerView = LayoutInflater.from(requireContext()).inflate(R.layout.header_banner, null);
        Log.d("---", "成功创建headerview视图 ");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("---", "初始化banner recyclerview ");
        banner = headerView.findViewById(R.id.banner);
        recyclerView = view.findViewById(R.id.recycler_view);
        search_image = view.findViewById(R.id.search_image);
        collect_image = view.findViewById(R.id.store_image);


        initRecyclerView();
        Log.d("----", "初始化RecyclerView成功 ");
        ScrollListening();
        Log.d("----", "滚动监听成功 ");

        initOnclickListener();
        Log.d("----", "收藏按钮和webView监听成功");
        setSearchButtonListener();
        Log.d("----", "搜索按钮监听成功 ");
        getHttpData();
        Log.d("----", "获取接口文章数据成功 ");

        getHttpBannerImage();
        Log.d("----", "获取接口轮播图片成功 ");
    }

    private void initOnclickListener() {
        //文章item点击事件 - 跳转到WebView
        articleListAdapter.setOnItemClickListener(url -> {
            Intent intent = new Intent(requireContext(), WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        //收藏按钮点击事件
        articleListAdapter.setOnCollectClickListener((articleId, position) -> {
            if (isArticleCollected(position)) {
                //取消收藏
                cancelCollectRequest(articleId, position);
            } else {
                //收藏
                postCollectRequest(articleId, position);
            }
        });
    }
    //根据文章ID判断收藏状态
    private boolean isArticleCollected(int position) {
        if (articleListAdapter != null) {
            List<ArticleInfo.DataDTO.DatasDTO> articles = articleListAdapter.getListData();
            if (articles != null && position >= 0 && position < articles.size()) {
                return articles.get(position).getCollect();
            }
        }
        return false;
    }




    //收藏某篇文章 参数id为文章id
    private void postCollectRequest(Integer id,int position) {
        //读取cookie
        SharedPreferences sp = getContext().getSharedPreferences("cookies", Context.MODE_PRIVATE);
        String cookie = sp.getString("cookie", "");
        if (cookie.isEmpty()) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        //根据文章id拼接url
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.wanandroid.com/lg/collect/").append(id).append("/json");
        String url = urlBuilder.toString();
        RequestBody body = RequestBody.create("", null); //空请求体
        OkHttpClient client= OkHttpUtils.getInstance();
        Request request = new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //更新UI状态
                    updateArticleCollectStatus(position, true);
                }
                Log.d("----", "收藏接口请求成功，response: ");
                Message message = new Message();
                message.what=7;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("----", "收藏接口请求失败 ");
            }
        });
    }

    //https://www.wanandroid.com/lg/uncollect_originId/2333/json
    //取消收藏某篇文章 参数id为文章id
    private void cancelCollectRequest(Integer id,int position) {
        //读取cookie
        SharedPreferences sp = getContext().getSharedPreferences("cookies", Context.MODE_PRIVATE);
        String cookie = sp.getString("cookie", "");
        if (cookie.isEmpty()) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        //根据文章id拼接url
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.wanandroid.com/lg/uncollect_originId/").append(id).append("/json");
        String url = urlBuilder.toString();
        OkHttpClient client= OkHttpUtils.getInstance();
        RequestBody body = RequestBody.create("", null); //空请求体
        Request request = new Request.Builder().url(url).addHeader("Cookie", cookie).post( body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //更新UI
                    updateArticleCollectStatus(position, false);
                }
                Log.d("---", "取消收藏接口请求成功");
                Message message = new Message();
                message.what = 8;
                message.obj = response.body().string();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("---", "取消收藏接口请求失败");
            }
        });

    }
    //收藏状态更新方法
    private void updateArticleCollectStatus(int position, boolean isCollected) {

        if (articleListAdapter != null) {
            int articleId = articleListAdapter.getListData().get(position).getId();
            if (isCollected) {
                saveCollectedArticle(articleId);
            } else {
                removeCollectedArticle(articleId);
            }
        }
        if (articleListAdapter != null) {
            requireActivity().runOnUiThread(() -> {
                articleListAdapter.updateCollectStatus(position, isCollected);
                String message = isCollected ? "收藏成功" : "取消收藏成功";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }

    //收藏成功后保存到本地
    private void saveCollectedArticle(int articleId) {
        SharedPreferences SoulPowerII = getContext().getSharedPreferences("collected_articles", Context.MODE_PRIVATE);
        SoulPowerII.edit().putBoolean(String.valueOf(articleId), true).apply();
        //你怎么知道我要去看SoulPowerII了
    }

    //取消收藏后从本地移除
    private void removeCollectedArticle(int articleId) {
        SharedPreferences SoulPowerII = getContext().getSharedPreferences("collected_articles", Context.MODE_PRIVATE);
        SoulPowerII.edit().remove(String.valueOf(articleId)).apply();
    }

    //初始化RecyclerView
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        articleListAdapter = new ArticleListAdapter(requireContext(), headerView);
        recyclerView.setAdapter(articleListAdapter);

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
                .setIndicator(new CircleIndicator(requireContext()));
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
                Log.d("----------", "get article,response:  "+response.body().toString());

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
                Log.d("---------------", "don't get article,e: "+e.toString());
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

    //给搜索按钮设置监听
    private void setSearchButtonListener(){
        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }
    private void mergeLocalCollectedStatus(List<ArticleInfo.DataDTO.DatasDTO> articles) {
        SharedPreferences sp = getContext().getSharedPreferences("collected_articles", Context.MODE_PRIVATE);
        for (ArticleInfo.DataDTO.DatasDTO article : articles) {
            boolean isCollected = sp.getBoolean(String.valueOf(article.getId()), article.getCollect());
            article.setCollect(isCollected);
        }
    }
}
