package com.glee.autorecyclerview;

import android.support.v7.util.DiffUtil;

/**
 * @author liji
 * @date 2019/1/2 10:43
 * description
 */


public interface Differentiable<T> {
     DiffUtil.ItemCallback<T> getItemCallback();
}
