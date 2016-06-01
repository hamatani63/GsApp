package com.mitsuyoshi.gsapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;

//public class MessageRecordHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
public class MessageRecordHolder extends RecyclerView.ViewHolder {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView content1Text;
    protected TextView content2Text;
    protected CardView card;
    protected Button button;
    protected Button goodButton;
    //Website
    private String mUrl;
    //Maps
    protected GoogleMap mGoogleMap;
    public MapView mapView;
    private Context mContext;
    //Maps: location
    private String mName;
    private Double mLat;
    private Double mLng;
    //Animation
    private int mMapViewHeight = 600;
    private boolean mIsViewExpanded;

    public MessageRecordHolder(Context context, View itemView) {
        super(itemView);
        card = (CardView) itemView;
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
//        content1Text = (TextView) itemView.findViewById(R.id.shopCatch);
//        content2Text = (TextView) itemView.findViewById(R.id.shopContent2);
//        button = (Button) itemView.findViewById(R.id.mapButton);
//        mapView = (MapView) itemView.findViewById(R.id.map);
        //Goodで追加ここから
        goodButton = (Button) itemView.findViewById(R.id.button1);


        //リスナー実装
        image.setOnClickListener(clickListener);
        titleText.setOnClickListener(clickListener);
//        content1Text.setOnClickListener(clickListener);
//        button.setOnClickListener(buttonClickListener);
//
//        //Map
//        mContext = context;
//        mapView.onCreate(null);
//        mapView.getMapAsync(this);
//        //Animation
//        mIsViewExpanded = false;
//        mapView.getLayoutParams().height = 0;
//        mapView.setVisibility(View.GONE);
//        mapView.setEnabled(mIsViewExpanded);
    }

    public void setShopUrl(String url){
        mUrl = url;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            //WebView表示
            Intent intent = new Intent(view.getContext(), WebActivity.class);
            intent.putExtra("url", mUrl);
            intent.putExtra("name", mName);
            view.getContext().startActivity(intent);
        }
    };

//    public void setMapLocation(String name, Double lat, Double lng){
//        mName = name;
//        mLat = lat;
//        mLng = lng;
//        // If the map is ready, update its content.
//        if (mGoogleMap != null) {
//            updateMapContents();
//        }
//    }
//
//    private GoogleMap.OnMapClickListener googleMapClickListener = new GoogleMap.OnMapClickListener() {
//        @Override
//        public void onMapClick(LatLng latLng) {
////            Toast.makeText(mContext, "Lng: " + mLng + ", Lat: " + mLat, Toast.LENGTH_SHORT).show();
//            //別画面でMapView表示
//            Intent intent = new Intent(mContext, MapActivity.class);
//            intent.putExtra("name", mName);
//            intent.putExtra("lat", mLat);
//            intent.putExtra("lng", mLng);
//            mContext.startActivity(intent);
//        }
//    };

//    public void setIsViewExpanded(Boolean isViewExpanded){
//        mIsViewExpanded = isViewExpanded;
//    }
//
//    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(final View view) {
//            //MapView表示
//            // Declare a ValueAnimator object
//            ValueAnimator valueAnimator;
//
//            if (!mIsViewExpanded) {
//                mIsViewExpanded = true;
//                valueAnimator = ValueAnimator.ofInt(0, mMapViewHeight);
//                mapView.setVisibility(View.VISIBLE);
//                mapView.setEnabled(mIsViewExpanded);
//            } else {
//                mIsViewExpanded = false;
//                valueAnimator = ValueAnimator.ofInt(mMapViewHeight, 0);
//
//                Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
//                a.setDuration(200);
//                // Set a listener to the animation and configure onAnimationEnd
//                a.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        mapView.setVisibility(View.GONE);
//                        mapView.setEnabled(mIsViewExpanded);
//                    }
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
//                // Set the animation on the custom view
//                mapView.startAnimation(a);
//            }
//            valueAnimator.setDuration(200);
//            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    Integer value = (Integer) animation.getAnimatedValue();
//                    mapView.getLayoutParams().height = value.intValue();
//                    mapView.requestLayout();
//                }
//            });
//            valueAnimator.start();
//        }
//    };
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//        MapsInitializer.initialize(mContext);
//        googleMap.getUiSettings().setMapToolbarEnabled(false);
//        // If we have map data, update the map content.
//        if (mLat != null && mLng != null) {
//            updateMapContents();
//        }
//        //リスナー設定すると、GoogleMapのLiteモードのonClickイベントがoverrideされる
//        //これにより、勝手に純正アプリへ遷移するのを防ぐことができる。
//        mGoogleMap.setOnMapClickListener(googleMapClickListener);
//    }
//
//    protected void updateMapContents() {
//        // Since the mapView is re-used, need to remove pre-existing mapView features.
//        mGoogleMap.clear();
//        LatLng location = new LatLng(mLat, mLng);
//        // Update the mapView feature data and camera position.
//        mGoogleMap.addMarker(new MarkerOptions().position(location).title(mName));
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15f);
//        mGoogleMap.moveCamera(cameraUpdate);
//    }

}