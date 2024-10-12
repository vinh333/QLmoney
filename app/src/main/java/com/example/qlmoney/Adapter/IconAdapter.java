package com.example.qlmoney.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.R;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
    private List<Integer> iconIds;
    private int selectedIconPosition = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;



    public IconAdapter(List<Integer> iconIds, OnItemClickListener itemClickListener) {
        this.iconIds = iconIds;
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon, parent, false);
        return new IconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, final int position) {
        final int iconId = iconIds.get(position);
        holder.iconImageView.setImageResource(iconId);

        if (position == selectedIconPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_icon_background);
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconIds.size();
    }

    public void setSelectedIconPosition(int position) {
        selectedIconPosition = position;
        notifyDataSetChanged(); // Cập nhật lại adapter sau khi thay đổi giá trị
    }
    public class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        setSelectedIconPosition(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public int getSelectedIconPosition() {
        return selectedIconPosition;
    }
}
