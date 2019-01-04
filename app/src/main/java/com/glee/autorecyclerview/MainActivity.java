package com.glee.autorecyclerview;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.glee.planB.BViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        BViewModel bViewModel = new BViewModel();
//        viewDataBinding.setVariable(BR.vm, bViewModel);
//        viewDataBinding.setLifecycleOwner(this);
        startActivity(new Intent(this, TestActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() called");
        super.onStop();
    }
}
