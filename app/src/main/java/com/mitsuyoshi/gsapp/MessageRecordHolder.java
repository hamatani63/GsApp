package com.mitsuyoshi.gsapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

public class MessageRecordHolder extends RecyclerView.ViewHolder {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView content1Text;
    protected TextView content2Text;
    protected CardView card;
    protected Button button;
    //protected TextView button;

    public MessageRecordHolder(View itemView) {
        super(itemView);
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
        content1Text = (TextView) itemView.findViewById(R.id.shopCatch);
        content2Text = (TextView) itemView.findViewById(R.id.shopWebsite);
        button = (Button) itemView.findViewById(R.id.mapButton);
        card = (CardView) itemView;
//        //リスナー実装：Toast表示用
//        image.setOnClickListener(clickListener);
//        content2Text.setTag(this);
    }

//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(final View view) {
//            //Toast表示
//            MessageRecordHolder vholder = (MessageRecordHolder) view.getTag();
//            int position = vholder.getPosition();
//            Toast.makeText(view.getContext(), "This is position " + position, Toast.LENGTH_SHORT).show();
//        }
//    };

}