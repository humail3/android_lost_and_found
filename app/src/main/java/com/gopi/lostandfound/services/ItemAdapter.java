package com.gopi.lostandfound.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gopi.lostandfound.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private ArrayList<Item> itemList;
    private ItemClickListener itemClickListener;

    public ItemAdapter(Context context, ArrayList<Item> itemList, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view, itemClickListener); // Pass itemClickListener
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Item getItem(int position) {
        return itemList.get(position);
    }
}
