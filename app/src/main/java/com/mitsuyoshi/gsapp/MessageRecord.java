//1つのセルにあるデータを保存するためのデータクラスです。
package com.mitsuyoshi.gsapp;

public class MessageRecord {
    //保存するデータ全てを変数で定義します。
    private String imageUrl;
    private String title;
    private String content1;
    private String content2;
    private String shopUrl;
    private Double lng;
    private Double lat;
    private int intValue;

    //データを１つ作成する関数です。項目が増えたら増やしましょう。
    public MessageRecord(String imageUrl, String title, String content1, String content2, String shopUrl, Double lng, Double lat, int intValue) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.content1 = content1;
        this.content2 = content2;
        this.shopUrl = shopUrl;
        this.lng = lng;
        this.lat = lat;
        this.intValue = intValue;
    }

    //それぞれの項目を返す関数です。項目が増えたら増やしましょう。
    public String getImageUrl() {
        return imageUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getContent1(){
        return content1;
    }
    public String getContent2() {
        return content2;
    }
    public String getShopUrl(){
        return shopUrl;
    }
    public Double getLng(){
        return lng;
    }
    public Double getLat(){
        return lat;
    }
    public int getIntValue(){
        return intValue;
    }
}
