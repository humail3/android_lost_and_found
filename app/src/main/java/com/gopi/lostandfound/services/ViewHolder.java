package com.gopi.lostandfound.services;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gopi.lostandfound.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView tvItemType, tvItemName;
    private ItemClickListener itemClickListener;

    public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        tvItemType = itemView.findViewById(R.id.TVItemType);
        tvItemName = itemView.findViewById(R.id.TVItemName);
        this.itemClickListener = itemClickListener; // Assign itemClickListener
        itemView.setOnClickListener(this);
    }

    public void bind(Item item) {
        tvItemType.setText(item.getType());
        tvItemName.setText(item.getName());
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

