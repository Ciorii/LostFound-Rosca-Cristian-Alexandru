package com.example.lostfound.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.R;
import com.example.lostfound.models.LostObject;
import com.example.lostfound.activities.PostDetailActivity;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<LostObject> list;

    public PostAdapter(List<LostObject> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LostObject obj = list.get(position);
        holder.tvTitle.setText(obj.getTitle());
        holder.tvCategory.setText(obj.getCategory());
        holder.tvDescription.setText(obj.getDescription());
        holder.tvAddress.setText(obj.getAddress());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("title", obj.getTitle());
            intent.putExtra("description", obj.getDescription());
            intent.putExtra("category", obj.getCategory());
            intent.putExtra("address", obj.getAddress());
            intent.putExtra("latitude", obj.getLatitude());
            intent.putExtra("longitude", obj.getLongitude());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDescription, tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
