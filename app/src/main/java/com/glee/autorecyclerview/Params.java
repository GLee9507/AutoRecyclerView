package com.glee.autorecyclerview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.view.LayoutInflater;

/**
 * @author liji
 * @date 2018/12/29 17:32
 * description
 */


public class Params<T> {
    private Context context;

    private LayoutInflater inflater;

    private AsyncDifferConfig<T> differConfig;
    @LayoutRes
    private int layoutId;
    @IdRes
    private int brId;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public AsyncDifferConfig<T> getDifferConfig() {
        return differConfig;
    }

    public void setDifferConfig(AsyncDifferConfig<T> differConfig) {
        this.differConfig = differConfig;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public int getBrId() {
        return brId;
    }

    public void setBrId(int brId) {
        this.brId = brId;
    }
}
