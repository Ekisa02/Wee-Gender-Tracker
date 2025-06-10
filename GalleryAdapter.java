package com.Joseph.WEE_GEnder_Tracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private final List<GalleryItem> galleryItems;

    public GalleryAdapter(Context context, List<GalleryItem> galleryItems) {
        this.context = context;
        this.galleryItems = galleryItems;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem currentItem = galleryItems.get(position);

        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());

        loadImage(holder, currentItem);
    }

    private void loadImage(GalleryViewHolder holder, GalleryItem item) {
        try {
            String assetPath = item.getAssetPath();
            Log.d(TAG, "Attempting to load: " + assetPath);

            RequestOptions requestOptions = new RequestOptions()
                    .override(Target.SIZE_ORIGINAL)
                    .dontTransform();

            Glide.with(context)
                    .load("file:///android_asset/" + assetPath)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Load failed for: " + assetPath, e);
                            holder.galleryImageView.setImageResource(R.drawable.error_image);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Successfully loaded: " + assetPath);
                            return false;
                        }
                    })
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.galleryImageView);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + item.getAssetPath(), e);
            holder.galleryImageView.setImageResource(R.drawable.error_image);
        }
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView galleryImageView;
        TextView descriptionTextView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            galleryImageView = itemView.findViewById(R.id.galleryImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}