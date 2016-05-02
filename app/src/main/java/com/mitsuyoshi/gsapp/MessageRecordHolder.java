package com.mitsuyoshi.gsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

public class MessageRecordHolder extends RecyclerView.ViewHolder {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView content1Text;
    protected TextView content2Text;
    protected CardView card;
    private Context mContext;

    public MessageRecordHolder(Context context, View itemView) {
        super(itemView);
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
        content1Text = (TextView) itemView.findViewById(R.id.shopCatch);
        content2Text = (TextView) itemView.findViewById(R.id.shopWebsite);
        card = (CardView) itemView;

        mContext = context;
        content2Text.setOnClickListener(clickListener);
        content2Text.setTag(this);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MessageRecordHolder vholder = (MessageRecordHolder) view.getTag();
            int position = vholder.getPosition();
            Toast.makeText(mContext, "This is position " + position, Toast.LENGTH_SHORT).show();
//            //タップしたのはTextViewなのでキャスト（型の変換）する
//            TextView textView = (TextView) view;
//            //リンクをタップした時に処理するクラスを作成。AndroidSDKにあるLinkMovementMethodを拡張しています。
//            MutableLinkMovementMethod m = new MutableLinkMovementMethod();
//            //MutableLinkMovementMethodのイベントリスナーをさらにセットしています。
//            m.setOnUrlClickListener(new MutableLinkMovementMethod.OnUrlClickListener() {
//                //リンクをクリックした時の処理
//                public void onUrlClick(TextView v,Uri uri) {
//                    Log.d("myurl", uri.toString());//デバッグログを出力します。
//                    // Intent のインスタンスを取得する。view.getContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
//                    Intent intent = new Intent(mContext, WebActivity.class);
//
//                    // 渡したいデータとキーを指定する。urlという名前でリンクの文字列を渡しています。
//                    intent.putExtra("url", uri.toString());
//
//                    // 遷移先の画面を呼び出す
//                    mContext.startActivity(intent);
//
//                }
//            });
//            //ここからはMutableLinkMovementMethodを使うための処理なので毎回同じ感じ。
//            //リンクのチェックを行うため一時的にsetする
//            textView.setMovementMethod(m);
//            //boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
//            //チェックが終わったので解除する しないと親view(listview)に行けない
//            textView.setMovementMethod(null);
//            //setMovementMethodを呼ぶとフォーカスがtrueになるのでfalseにする
//            textView.setFocusable(false);
//            //戻り値がtrueの場合は今のviewで処理、falseの場合は親view(ListView)で処理
//            //return mt;
        }
    };
}