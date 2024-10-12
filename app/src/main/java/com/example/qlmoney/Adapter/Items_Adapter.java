package com.example.qlmoney.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.Database.Item;
import com.example.qlmoney.R;

import java.util.List;

public class Items_Adapter extends RecyclerView.Adapter<Items_Adapter.ItemsViewHolder> {
    private List<Item> itemList;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int itemId);
    }

    public Items_Adapter() {
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.font_item_thuchi2, parent, false);
        return new ItemsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        // Lấy item hiện tại trong danh sách
        Item currentItem = itemList.get(position);

        // Đặt giá trị ngày tháng vào TextView
        TextView textViewItemDate = holder.itemView.findViewById(R.id.textView_item_date);
        textViewItemDate.setText(currentItem.getDate()); // Thay "getDate()" bằng phương thức lấy ngày tháng của item

        // Các bước khác để đặt giá trị khác của item (hình ảnh, tiêu đề, giá trị, v.v.)
        holder.bind(currentItem);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgItem;
        private TextView nameItem;
        private TextView giaItem;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.image_item);
            nameItem = itemView.findViewById(R.id.textView_item);
            giaItem = itemView.findViewById(R.id.textView_gia);
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Item item) {
            imgItem.setImageResource(item.getImg());
            nameItem.setText(item.getName());
            giaItem.setText(item.getValue());
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    onItemClickListener.onItemClick(itemList.get(position).getId_Item());
            }
        }
    }
}
