package com.example.pandora.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Class.Location;
import com.example.pandora.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> locationList;
    private OnItemClickListener listener;
    private Context context;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public void setFilteredList(List<Location> filteredList) {
        this.locationList = filteredList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LocationAdapter(List<Location> locationList) {
        this.locationList =  locationList;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new LocationViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(LocationViewHolder  holder, int position) {
        Location location = locationList.get(position);
        holder.nameTextView.setText(location.getName());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        public LocationViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.categoryName);
        }
    }

    public void updateData(List<Location> newCategoryList) {
        this.locationList.clear();
        this.locationList.addAll(newCategoryList);
        notifyDataSetChanged();
    }
}