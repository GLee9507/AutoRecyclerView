package com.glee.planB;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

/**
 * @author liji
 * @date 2019/1/7 9:36
 * description
 */


public interface BindingItem {
    @IdRes
    int getBrId();

    @LayoutRes
    int getLayoutId();
}
