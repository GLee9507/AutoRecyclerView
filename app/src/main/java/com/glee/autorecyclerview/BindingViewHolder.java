package com.glee.autorecyclerview;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author liji
 * @date 2018/12/29 17:12
 * description
 */


public class BindingViewHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;
    public BindingViewHolder(@NonNull ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
