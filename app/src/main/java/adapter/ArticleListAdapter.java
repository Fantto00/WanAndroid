package adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;

import java.util.ArrayList;
import java.util.List;

import pojo.ArticleInfo;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.MyHolder> {


    //两种item类型
    private static final int TYPE_CONTENT = 0;//文章内容0
    private static final int TYPE_HEAD = 1;//头部banner1
    //所需数据
    private List<ArticleInfo.DataDTO.DatasDTO> mDataDTOS ;

    //上下文
    private Context mContext;

    //头部bannerview
    private View mHeadView;

    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public interface OnCollectClickListener {
        void onCollectClick(int articleId,int  position);
    }

    private OnItemClickListener mItemClickListener;
    private OnCollectClickListener mCollectClickListener;

    //点击事件监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnCollectClickListener(OnCollectClickListener listener) {
        this.mCollectClickListener = listener;
    }

    public ArticleListAdapter(Context mContext,View headerView) {
        this.mContext = mContext;
        this.mHeadView = headerView;
        this.mDataDTOS = new ArrayList<>();
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

    public List<ArticleInfo.DataDTO.DatasDTO> getListData() {
        return mDataDTOS;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView title ;
        TextView author_name;
        TextView date;
        ImageView store_image;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //找到控件
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date);
            store_image = itemView.findViewById(R.id.store_image);
        }
    }
    @NonNull
    @Override
    public ArticleListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD&&mHeadView!=null){
            return new MyHolder(mHeadView);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListAdapter.MyHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEAD) {
            Log.d("----", "position为0 头部轮播图  ");
            return;
        }
        //position = position-1;
        //绑定数据 在集合里面取
        if (mDataDTOS != null && position > 0) {
            ArticleInfo.DataDTO.DatasDTO datasDTO = mDataDTOS.get(position - 1);
            Log.d("---", "获取文章集合索引为 " + (position - 1) + " 的数据: " + datasDTO.getTitle());


            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                holder.title.setText(Html.fromHtml(datasDTO.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            }else{
                holder.title.setText(Html.fromHtml(datasDTO.getTitle()));
            }



            holder.author_name.setText(datasDTO.getAuthor());
            holder.date.setText(datasDTO.getNiceDate());

            //设置收藏图标状态
            holder.store_image.setImageResource(datasDTO.getCollect() ? R.drawable.collect_filled : R.drawable.collect_empty);

            holder.itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(datasDTO.getLink());
                }
            });

            holder.store_image.setOnClickListener(v -> {
                if (mCollectClickListener != null) {
                    mCollectClickListener.onCollectClick(datasDTO.getId(),position-1);
                }
            });

        }
    }

    public void updateCollectStatus(int position, boolean isCollected) {
        if (mDataDTOS != null && position >= 0 && position < mDataDTOS.size()) {
            mDataDTOS.get(position).setCollect(isCollected);
            notifyItemChanged(position + 1); // +1 有header
        }
    }


    @Override
    public int getItemCount() {
        return mDataDTOS.size()+1;
    }


}
