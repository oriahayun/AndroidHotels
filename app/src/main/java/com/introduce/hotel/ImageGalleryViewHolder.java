package com.introduce.hotel;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ImageGalleryViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    public ImageView imageView;
    public ImageGalleryViewHolder(@NonNull View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.imageGaleryCardView);
        imageView = itemView.findViewById(R.id.cityImageView);
    }
}
