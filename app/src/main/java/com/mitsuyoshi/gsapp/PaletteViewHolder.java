package com.mitsuyoshi.gsapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PaletteViewHolder extends RecyclerView.ViewHolder {

    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;

    public PaletteViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.title);
        contentText = (TextView) itemView.findViewById(R.id.content);
        card = (CardView) itemView;
    }
}