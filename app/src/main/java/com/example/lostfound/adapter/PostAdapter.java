package com.example.lostfound.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostfound.R;
import com.example.lostfound.activities.PostDetailActivity;
import com.example.lostfound.models.LostObject;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {

    private final List<LostObject> list;
    private final List<LostObject> listFull;

    public PostAdapter(List<LostObject> list) {
        this.list = list;
        this.listFull = new ArrayList<>(list);
    }

    public void updateFullList(List<LostObject> newList) {
        listFull.clear();
        listFull.addAll(newList);
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

        if (obj.getImageUrls() != null && !obj.getImageUrls().isEmpty()) {
            holder.ivThumb.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(obj.getImageUrls().get(0))
                    .centerCrop()
                    .into(holder.ivThumb);
        } else {
            holder.ivThumb.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("id", obj.getId());
            intent.putExtra("title", obj.getTitle());
            intent.putExtra("description", obj.getDescription());
            intent.putExtra("category", obj.getCategory());
            intent.putExtra("address", obj.getAddress());
            intent.putExtra("latitude", obj.getLatitude());
            intent.putExtra("longitude", obj.getLongitude());
            intent.putStringArrayListExtra("imageUrls",
                    obj.getImageUrls() == null ? null : new java.util.ArrayList<>(obj.getImageUrls()));
            intent.putExtra("contactEmail", obj.getContactEmail());
            intent.putExtra("contactPhone", obj.getContactPhone());
            intent.putExtra("ownerUid", obj.getOwnerUid());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LostObject> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(listFull);
            } else {
                String query = constraint.toString().toLowerCase().trim();
                for (LostObject obj : listFull) {
                    if (obj.getTitle() != null && obj.getTitle().toLowerCase().contains(query)) {
                        filtered.add(obj);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDescription, tvAddress;
        ImageView ivThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}