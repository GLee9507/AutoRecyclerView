package com.glee.autorecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author liji
 * @date 2019/1/2 10:01
 * description
 */


public class AutoRecyclerView extends RecyclerView {

    private final LayoutManager.Properties properties;

    public AutoRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public AutoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        properties = LayoutManager.getProperties(getContext(), attrs, 0, defStyle);
    }

    private void init() {

    }
}
