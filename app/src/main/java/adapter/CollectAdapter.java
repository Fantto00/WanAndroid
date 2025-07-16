package adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;

import java.util.List;

import pojo.CollectArticleInfo;


public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.MyHolder> {

    private List<CollectArticleInfo.DataDTO.DatasDTO> mDatasDTO;
    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public interface OnCollectClickListener {
        void onCollectClick(int position);
    }
    private OnItemClickListener mItemClickListener;
    private OnCollectClickListener mCollectClickListener;

    //对外的获取数据的方法
    public List<CollectArticleInfo.DataDTO.DatasDTO> getCollectData(){
        return mDatasDTO;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
    public void setOnCollectClickListener(OnCollectClickListener listener) {
        this.mCollectClickListener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //绑定数据
        CollectArticleInfo.DataDTO.DatasDTO datasDTO = mDatasDTO.get(position);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            holder.title.setText(Html.fromHtml(datasDTO.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        }else{
            holder.title.setText(Html.fromHtml(datasDTO.getTitle()));
        }

        //holder.title.setText(datasDTO.getTitle());



        holder.author_name.setText(datasDTO.getAuthor());
        holder.date.setText(datasDTO.getNiceDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null && datasDTO.getLink() != null) {
                    mItemClickListener.onItemClick(datasDTO.getLink());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatasDTO == null ? 0 : mDatasDTO.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        //找到控件

        TextView title ;
        TextView author_name;
        TextView date;
        ImageView Icon;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date);

        }
    }

    //对外的设置数据方法
    public void setCollectData(List<CollectArticleInfo.DataDTO.DatasDTO> list){
        this.mDatasDTO = list;
        notifyDataSetChanged();
    }
    //对外的追加数据方法
    public void addCollectData(List<CollectArticleInfo.DataDTO.DatasDTO> list) {
        if (list != null) {
            this.mDatasDTO.addAll(list);
            notifyDataSetChanged();
        }
    }
}
