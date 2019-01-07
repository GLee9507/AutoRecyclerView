package com.glee.planB;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/3 14:14
 * description
 */


public class BAdapter<T> extends RecyclerView.Adapter<BAdapter.BViewHolder> {
    private AAsyncDiffer<T> differ;
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
        return new BAdapter.BViewHolder(DataBindingUtil.inflate(inflater, i, viewGroup, false), i);
    }

    @Override
    public void onBindViewHolder(@NonNull BAdapter.BViewHolder bindingViewHolder, int i) {
        BindingItem bindingItem = getBindingItemByLayoutId(getItemLayoutIdByPosition(i));
        bindingViewHolder.bind(getBindingItemData(i), bindingItem.getBrId());
        checkLoad(i);
    }

    private void checkLoad(int position) {
        if (currentState != STATE_NORMAL
                || position < getHeaderCount() + getContentCount() - 1) {
            return;
        }
        currentState = STATE_LOADING;
    }

    private BindingItem getBindingItemByLayoutId(@LayoutRes int layoutId) {
        if (autoList.getLayoutId() == layoutId) {
            return autoList;
        }
        for (AutoList.HF header : headers) {
            if (header.getLayoutId() == layoutId) {
                return header;
            }
        }

        for (AutoList.HF footer : footers) {
            if (footer.getLayoutId() == layoutId) {
                return footer;
            }
        }
        throw new IllegalArgumentException();
    }

    private Object getBindingItemData(int position) {
        int headerCount = getHeaderCount();
        int contentCount = getContentCount();
        int footerCount = getFooterCount();
        if (headerCount > position) {
            return headers.get(position).getData();
        }

        if (footerCount > 0 && headerCount + contentCount - 1 < position) {
            return footers.get(position - contentCount - headerCount).getData();
        }

        return differ.getCurrentList().get(position - headerCount);

    }


    @Override
    public int getItemCount() {
        return getHeaderCount() + getContentCount() + getFooterCount();
    }


    private @LayoutRes
    int getItemLayoutIdByPosition(int position) {
        return getItemViewType(position);
    }

    @Override
    public int getItemViewType(int position) {
        int headerCount = getHeaderCount();
        int contentCount = getContentCount();
        if (position < headerCount) {
            return headers.get(position).getLayoutId();
        }

        if (position < contentCount + headerCount) {
            return autoList.getLayoutId();
        }

        return footers.get(position - headerCount - contentCount).getLayoutId();

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


    private void checkNullDiffer(@NonNull AutoList<T> list) {
        if (differ == null) {
            if (this.autoList == null) {
                this.autoList = list;
            }
            differ = new AAsyncDiffer<>(new ListUpdateCallback() {
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

    public void submitList(final AutoList<T> autoList) {
        checkNullDiffer(autoList);
        differ.submitList(autoList, new Runnable() {
            @Override
            public void run() {
                long l = System.nanoTime();
                calculateDiffHeaders(autoList);
                calculateDiffFooters(autoList);
                Log.d("glee9507", (System.nanoTime() - l) + "");
            }
        });
    }

    private void calculateDiffHeaders(final AutoList<T> autoList) {
        List<AutoList.HF> newHeaders = autoList.getHeaderList();
        if (isEmpty(headers)) {
            if (isEmpty(newHeaders)) {
                return;
            }
            headers = newHeaders;
            notifyItemRangeInserted(0, headers.size());
            return;
        }
        if (isEmpty(newHeaders)) {
            if (isEmpty(headers)) {
                return;
            }
            int size = headers.size();
            headers = newHeaders;
            notifyItemRangeRemoved(0, size);
            return;
        }

        int oldSize = headers.size();
        int newSize = newHeaders.size();

        headers = newHeaders;
        if (newSize > oldSize) {
            notifyItemRangeInserted(oldSize - 1, newSize - oldSize);
        } else if (newSize < oldSize) {
            notifyItemRangeRemoved(0, oldSize - newSize);
        }

        notifyItemRangeChanged(0, newSize);
    }

    private void calculateDiffFooters(final AutoList<T> autoList) {
        List<AutoList.HF> newFooters = autoList.getFooterList();
        if (isEmpty(footers)) {
            if (isEmpty(newFooters)) {
                return;
            }
            footers = newFooters;
            notifyItemRangeInserted(getHeaderCount() + getContentCount(), footers.size());
            return;
        }
        if (isEmpty(newFooters)) {
            if (isEmpty(footers)) {
                return;
            }
            int size = footers.size();
            footers = newFooters;
            notifyItemRangeRemoved(getHeaderCount() + getContentCount(), size);
            return;
        }

        int oldSize = footers.size();
        int newSize = newFooters.size();

        footers = newFooters;
        if (newSize > oldSize) {
            notifyItemRangeInserted(getHeaderCount() + getContentCount() + oldSize, newSize - oldSize);
        } else if (newSize < oldSize) {
            notifyItemRangeRemoved(getHeaderCount() + getContentCount(), oldSize - newSize);
        }

        notifyItemRangeChanged(getHeaderCount() + getContentCount(), newSize);
    }

    private boolean isEmpty(List list) {
        return list == null || list.size() == 0;
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




