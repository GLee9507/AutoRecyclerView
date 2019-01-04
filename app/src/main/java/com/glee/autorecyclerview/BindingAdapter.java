package com.glee.autorecyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.glee.planB.AutoList;
import com.glee.planB.BAdapter;

/**
 * @author liji
 * @date 2019/1/2 10:36
 * description
 */


public class BindingAdapter {
    private static final String LINEAR = "LinearLayoutManager";
    private static final String GRIDE = "GridLayoutManager";

    @android.databinding.BindingAdapter({
            "app:bind",
    })
    public static void autoAdapter(
            @NonNull RecyclerView recyclerView,
            AutoList autoList
    ) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new BAdapter(recyclerView.getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(adapter);
        }
        ((BAdapter)adapter).submitList(autoList);
    }

}
