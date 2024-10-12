package com.example.qlmoney.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.qlmoney.Database.Phanloai_Nganh;
import com.example.qlmoney.R;

import java.util.List;

public class Phanloai_Adapter extends RecyclerView.Adapter<Phanloai_Adapter.UserViewHolder> {


    private List<Phanloai_Nganh> phanloaiList;
    private OnPhanloaiClickListener onPhanloaiClickListener;


    public interface OnPhanloaiClickListener {
        void onPhanloaiClick(int position);
    }

    public Phanloai_Adapter() {
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View phanloaiView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.font_phan_loai, parent, false);
        return new UserViewHolder(phanloaiView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Phanloai_Nganh user = phanloaiList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return phanloaiList.size();    }



    public void setPhanloaiList(List<Phanloai_Nganh> userList) {
        this.phanloaiList = userList;
        notifyDataSetChanged();
    }



    public void setOnPhanloaiClickListener(OnPhanloaiClickListener listener) {
        this.onPhanloaiClickListener = listener;
    }



    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgItem;
        private TextView nameItem;
        private TextView giaItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.image_item);
            nameItem = itemView.findViewById(R.id.textView_item);
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Phanloai_Nganh item) {
            imgItem.setImageResource(item.getImgphanloainganh());
            nameItem.setText(item.getTenphanloainganh());
        }

        @Override
        public void onClick(View v) {
            if (onPhanloaiClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onPhanloaiClickListener.onPhanloaiClick(position);
                }
            }
        }
    }
}