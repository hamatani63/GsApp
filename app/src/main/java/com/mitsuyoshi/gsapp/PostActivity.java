//投稿するActivityです
package com.mitsuyoshi.gsapp;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.callback.KiiObjectPublishCallback;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;

import java.io.File;
import java.io.FileOutputStream;


public class PostActivity extends ActionBarActivity{
    //今回使用するインテントの結果の番号。適当な値でOK.
    private static final int IMAGE_CHOOSER_RESULTCODE = 1;
    //画像のパスを保存しておく
    private String mImagePath = null;
    //UPした画像のKiiObject
    private KiiObject mKiiImageObject = null;
    //入力したコメント
    private String comment;
    //カメラで撮影した画像のuri
    private Uri mImageUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //画像ボタンにクリックイベントを追加しています。
        Button attachBtn = (Button) findViewById(R.id.attach_button);
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //クリックした時は画像選択
                onAttachFileButtonClicked(v);
            }
        });
        //カメラボタンにクリックイベントを追加しています。
        Button attachCameraBtn = (Button) findViewById(R.id.attach_camera_button);
        attachCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //クリックした時はカメラ起動する
                onAttachCameraFileButtonClicked(v);
            }
        });
        //投稿ボタンにクリックイベントを追加しています。
        Button postBtn = (Button) findViewById(R.id.post_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //クリックした時は投稿する
                onPostButtonClicked(v);
            }
        });
    }
    //画像の添付ボタンをおした時の処理
    //ギャラリー（別アプリ）を起動する典型的な書き方
    public void onAttachFileButtonClicked(View v) {
        //ギャラリーを開くインテントを作成して起動する。
        Intent intent = new Intent();
        //フアイルのタイプを設定：imageだけ。動画はダメにする。
        intent.setType("image/*");
        //画像のインテント
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Activityを起動：ギャラリーを起動
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CHOOSER_RESULTCODE);
    }
    //カメラの添付ボタンをおした時の処理
    public void onAttachCameraFileButtonClicked(View v) {
        //カメラは機種依存が大きく、いろいろサンプルを見たほうが良い
        //普通は下記の２行の処理で大丈夫。でもXperiaだと動かない。
        //カメラのインテントを作成
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Activityを起動
        //startActivityForResult(Intent.createChooser(intent, "Camera"), IMAGE_CHOOSER_RESULTCODE);

        //Xperiaでカメラを使う場合は、あらかじめファイルを作成する必要がある。
        //現在時刻をもとに一時ファイル名を作成
        String filename = System.currentTimeMillis() + ".jpg";
        //設定を保存するパラメータを作成：撮りたい画像の種類を指定する
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);//ファイル名
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");//ファイルの種類
        //設定した一時ファイルを作成（この画像は空っぽ）：カメラアプリがここに上書きする
        mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //カメラのインテントを作成
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//カメラ
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);//画像の保存先を先ほどの一時ファイルに指定する
        //インテント起動
        startActivityForResult(intent, IMAGE_CHOOSER_RESULTCODE);
    }
    //画像を選択した後に実行されるコールバック関数。インテントの実行された後にコールバックされる。自動的に実行されます。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //他のインテントの実行結果と区別するためstartActivityで指定した定数IMAGE_CHOOSER_RESULTCODEと一致するか確認
        //onActivityResultは他のアプリからも呼ばれる可能性がある。requestCodeで判断する
        if (requestCode == IMAGE_CHOOSER_RESULTCODE) {
            //失敗の時
            if (resultCode != RESULT_OK ) {
                //何もしない（本当はエラーメッセージを出した方が良い）
                return;
            }

            //画像を取得する。
            Uri result;
            if(data != null) {
                //Xperiaの場合はdataに画像が入っている。
                result = data.getData();
            }else {
                //Xperia以外はintentで設定したmImageUriに入っている。
                result = mImageUri;
                Log.d("mogi:mImageUri:",result.toString());
            }
            //画面に画像を表示
            ImageView iv = (ImageView) findViewById(R.id.image_view1);
            iv.setImageURI(result);

            //画像のパスを設定。Uploadでつかう。
            //AndroidのバーションによってUriの保存先が違うため、getFilePathByUriを使う
            //（getStringだと失敗する）
            mImagePath = getFilePathByUri(result);

        }
    }
    //uriからファイルのパスを取得する。バージョンによって処理が違う。KiiCloudのチュートリアルから取り込んだ。汎用的に使えます。
    private String getFilePathByUri(Uri selectedFileUri) {
        //4.2以降の時
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Workaround of retrieving file image through ContentResolver
            // for Android4.2 or later
            String filePath = null;
            FileOutputStream fos = null;
            try {
                //ビットマップを取得
                Bitmap bmp = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), selectedFileUri);
                //一時保存するディレクトリ。アプリに応じてgsappの部分を変更したほうが良い
                String cacheDir = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "gsapp";
                //ディレクトリ作成
                File createDir = new File(cacheDir);
                if (!createDir.exists()) {
                    createDir.mkdir();
                }
                //一時ファイル名を作成。毎回上書き：こうしないと機種によってはうまく動かなくなる
                filePath = cacheDir + File.separator + "upload.jpg";
                File file = new File(filePath);
                //ビットマップをjpgに変換して一時的に保存する。
                fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                fos.flush();
                fos.getFD().sync();
            } catch (Exception e) { //エラーの時に実行
                filePath = null;
            } finally {//正常でもエラーでも、かならず最後に実行する処理
                if (fos != null) {
                    try {
                        //エラーでファイルが閉じられないと書き込めなくなるので、
                        //何があってもファイルを閉じる
                        fos.close();
                    } catch (Exception e) {
                        // Nothing to do
                    }
                }
            }
            return filePath;
        } else {
            //データから探す
            String[] filePathColumn = { MediaStore.MediaColumns.DATA };
            Cursor cursor = this.getContentResolver().query(
                    selectedFileUri, filePathColumn, null, null, null);

            if (cursor == null)
                return null;
            try {
                if (!cursor.moveToFirst())
                    return null;
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                if (columnIndex < 0) {
                    return null;
                }
                //これがファイルのパス
                String picturePath = cursor.getString(columnIndex);
                return picturePath;
            } finally {
                cursor.close();
            }
        }
    }


    //投稿ボタンを御した時の処理
    public void onPostButtonClicked(View v) {
        //入力文字を得る
        EditText mCommentField = (EditText) (findViewById(R.id.comment_field));
        comment = mCommentField.getText().toString();
        //Log.d("mogi comment", ":" + comment + ":");
        //未入力（""は文字が空）の時はエラーメッセージを出す
        if (comment.equals("")) {
            //ダイアログを表示
            showAlert(getString(R.string.no_data_message));
            return;
        }
        //まずimagesのバケットにアップしておかないとURLが確定できないので、
        //画像をUPしてからmessagesに投稿する。
        if (mImagePath != null) {
            //ファイルをUP、完了した時にpostMessagesを実行している。
            uploadFile(mImagePath);
        }else {
            //画像がないときはcommentだけ登録
            postMessages(null);
        }
    }
    //投稿処理。画像のUploadがうまくいったときは、urlに公開のURLがセットされる
    public void postMessages(String url) {
        //バケット名を設定。バケット＝DBのテーブルみたいなもの。Excelのシートみたいなもの。
        KiiBucket bucket = Kii.bucket("messages");
        KiiObject object = bucket.object();
        //Json形式でKeyのcommentをセット.{"comment":"こめんとです","imageUrl":"http://xxx.com/xxxx"}
        object.set("comment", comment);
        //画像があるときだけセット
        if(url != null) {
            object.set("imageUrl", url);
        }
        //データをKiiCloudに保存
        object.save(new KiiObjectCallBack() {
            //保存結果が帰ってくるコールバック関数。自動的に呼び出される。
            @Override
            public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                //エラーがないとき
                if (exception == null) {
                    // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。
                    // 遷移先にはMainActivityを指定する。（遷移先のアクティビティーを.classで指定）
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //PostActivityを終了します。
                    finish();
                } else {
                    //eがKiiCloud特有のクラスを継承している時
                    if (exception instanceof CloudExecutionException)
                        //KiiCloud特有のエラーメッセージを表示。フォーマットが違う
                        showAlert(Util.generateAlertMessage((CloudExecutionException) exception));
                    else
                        //一般的なエラーを表示
                        showAlert(exception.getLocalizedMessage());
                }
            }
        });
    }

    //画像をKiiCloudのimagesにUPする。参考：チュートリアル、http://www.riaxdnp.jp/?p=6775
    private void uploadFile(String path) {
        //イメージを保存するバケット名を設定。すべてここに保存してmessageにはそのhttpパスを設定する。
        //バケット＝DBのテーブルみたいなもの。Excelのシートみたいなもの。
        //まずimagesバケットを作成する
        KiiBucket bucket = Kii.bucket("images");
        KiiObject object = bucket.object(); //メモリ上に空バケットの生成
        //Up後に公開設定するので保存
        mKiiImageObject = object;
        File f = new File(path);
        //KiiCloudにアップロードする
        //まずインスタンスを生成
        KiiUploader uploader = object.uploader(this, f);
        //プログレスダイアログのインスタンスも生成
        mProgressDialog = new ProgressDialog(this);
        //非同期でUpする。
        uploader.transferAsync (new KiiRTransferCallback() { //Kiiの転送完了のコールバックを待つ
            @Override
            public void onStart(KiiRTransfer operator) {
//                mProgressDialog.setTitle("タイトル");
                mProgressDialog.setMessage("画像アップロード中...");
//                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //プログレスバー
//                mProgressDialog.setMax(100);
//                mProgressDialog.setCancelable(false); // プログレスダイアログのキャンセルが可能かどうかを設定
            }

            @Override
            public void onProgress(KiiRTransfer operator, long completedInBytes, long totalSizeInBytes) {
                //プログレスバー表示更新
                //float progress = (float) completedInBytes / (float) totalSizeInBytes * 100.0f;
                //mProgressDialog.incrementProgressBy((int) progress);
            }

            //完了した時
            @Override
            public void onTransferCompleted(KiiRTransfer operator, Exception e) {
                //プログレスダイアログの消去
                mProgressDialog.dismiss();

                if (e == null) {
                    //成功の時
                    //画像を一覧で表示するため、公開状態にする。参考：http://www.riaxdnp.jp/?p=6841
                    //URI指定Obj（mKiiImageObject）をリフレッシュして、最新状態にする
                    mKiiImageObject.refresh(new KiiObjectCallBack() { //Kiiのリフレッシュ完了のコールバックを待つ
                        public void onRefreshCompleted(int token, KiiObject object, Exception e) {
                            if (e == null) {
                                // ObjectBodyの公開設定する：公開状態にすることで画像のURLを取得できる
                                object.publishBody(new KiiObjectPublishCallback() {
                                    @Override
                                    public void onPublishCompleted(String url, KiiObject kiiObject, Exception e) {
                                        Log.d("KiiCloud_imageUrl", url);
                                        //画像のURL付きでmessagesに投稿する。messagesバケットに書き込む
                                        postMessages(url);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    //失敗の時
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudExecutionException)
                        showAlert(Util.generateAlertMessage((CloudExecutionException) cause));
                    else
                        showAlert(e.getLocalizedMessage());
                }
            }
        });
        //プログレスダイアログの表示
        mProgressDialog.show();

    }
//    @Override
//    public void run(){
//        try{
//            thread.sleep()
//        } catch (InterruptedException e){
//            e.printStackTrace();
//        }
//        progressDialog.dismiss();
//        handler.sendemptyMessage(0);
//    }

    //エラーダイアログを表示する
    void showAlert(String message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(R.string.operation_failed, message, null);
        newFragment.show(getFragmentManager(), "dialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_post, menu);
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
