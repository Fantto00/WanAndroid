package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;

import java.util.List;

import pojo.SettingItem;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.Myholder> {


    private List<SettingItem> settingItems;

    private Context context;

    public SettingItemAdapter(List<SettingItem> settingItems, Context context) {
        this.settingItems = settingItems;
        this.context = context;
    }

    private MyselfMenuItemAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MyselfMenuItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_withtextview, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        SettingItem settingItem = settingItems.get(position);
        holder.itemTitle.setText(settingItem.getItemTitle());
        holder.itemNavigation.setText(settingItem.getItemNavigation());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingItems.size();
    }

    class Myholder extends RecyclerView.ViewHolder{

        //找到控件
        TextView itemTitle;
        TextView itemNavigation;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            itemTitle=itemView.findViewById(R.id.item_title);
            itemNavigation=itemView.findViewById(R.id.item_navigation);
        }




    }
}
