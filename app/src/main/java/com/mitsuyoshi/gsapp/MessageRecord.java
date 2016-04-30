//1つのセルにあるデータを保存するためのデータクラスです。
package com.mitsuyoshi.gsapp;

public class MessageRecord {
    //保存するデータ全てを変数で定義します。
    private String imageUrl;
    private String title;
    private String content;
    private int intValue;

    //データを１つ作成する関数です。項目が増えたら増やしましょう。
    public MessageRecord(String imageUrl, String title, String content, int intValue) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.intValue = intValue;
    }

    //それぞれの項目を返す関数です。項目が増えたら増やしましょう。
    public String getImageUrl() {
        return imageUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getContent(){
        return content;
    }
    public int getIntValue(){
        return intValue;
    }
}
