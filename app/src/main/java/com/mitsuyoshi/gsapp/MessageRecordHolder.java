package com.mitsuyoshi.gsapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

public class MessageRecordHolder extends RecyclerView.ViewHolder {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;
    private Context mContext;

    public MessageRecordHolder(Context context, View itemView) {
        super(itemView);
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
        contentText = (TextView) itemView.findViewById(R.id.content);
        card = (CardView) itemView;

        mContext = context;
        card.setOnClickListener(clickListener);
        card.setTag(this);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MessageRecordHolder vholder = (MessageRecordHolder) view.getTag();
            int position = vholder.getPosition();
            Toast.makeText(mContext, "This is position " + position, Toast.LENGTH_SHORT).show();
        }
    };
}