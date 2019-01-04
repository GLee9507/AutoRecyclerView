package com.glee.planB;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.glee.autorecyclerview.AutoBinder;

import java.util.List;

/**
 * @author liji
 * @date 2019/1/3 14:14
 * description
 */


public class BAdapter<T> extends RecyclerView.Adapter<BAdapter.BViewHolder> {
    private AsyncListDiffer<T> differ;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = -1;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_LOADING = 2;
    private static final int STATE_END = 3;
    private final Context context;
    private final LayoutInflater inflater;
    private int pageNum = 0;
    private int currentState = STATE_NORMAL;
    private AutoList<T> autoList;
    private List<AutoList.HF> headers;
    private List<AutoList.HF> footers;

    public BAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BAdapter.BViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BAdapter.BViewHolder(DataBindingUtil.inflate(inflater, autoList.getLayoutId(), viewGroup, false), i);
    }

    @Override
    public void onBindViewHolder(@NonNull BAdapter.BViewHolder bindingViewHolder, int i) {
        bindingViewHolder.bind(getItem(i), autoList.getBrId());
        checkLoad(i);
    }

    private void checkLoad(int position) {
        if (currentState != STATE_NORMAL
                || position < getHeaderCount() + getContentCount() - 1) {
            return;
        }
        currentState = STATE_LOADING;
    }


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
        return autoList.getHeaderList() == null ? 0 : autoList.getHeaderList().size();
    }

    private int getFooterCount() {
        return autoList.getFooterList() == null ? 0 : autoList.getFooterList().size();
    }


    private void checkNullDiffer(@NonNull AutoList<T> list) {
        if (differ == null) {
            if (this.autoList == null) {
                this.autoList = list;
            }
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
            }, list.getConfig());
        }
    }

    public void submitList(AutoList<T> autoList) {
        checkNullDiffer(autoList);
        diffHeadersAndFooters(autoList);
        differ.submitList(autoList);
    }

    private void diffHeadersAndFooters(AutoList<T> autoList) {
        if (autoList.getHeaderList() != null) {
            if (headers == null) {
                headers = autoList.getHeaderList();
                notifyItemRangeInserted(0, autoList.getHeaderList().size());
            }
            if (autoList.isShouldRefreshHeader()) {
                headers.clear();
//                notifyItemRemoved();
                headers.addAll(autoList.getHeaderList());
//                notifyItemInserted();
            }
        }

    }

    static class BViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding dataBinding;

        public BViewHolder(@NonNull ViewDataBinding dataBinding, int i) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        public <T> void bind(T item, int brId) {
            dataBinding.setVariable(brId, item);
            dataBinding.executePendingBindings();
        }
    }
}




