//起動時に実行されるアクティビティーです。１つの画面に１つのアクティビティーが必要です。
//どのアクティビティーが起動時に実行されるのかはAndroidManifestに記述されています。
package com.mitsuyoshi.gsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //アダプタークラスです。
    private MessageRecordsAdapter mAdapter;
    private List<MessageRecord> mMessageRecords = new ArrayList<MessageRecord>();
    private String mSearchText;

    //起動時にOSから実行される関数です。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //KiiCloudの初期化。Applicationクラスで実行してください。キーは自分の値にかえる。
        Kii.initialize(getApplicationContext(), getString(R.string.kii_app_id), getString(R.string.kii_app_key), Kii.Site.JP);
        //ログインしていない時はログイン画面を表示する
        //KiiCloudでのログイン状態を取得します。nullの時はログインしていない。
        KiiUser user = KiiUser.getCurrentUser();
        //自動ログインのため保存されているaccess tokenを読み出す。tokenがあればログインできる
        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        //ログインしていない時はログインのactivityに遷移.SharedPreferencesが空の時もチェックしないとLogOutできない。
        if(user == null || token == "") {
            // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            // 遷移先の画面を呼び出す
            startActivity(intent);
            //ログインできてない時はMainActivityに戻れないように、MainActivityを終了します。
            finish();
        }

        //メイン画面のレイアウトをセットしています。
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAdapter = new MessageRecordsAdapter();
        mAdapter.setMessageRecords(mMessageRecords);
        //RecyclerViewのViewを取得
        RecyclerView rv = (RecyclerView) findViewById(R.id.mylist);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        //RecyclerViewにアダプターをセット。
        rv.setAdapter(mAdapter);
        //一覧のデータを作成して表示します。
        mSearchText = "御茶ノ水";
        fetch();
    }

    //自分で作った関数です。一覧のデータを作成して表示します。
    private void fetch() {
        //jsonデータをサーバーから取得する通信機能です。Volleyの機能です。通信クラスのインスタンスを作成しているだけです。通信はまだしていません。
        JsonObjectRequest request = new JsonObjectRequest(
                "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/" +
                        "?key=e8c791dd3c21d317" +
                        "&format=json" +
                        "&keyword=" + mSearchText ,
                null,
                //サーバー通信した結果、成功した時の処理をするクラスを作成しています。
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //try catchでエラーを処理します。tryが必要かどうかはtryに記述している関数次第です。
                        try {
                            //jsonデータを下記で定義したparse関数を使いデータクラスにセットしています。
                            mMessageRecords = parseHotpepper(jsonObject);
                            //データをアダプターにセットしています。
                            mAdapter.setMessageRecords(mMessageRecords);
                            mAdapter.notifyDataSetChanged();
                        }
                        catch(JSONException e) {
                            //トーストを表示
                            Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                //通信結果、エラーの時の処理クラスを作成。
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //トーストを表示
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //作成した通信クラスをキュー、待ち行列にいれて適当なタイミングで通信します。
        //VolleyApplicationはnewしていません。これはAndroidManifestで記載しているので起動時に自動的にnewされています。
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    //サーバにあるjsonデータをMessageRecordに変換します。
    private List<MessageRecord> parseHotpepper(JSONObject json) throws JSONException {
        //空のMessageRecordデータの配列を作成
        ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
        //jsonデータのmessagesにあるJson配列を取得します。
        JSONArray jsonMessages = json.getJSONObject("results").getJSONArray("shop");
        //配列の数だけ繰り返します。
        for (int i =0; i < jsonMessages.length(); i++) {
            //１つだけ取り出します。
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            //jsonの値を取得します。
            String url = jsonMessage.getJSONObject("photo").getJSONObject("mobile").getString("l");
            String title = jsonMessage.getString("name");
            String content1 = jsonMessage.getJSONObject("genre").getString("catch");
//            String content2 = jsonMessage.getString("access"); //アクセス
//            String content2 = jsonMessage.getString("open"); //営業時間
            String content2 = jsonMessage.getJSONObject("budget").getString("average"); //予算
            if(content2.equals("")){
                content2 = "(記載なし)";
            }
            String shopUrl = jsonMessage.getJSONObject("urls").getString("pc");
            Double shopAddressLng = Double.valueOf(jsonMessage.getString("lng"));
            Double shopAddressLat = Double.valueOf(jsonMessage.getString("lat"));
            //jsonMessageを新しく作ります。
            MessageRecord record = new MessageRecord(url, title, content1, "予算：" + content2, shopUrl, shopAddressLng, shopAddressLat, i);
            //MessageRecordの配列に追加します。
            records.add(record);
        }
        return records;
    }

    private void fetchGnavi() {
        //jsonデータをサーバーから取得する通信機能です。Volleyの機能です。通信クラスのインスタンスを作成しているだけです。通信はまだしていません。
        JsonObjectRequest request = new JsonObjectRequest(
                "http://api.gnavi.co.jp/RestSearchAPI/20150630/" +
                        "?keyid=f11228f6979ff36949ef05c26d59d8a1" +
                        "&format=json" +
                        "&freeword=" + mSearchText ,
                null,
                //サーバー通信した結果、成功した時の処理をするクラスを作成しています。
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //try catchでエラーを処理します。tryが必要かどうかはtryに記述している関数次第です。
                        try {
                            //jsonデータを下記で定義したparse関数を使いデータクラスにセットしています。
                            mMessageRecords = parseGnavi(jsonObject);
                            //データをアダプターにセットしています。
                            mAdapter.setMessageRecords(mMessageRecords);
                            mAdapter.notifyDataSetChanged();
                        }
                        catch(JSONException e) {
                            //トーストを表示
                            Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                //通信結果、エラーの時の処理クラスを作成。
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //トーストを表示
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //作成した通信クラスをキュー、待ち行列にいれて適当なタイミングで通信します。
        //VolleyApplicationはnewしていません。これはAndroidManifestで記載しているので起動時に自動的にnewされています。
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    //サーバにあるjsonデータをMessageRecordに変換します。
    private List<MessageRecord> parseGnavi(JSONObject json) throws JSONException {
        //空のMessageRecordデータの配列を作成
        ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
        //jsonデータのmessagesにあるJson配列を取得します。
        JSONArray jsonMessages = json.getJSONArray("rest");
        //配列の数だけ繰り返します。
        for (int i =0; i < jsonMessages.length(); i++) {
            //１つだけ取り出します。
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            //jsonの値を取得します。
            String url = jsonMessage.getJSONObject("image_url").getString("shop_image1");
            String title = jsonMessage.getString("name");
            String content1 = jsonMessage.getString("category");
            //String content2 = jsonMessage.getString("opentime"); //営業時間
            String content2 = jsonMessage.getJSONObject("pr").getString("pr_short"); //PR
            if(content2.equals("{}")){
                content2 = "(記載なし)";
            }
            String shopUrl = jsonMessage.getString("url");
            Double shopAddressLng = Double.valueOf(jsonMessage.getString("longitude"));
            Double shopAddressLat = Double.valueOf(jsonMessage.getString("latitude"));
            //jsonMessageを新しく作ります。
            MessageRecord record = new MessageRecord(url, title, content1, content2, shopUrl, shopAddressLng, shopAddressLat, i);
            //MessageRecordの配列に追加します。
            records.add(record);
        }
        return records;
    }

    //デフォルトで作成されたメニューの関数です。未使用。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();;
        Log.d("TAG", "searchView is " + searchView);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //ログアウト処理.KiiCloudにはログアウト機能はないのでAccesTokenを削除して対応。
        if (id == R.id.log_out) {
            //自動ログインのため保存されているaccess tokenを消す。
            SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
            pref.edit().clear().apply();
            //ログイン画面に遷移
            // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            // 遷移先の画面を呼び出す
            startActivity(intent);
            //戻れないようにActivityを終了します。
            finish();
            return true;
        }

        //Postで追加ここから
        //投稿処理
        if (id == R.id.post) {
            //投稿画面に遷移
            // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            // 遷移先の画面を呼び出す
            startActivity(intent);
            return true;
        }
        //Postで追加ここまで

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchText = query;
        fetch();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchText = newText;
        if(!mSearchText.equals("")){
            fetch();
        }
        return false;
    }
}
