//ブラウザを表示するアクティビティーです
package com.mitsuyoshi.gsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class WebActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        //Intent を取得: Intentでアクティビティー間のデータを受け渡しします。Intentの値を受け取るために作成。
        Intent intent = getIntent();
        String url  = intent.getStringExtra("url");
        //メニュー：戻るボタンとタイトル表示
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("name"));

        //WebViewを探す
        mWebView = (WebView) findViewById(R.id.webView1);
        //デバッグログ
        Log.d("get myurl", url);
        //ブラウザの機能をセットします。お約束。
        mWebView.setWebViewClient(new WebViewClient());
        //URLを表示します。
        mWebView.loadUrl(url);
    }

    //GrowthHackで追加ここから
    @Override
    protected void onStart() {
        super.onStart();
        Tracker t = ((VolleyApplication)getApplication()).getTracker(VolleyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }
    //GrowthHackで追加ここまで

    @Override
    public boolean onOptionsItemSelected(MenuItem item){ // Called when you tap a menu item
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}
