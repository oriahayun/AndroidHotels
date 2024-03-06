package com.introduce.hotel;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryViewHolder>{
    List<Bitmap> Images;

    public ImageGalleryAdapter(List<Bitmap> images) {
        Images = images;
    }

    @NonNull
    @Override
    public ImageGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_gallery, parent, false);
        ImageGalleryViewHolder viewHolder = new ImageGalleryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryViewHolder holder, int position) {
        Bitmap image = Images.get(position);
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return Images.size();
    }
}
