package com.example.pandora.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Category;
import com.example.pandora.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnItemClickListener listener;
    private Context context;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public void setFilteredList(List<Category> filteredList) {
        this.categoryList = filteredList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(List<Category> categories) {
        this.categoryList = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.nameTextView.setText(category.getName());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.categoryName);
        }
    }

    public void updateData(List<Category> newCategoryList) {
        this.categoryList.clear();
        this.categoryList.addAll(newCategoryList);
        notifyDataSetChanged();
    }
}