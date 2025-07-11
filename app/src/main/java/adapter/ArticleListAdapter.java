package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import pojo.ArticleInfo;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.MyHolder> {


    //两种item类型
    private static final int TYPE_CONTENT = 0;//文章内容0
    private static final int TYPE_HEAD = 1;//头部banner1
    //所需数据
    private List<ArticleInfo.DataDTO.DatasDTO> mDataDTOS = new ArrayList<>();

    //上下文
    private Context mContext;

    //头部bannerview
    private View mHeadView;

    public ArticleListAdapter(Context mContext,View headerView) {
        this.mContext = mContext;
        mHeadView = headerView;
    }

    //获取item类型
    @Override
    public int getItemViewType(int position) {
        if (position ==0&&mHeadView!=null){
            return TYPE_HEAD;
        }else {
            return TYPE_CONTENT;
        }
    }

    //对外的设置数据的方法
    public void setListData(List<ArticleInfo.DataDTO.DatasDTO> list){
        this.mDataDTOS = list;
        notifyDataSetChanged();
    }

    //对外的追加数据的方法
    public void addListData(List<ArticleInfo.DataDTO.DatasDTO> list){
        if (list!=null){
            this.mDataDTOS.addAll(list);
            notifyDataSetChanged();
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    class MyHolder extends RecyclerView.ViewHolder{

        TextView title ;
        TextView author_name;
        TextView date;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //找到控件
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date);
        }
    }
    @NonNull
    @Override
    public ArticleListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD){
            return new MyHolder(mHeadView);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListAdapter.MyHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEAD){
            return;
        }
        position = position-1;
        //绑定数据 在集合里面取
        ArticleInfo.DataDTO.DatasDTO datasDTO = mDataDTOS.get(position);
        holder.title.setText(datasDTO.getTitle());
        holder.author_name.setText(datasDTO.getAuthor());
        holder.date.setText(datasDTO.getNiceDate());
    }

    @Override
    public int getItemCount() {
        return mDataDTOS.size()+1;
    }


}
