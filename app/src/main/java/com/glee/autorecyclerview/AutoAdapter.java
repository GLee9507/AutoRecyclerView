package com.glee.autorecyclerview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2018/12/29 17:12
 * description
 */


public class AutoAdapter<T extends Differentiable<T>> extends RecyclerView.Adapter<BindingViewHolder> {
    private AsyncListDiffer<T> differ;
    private LinearLayoutCompat headerView, footerView;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = -1;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_LOADING = 2;
    private static final int STATE_END = 3;
    private final Context context;
    private final LayoutInflater inflater;
    private final AutoBinder<T> autoBinder;
    private int pageNum = 0;
    private int currentState = STATE_NORMAL;

    public AutoAdapter(Context context, AutoBinder<T> autoBinder) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.autoBinder = autoBinder;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BindingViewHolder(DataBindingUtil.inflate(inflater, autoBinder.getLayoutId(), viewGroup, false), i);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder bindingViewHolder, int i) {
        bindingViewHolder.bind(getItem(i), autoBinder.getBrId());
        checkLoad(i);
    }

    private void checkLoad(int position) {
        if (currentState != STATE_NORMAL
                || position < getHeaderCount() + getContentCount() - 1) {
            return;
        }
        currentState = STATE_LOADING;
        autoBinder.getLoadListener().onLoad(pageNum + 1, loadCallback);
    }

    private LoadCallback<T> loadCallback = new LoadCallback<T>() {
        @Override
        public void onResult(List<T> result, int page) {
            List<T> list = new ArrayList<>(differ.getCurrentList());
            list.addAll(result);
            submitList(list, page);
        }
    };

//    private int getBrIdByPosition(int position) {
//        switch (getItemViewType(position)) {
//            case TYPE_ITEM:
//                return autoBinder.getBrId();
//            case TYPE_HEADER:
//            case TYPE_FOOTER:
//            default:
//        }
//    }

    private T getItem(int position) {
        return differ.getCurrentList().get(position - getHeaderCount());
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getContentCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return TYPE_HEADER;
        }

        if (position < getContentCount()) {
            return TYPE_ITEM;
        }

        return TYPE_FOOTER;

    }

    private int getContentCount() {
        return differ.getCurrentList().size();
    }

    private int getHeaderCount() {
        return headerView == null ? 0 : 1;
    }

    private int getFooterCount() {
        return footerView == null ? 0 : 1;
    }

    private LinearLayoutCompat getHeaderView() {
        if (headerView == null) {
            headerView = new LinearLayoutCompat(context);
        }
        return headerView;
    }

    private LinearLayoutCompat getFooterView() {
        if (footerView != null) {
            footerView = new LinearLayoutCompat(context);
        }
        return footerView;
    }


    private void submitList(@NonNull List<T> list, int page) {
        checkNullDiffer(list);
        differ.submitList(list);
        this.pageNum = page;
    }

    private void checkNullDiffer(@NonNull List<T> list) {
        if (differ == null && list.size() > 0) {
            differ = new AsyncListDiffer<>(new ListUpdateCallback() {
                @Override
                public void onInserted(int i, int i1) {
                    notifyItemRangeInserted(i + getHeaderCount(), i1 + getHeaderCount());
                }

                @Override
                public void onRemoved(int i, int i1) {
                    notifyItemRangeRemoved(i + getHeaderCount(), i1 + getHeaderCount());
                }

                @Override
                public void onMoved(int i, int i1) {
                    notifyItemMoved(i + getHeaderCount(), i1 + getHeaderCount());
                }

                @Override
                public void onChanged(int i, int i1, @Nullable Object o) {
                    notifyItemRangeChanged(i + getHeaderCount(), i1 + getHeaderCount(), o);
                }
            }, new AsyncDifferConfig.Builder<>(list.get(0).getItemCallback()).build());
        }
    }


    public interface LoadCallback<T> {
        void onResult(List<T> result, int page);
    }
}
