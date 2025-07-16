package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;

import java.util.List;

import pojo.Menuitem_Myself;

public class MyselfMenuItemAdapter extends RecyclerView.Adapter<MyselfMenuItemAdapter.Myholder> {


    private List<Menuitem_Myself> menuItemList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public MyselfMenuItemAdapter(Context context, List<Menuitem_Myself> menuItemList) {
        this.context = context;
        this.menuItemList = menuItemList;
    }
    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myself, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        Menuitem_Myself menuitem = menuItemList.get(position);
        //设置图标
        holder.imageView_myself.setImageResource(menuitem.getImageResId());
        //设置标题
        holder.textView_myself.setText(menuitem.getTitle());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        //找到控件
        ImageView imageView_myself;
        TextView textView_myself;
        TextView arrow_textview_myself;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            imageView_myself=itemView.findViewById(R.id.item_myself_imageview);
            textView_myself = itemView.findViewById(R.id.menuitem_title);
            arrow_textview_myself = itemView.findViewById(R.id.arrow_item_myself_textview);
        }
    }
}
