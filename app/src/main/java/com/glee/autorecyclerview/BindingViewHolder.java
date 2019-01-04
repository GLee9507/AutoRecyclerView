package com.glee.autorecyclerview;

import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
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
    private final int viewType;

    public BindingViewHolder(@NonNull ViewDataBinding binding, int viewType) {
        super(binding.getRoot());
        this.binding = binding;
        this.viewType = viewType;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public void bind(Object object, @IdRes int brId) {
        binding.setVariable(brId, object);
        binding.executePendingBindings();
    }
}
