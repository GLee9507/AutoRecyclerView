package com.glee.planB;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.glee.autorecyclerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/3 14:14
 * description
 */


public class AutoAdapter<T> extends RecyclerView.Adapter<AutoAdapter.BViewHolder> {
    private LastRunAsyncListDiffer<T> differ;
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
    private List<AutoList.Hf> headers;
    private List<AutoList.Hf> footers;

    public List<AutoList.Hf> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        return headers;
    }


    public List<AutoList.Hf> getFooters() {
        if (footers == null) {
            footers = new ArrayList<>();
        }
        return footers;
    }


    public AutoAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setItemAnimator(null);
    }

    @NonNull
    @Override
    public AutoAdapter.BViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AutoAdapter.BViewHolder(DataBindingUtil.inflate(inflater, i, viewGroup, false), i);
    }

    @Override
    public void onBindViewHolder(@NonNull AutoAdapter.BViewHolder bindingViewHolder, int i) {

        bindingViewHolder.bind(getBindingItemData(i), getBrIdByPosition(i));
        checkLoad(i);
    }

    @IdRes
    private int getBrIdByPosition(int pos) {
        int layoutId = getItemViewType(pos);
        if (layoutId == autoList.getLayoutId()) {
            return autoList.getBrId();
        }
        if (headers != null) {
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                AutoList.Hf hf = headers.get(i);
                if (hf.getLayoutId() == layoutId) {
                    return hf.getBrId();
                }
            }
        }

        if (footers != null) {
            int size = footers.size();
            for (int i = 0; i < size; i++) {
                AutoList.Hf hf = footers.get(i);
                if (hf.getLayoutId() == layoutId) {
                    return hf.getBrId();
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private void checkLoad(int position) {
        if (currentState != STATE_NORMAL
                || position < getHeaderCount() + getContentCount() - 1) {
            return;
        }
        currentState = STATE_LOADING;
    }

//    private BindingItem getBindingItemByLayoutId(@LayoutRes int layoutId) {
//        if (autoList.getLayoutId() == layoutId) {
//            return autoList;
//        }
//        for (AutoList.Hf header : headers) {
//            if (header.getLayoutId() == layoutId) {
//                return header;
//            }
//        }
//
//        for (AutoList.Hf footer : footers) {
//            if (footer.getLayoutId() == layoutId) {
//                return footer;
//            }
//        }
//        throw new IllegalArgumentException();
//    }

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
            differ = new LastRunAsyncListDiffer<>(new ListUpdateCallback() {
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
        Runnable runnable = null;
        if (autoList.isShouldRefreshFooter() || autoList.isShouldRefreshHeader()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    long l = System.nanoTime();
                    if (autoList.isShouldRefreshHeader()) {
                        calculateDiffHeaders(autoList);
                        autoList.refreshHeaderComplete();
                    }
                    if (autoList.isShouldRefreshFooter()) {
                        calculateDiffFooters(autoList);
                        autoList.refreshFooterComplete();
                    }
                    Log.d("glee9507", (System.nanoTime() - l) + "");
                }
            };
        }

        differ.submitList(autoList, runnable);
    }

    private void calculateDiffHeaders(final AutoList<T> autoList) {

        final List<AutoList.Hf> newHeaders = autoList.getHeaderList();
        final int oldSize = getHeaderCount();
        final int newSize = newHeaders == null ? 0 : newHeaders.size();

        if (headers != null) {
            headers.clear();
        }
        if (newSize > 0) {
            getHeaders().addAll(newHeaders);
        }
        if (newSize > oldSize) {
            notifyItemRangeInserted(0, newSize - oldSize);
        } else if (newSize < oldSize) {
            notifyItemRangeRemoved(0, oldSize - newSize);
        }
        notifyItemRangeChanged(0, getHeaderCount());

    }

    private void calculateDiffFooters(final AutoList<T> autoList) {
        final List<AutoList.Hf> newFooters = autoList.getFooterList();
        final int oldSize = getFooterCount();
        final int newSize = newFooters == null ? 0 : newFooters.size();
        if (footers != null) {
            footers.clear();
        }
        if (newSize > 0) {
            getFooters().addAll(newFooters);
        }
        if (newSize > oldSize) {
            notifyItemRangeInserted(getHeaderCount() + getContentCount(), newSize - oldSize);
        } else if (newSize < oldSize) {
            notifyItemRangeRemoved(getHeaderCount() + getContentCount(), oldSize - newSize);
        }
        notifyItemRangeChanged(getHeaderCount() + getContentCount(), getFooterCount());

    }

    private boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    static class BViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding dataBinding;

        BViewHolder(@NonNull ViewDataBinding dataBinding, int i) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        public <T> void bind(T item, int brId) {
            dataBinding.setVariable(brId, item);
            dataBinding.executePendingBindings();
        }
    }
}




