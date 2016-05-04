package com.mitsuyoshi.gsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class MessageRecordHolder extends RecyclerView.ViewHolder implements  OnMapReadyCallback {

    protected NetworkImageView image;
    protected TextView titleText;
    protected TextView content1Text;
    protected TextView content2Text;
    protected CardView card;
    protected Button button;

    private String mUrl;

    protected GoogleMap mGoogleMap;
    public MapView mapView;
    private Context mContext;

    private String mName;
    private Double mLat;
    private Double mLng;

    public MessageRecordHolder(Context context, View itemView) {
        super(itemView);
        card = (CardView) itemView;
        image = (NetworkImageView) itemView.findViewById(R.id.image);
        titleText = (TextView) itemView.findViewById(R.id.title);
        content1Text = (TextView) itemView.findViewById(R.id.shopCatch);
        content2Text = (TextView) itemView.findViewById(R.id.shopWebsite);
        button = (Button) itemView.findViewById(R.id.mapButton);
        mapView = (MapView) itemView.findViewById(R.id.map);

        //リスナー実装
        image.setOnClickListener(clickListener);
        button.setOnClickListener(buttonClickListener);

        //Map
        mContext = context;
        mapView.onCreate(null);
        mapView.getMapAsync(this);
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
            view.getContext().startActivity(intent);
        }
    };

    public void setMapLocation(String name, Double lat, Double lng){
        mName = name;
        mLat = lat;
        mLng = lng;
        // If the map is ready, update its content.
        if (mGoogleMap != null) {
            updateMapContents();
        }
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Toast表示
            Toast.makeText(view.getContext(), "Lng: " + mLng + ", Lat: " + mLat, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        MapsInitializer.initialize(mContext);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // If we have map data, update the map content.
        if (mLat != null && mLng != null) {
            updateMapContents();
        }
    }

    protected void updateMapContents() {
        // Since the mapView is re-used, need to remove pre-existing mapView features.
        mGoogleMap.clear();

        LatLng location = new LatLng(mLat, mLng);
        // Update the mapView feature data and camera position.
        mGoogleMap.addMarker(new MarkerOptions().position(location).title(mName));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
        mGoogleMap.moveCamera(cameraUpdate);
    }

}