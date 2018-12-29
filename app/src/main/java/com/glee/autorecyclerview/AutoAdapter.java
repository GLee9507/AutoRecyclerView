package com.glee.autorecyclerview;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author liji
 * @date 2018/12/29 17:12
 * description
 */


public class AutoAdapter<T> extends RecyclerView.Adapter<BindingViewHolder> {
    private final AsyncListDiffer<T> differ;
    private final Params<T> params;
    private SparseArray<View> headers;
    private SparseArray<View> footers;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = -1;

    public AutoAdapter(@NonNull Params<T> params) {
        differ = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), params.getDifferConfig());
        this.params = params;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BindingViewHolder(DataBindingUtil.inflate(params.getInflater(), params.getLayoutId(), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder bindingViewHolder, int i) {
        ViewDataBinding binding = bindingViewHolder.getBinding();
        binding.setVariable(params.getBrId(), getItem(i));
        binding.executePendingBindings();
    }

    //TODO 绑定 header footer
    private T getItem(int position) {

        return differ.getCurrentList().get(position - getHeaderCount());
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getContentCount() + getFooterCount();
    }

    private int getContentCount() {
        return differ.getCurrentList().size();
    }

    private int getHeaderCount() {
        return headers == null ? 0 : headers.size();
    }

    private int getFooterCount() {
        return footers == null ? 0 : footers.size();
    }
}
