package adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;

import java.util.List;

import pojo.ArticleInfo;
import pojo.SearchInfo;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.Myholder> {


    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public interface OnCollectClickListener {
        void onCollectClick(int position);
    }

    private OnItemClickListener mItemClickListener;
    private OnCollectClickListener mCollectClickListener;
    private List<SearchInfo.DataDTO.DatasDTO> mDatasDTO;
    //对外提供数据获取方法
    public List<SearchInfo.DataDTO.DatasDTO> getSearchResultData() {
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
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        //绑定数据
        SearchInfo.DataDTO.DatasDTO datasDTO = mDatasDTO.get(position);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            holder.title.setText(Html.fromHtml(datasDTO.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        }else{
            holder.title.setText(Html.fromHtml(datasDTO.getTitle()));
        }

        //holder.title.setText(datasDTO.getTitle());


        holder.author_name.setText(datasDTO.getAuthor());
        holder.date.setText(datasDTO.getNiceDate());

        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null && datasDTO.getLink() != null) {
                mItemClickListener.onItemClick(datasDTO.getLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatasDTO == null ? 0 : mDatasDTO.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        TextView title ;
        TextView author_name;
        TextView date;
        //找到控件
        public Myholder(@NonNull View itemView) {
            super(itemView);

            //找到控件
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date);
        }
    }

    //对外的设置数据的方法
    public void setSearchResultData(List<SearchInfo.DataDTO.DatasDTO> list){
        this.mDatasDTO = list;
        notifyDataSetChanged();
    }
    //对外的追加数据的方法
    public void addSearchResultData(List<SearchInfo.DataDTO.DatasDTO> list){
        if (list!=null){
            this.mDatasDTO.addAll(list);
            notifyDataSetChanged();
        }
    }




}
