//ListViewに１つのセルの情報(message_item.xmlとMessageRecord)を結びつけるためのクラス
package com.mitsuyoshi.gsapp;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

public class MessageRecordsAdapter extends RecyclerView.Adapter<MessageRecordHolder> {
    private ImageLoader mImageLoader;
    private List<MessageRecord> mDataList;

    public MessageRecordsAdapter(){
        //キャッシュメモリを確保して画像を取得するクラスを作成。これを使って画像をダウンロードする。Volleyの機能
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    //データをセットしなおす関数
    public void setMessageRecords(List<MessageRecord> dataList) {
        mDataList = dataList;
    }

    @Override
    public MessageRecordHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        MessageRecordHolder holder = new MessageRecordHolder(viewGroup.getContext(), v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageRecordHolder holder, int position) {
        MessageRecord m = mDataList.get(position);
        Log.d("TAG", "position is " + position);

        holder.card.setCardBackgroundColor(m.getIntValue());
        holder.image.setImageUrl(m.getImageUrl(), mImageLoader);
        holder.titleText.setText(m.getTitle());
        holder.content1Text.setText(m.getContent1());
        holder.content2Text.setText(m.getContent2());
        //for clickListener
        holder.setShopUrl(m.getShopUrl());
        holder.setMapLocation(m.getTitle(), m.getLat(), m.getLng());
        //for view expansion
//        holder.setIsViewExpanded(mMessageRecord.getExpanded());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


//    //表示するViewを返します。これがListVewの１つのセルとして表示されます。表示されるたびに実行されます。
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        //convertViewをチェックし、Viewがないときは新しくViewを作成します。convertViewがセットされている時は未使用なのでそのまま再利用します。メモリーに優しい。
//        if(convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
//        }
//
//        //レイアウトにある画像と文字のViewを所得します。
//        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
//        TextView textView1 = (TextView) convertView.findViewById(R.id.text1);
//        TextView textView2 = (TextView) convertView.findViewById(R.id.text2);
//
//        //webリンクを制御するプログラムはここから
//        //TextView に LinkMovementMethod を登録します
//        //TextViewをタップした時のイベントリスナー（タップの状況を監視するクラス）を登録します。onTouchにタップした時の処理を記述します。buttonやほかのViewも同じように記述できます。
//        textView1.setOnTouchListener(new ViewGroup.OnTouchListener() {
//            //タップした時の処理
//            @Override
//            public boolean onTouch(final View view, MotionEvent event) {
//                //タップしたのはTextViewなのでキャスト（型の変換）する
//                TextView textView = (TextView) view;
//                //リンクをタップした時に処理するクラスを作成。AndroidSDKにあるLinkMovementMethodを拡張しています。
//                MutableLinkMovementMethod m = new MutableLinkMovementMethod();
//                //MutableLinkMovementMethodのイベントリスナーをさらにセットしています。
//                m.setOnUrlClickListener(new MutableLinkMovementMethod.OnUrlClickListener() {
//                    //リンクをクリックした時の処理
//                    public void onUrlClick(TextView v,Uri uri) {
//                        Log.d("myurl",uri.toString());//デバッグログを出力します。
//                        // Intent のインスタンスを取得する。view.getContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
//                        Intent intent = new Intent(view.getContext(), WebActivity.class);
//
//                        // 渡したいデータとキーを指定する。urlという名前でリンクの文字列を渡しています。
//                        intent.putExtra("url", uri.toString());
//
//                        // 遷移先の画面を呼び出す
//                        view.getContext().startActivity(intent);
//
//                    }
//                });
//                //ここからはMutableLinkMovementMethodを使うための処理なので毎回同じ感じ。
//                //リンクのチェックを行うため一時的にsetする
//                textView.setMovementMethod(m);
//                boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
//                //チェックが終わったので解除する しないと親view(listview)に行けない
//                textView.setMovementMethod(null);
//                //setMovementMethodを呼ぶとフォーカスがtrueになるのでfalseにする
//                textView.setFocusable(false);
//                //戻り値がtrueの場合は今のviewで処理、falseの場合は親view(ListView)で処理
//                return mt;
//            }
//        });
//        //webリンクを制御するプログラムはここまで
//
//        //表示するセルの位置からデータをMessageRecordのデータを取得します。
//        MessageRecord imageRecord = getItem(position);
//
//        //mImageLoaderを使って画像をダウンロードし、Viewにセットします。
//        imageView.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
//        //Viewに文字をセットします。
//        textView1.setText(imageRecord.getTitle());
//        textView2.setText(imageRecord.getContent());
//
//        //1つのセルのViewを返します。
//        return convertView;
//    }
}
