package com.mitsuyoshi.gsapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;


public class UserActivity extends AppCompatActivity {
    //入力するビューです。
    private EditText mUsernameField;
    private EditText mPasswordField;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //自動ログインのため保存されているaccess tokenを読み出す。tokenがあれば自動ログインできる
        //SharedPreferences はアプリ用にローカルストレージに保存するためのファイル
        //SQLiteを使っても良いが、もうちょっと難しくなる
        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        //tokenがないとき。
        if(token == "") {
            //ログイン画面を作る
            CreateMyView(savedInstanceState);
        }else {
            //自動ログインをする。
            try {
                //KiiCloudのAccessTokenによるログイン処理。完了すると結果がcallback関数として実行される。
                //サーバーの実行結果があった時に、callbackが実行される。clickListenerみたいなもの
                KiiUser.loginWithToken(callback, token);
            } catch (Exception e) {
                //ダイアログを表示
                showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
                //画面を作る
                CreateMyView(savedInstanceState);
            }
        }

    }

    //ログイン画面用のViewを作る。いつもonCreateでやっていること
    protected void CreateMyView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user);
        //EditTextのビューを探します
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        //パスワードを隠す設定
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        //パスワードの入力文字を制限する。参考：http://techbooster.jpn.org/andriod/ui/3857/
        //InputTypeで入力文字内容を制限している（ | は２つの設定を両方使うので）
        mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //登録ボタン
        Button signupBtn = (Button) findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登録処理
                onSignupButtonClicked(v);
            }
        });
        //ログインボタン
        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ログイン処理
                onLoginButtonClicked(v);
            }
        });

        //Facebookログインボタン
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("FB", "loginSuccess");
                //Kiiクラウドへのログイン内容を入れる
                Bundle options = new Bundle();
                String accessToken = loginResult.getAccessToken().getToken();
                options.putString("accessToken", accessToken);
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
                //KiiCloudのソーシャル経由のログイン
                KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
                conn.logIn(UserActivity.this, options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            Toast.makeText(getApplicationContext(), "Failed to Login to Kii! " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Login to Kii! " + user.getID(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancel() {
                Log.v("FB", "Cancelled.");
                Toast.makeText(getApplicationContext(), "Facebook Login has been cancelled.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Log.v("FB", "loginFailed");
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Facebook Login has been failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //ログイン処理：参考　http://documentation.kii.com/ja/guides/android/managing-users/sign-in/
    public void onLoginButtonClicked(View v) {
        //IMEを閉じる
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //これを入れておかないとキーボードが開きっぱなしになってしまう
        //入力文字を得る
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        //APIによってはtry, catchが必須となる
        try {
            //KiiCloudのログイン処理。完了すると結果がcallback関数として実行される。
            KiiUser.logIn(callback, username, password);
        } catch (Exception e) {
            //ダイアログを表示
            showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
        }
    }
    //ダイアログを表示する
    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener ) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
        newFragment.show(getFragmentManager(), "dialog");
    }

    //登録処理
    public void onSignupButtonClicked(View v) {
        //IMEを閉じる
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //これを入れておかないとキーボードが開きっぱなしになってしまう

        //入力文字を得る
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        //APIによってはtry, catchが必須となる
        try {
            //KiiCloudのユーザ登録処理
            KiiUser user = KiiUser.createWithUsername(username);
            user.register(callback, password); //KiiCloudから通信が帰ってくるとcallbackは起動する
        } catch (Exception e) {
            showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
        }
    }

    //新規登録、ログインの時に呼び出されるコールバック関数
    KiiUserCallBack callback = new KiiUserCallBack() {
        //ログインが完了した時に自動的に呼び出される。自動ログインの時も呼び出される
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            // setFragmentProgress(View.INVISIBLE);
            if (e == null) {
                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply(); //.applyを入れないと実行されないので注意

                // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。遷移先のアクティビティーを.classで指定
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // 遷移先の画面を呼び出す
                startActivity(intent);
                //戻るボタンでログイン画面に戻れないようにActivityを終了しておく。
                finish();
            } else {
                //eには一般的なJavaのエラーのオブジェクトの場合と、KiiCloudのエラーのオブジェクトの場合がある
                //eがKiiCloud特有のクラスを継承している時
                if (e instanceof CloudExecutionException)
                    //KiiCloud特有のエラーメッセージを表示。フォーマットが違う
                    showAlert(R.string.operation_failed, Util.generateAlertMessage((CloudExecutionException) e), null);
                else
                    //一般的なJavaのエラーを表示
                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
            }
        }
        //新規登録の時に自動的に呼び出される
        @Override
        public void onRegisterCompleted(int token, KiiUser user, Exception e) {
            if (e == null) {
                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply(); //.applyを入れないと実行されないので注意

                // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。遷移先のアクティビティーを.classで指定
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // 遷移先の画面を呼び出す
                startActivity(intent);
                //戻るボタンでログイン画面に戻れないようにActivityを終了しておく。
                finish();
            } else {
                //eには一般的なJavaのエラーのオブジェクトの場合と、KiiCloudのエラーのオブジェクトの場合がある
                //eがKiiCloud特有のクラスを継承している時
                if (e instanceof CloudExecutionException)
                    //KiiCloud特有のエラーメッセージを表示
                    showAlert(R.string.operation_failed, Util.generateAlertMessage((CloudExecutionException) e), null);
                else
                    //一般的なJavaのエラーを表示
                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Kii.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Kii.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //メニュー関係：未使用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
