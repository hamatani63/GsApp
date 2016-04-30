package com.mitsuyoshi.gsapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class MessageRecordHolder extends RecyclerView.ViewHolder {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;

    public MessageRecordHolder(View itemView) {
        super(itemView);
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
        contentText = (TextView) itemView.findViewById(R.id.content);
        card = (CardView) itemView;
    }
}