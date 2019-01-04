package com.glee.autorecyclerview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.view.LayoutInflater;

import java.util.concurrent.Executor;

/**
 * @author liji
 * @date 2018/12/29 17:32
 * description
 */


public class Params<T> {
    private final @IdRes
    int layoutId;
    private final @IdRes
    int headersId;
    private final @IdRes
    int footersId;
    private final @IdRes
    int brId;
    private final @IdRes
    int headerBrId;
    private final @IdRes
    int footerBrId;

    private final Executor mainExecutor;
    private final Executor backgroundExecutor;

    public Params(
            int layoutId,
            int headersId,
            int footersId,
            int brId,
            int headerBrId,
            int footerBrId,
            Executor mainExecutor,
            Executor backgroundExecutor
    ) {
        this.layoutId = layoutId;
        this.headersId = headersId;
        this.footersId = footersId;
        this.brId = brId;
        this.headerBrId = headerBrId;
        this.footerBrId = footerBrId;
        this.mainExecutor = mainExecutor;
        this.backgroundExecutor = backgroundExecutor;
    }


}
