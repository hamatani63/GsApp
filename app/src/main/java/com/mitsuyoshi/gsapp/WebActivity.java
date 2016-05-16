//ブラウザを表示するアクティビティーです
package com.mitsuyoshi.gsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
