package com.mitsuyoshi.gsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    @Override
//    public boolean onCreateOptionMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//    }
//
//    @Override
//    public boolean onOptionalItemSelected(MenuItem item){
//        int id = item.getItemId();
//
//        if(id == R.id.action_settings){
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
