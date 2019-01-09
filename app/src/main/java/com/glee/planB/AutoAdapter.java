package com.glee.planB;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/3 14:14
 * description
 */


public class AutoAdapter<T> extends RecyclerView.Adapter<AutoAdapter.BindingViewHolder> {
    /**
     * 自定义异步列表比较器
     */
    private LastRunAsyncListDiffer<T> differ;
    /**
     * 正常状态
     */
    static final int STATE_NORMAL = 1;
    /**
     * 加载中状态
     */
    static final int STATE_LOADING = 2;
    /**
     * 加载到尾部，无需继续加载
     */
    static final int STATE_END = 3;
    /**
     * 当前状态
     */
    private int currentState = STATE_NORMAL;
    private final Context context;
    private final LayoutInflater inflater;
    /**
     * 当前列表
     */
    private AutoList<T> autoList;
    /**
     * 头部
     */
    private List<AutoList.Hf> headers;
    /**
     * 尾部
     */
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
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BindingViewHolder(DataBindingUtil.inflate(inflater, i, viewGroup, false), i, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder bindingViewHolder, int i) {
        //绑定刷新数据
        bindingViewHolder.bind(getBindingItemData(i), getBrIdByPosition(i));
        checkLoad(i);
    }

    /**
     * 通过position获取刷新数据所需的brId
     *
     * @param pos position
     * @return brId
     */
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

    /**
     * 检查是否回调onLoad
     *
     * @param position onBindViewHolder position
     */
    private void checkLoad(int position) {
        if (currentState != STATE_NORMAL
                || position < getHeaderCount() + getContentCount() - 1) {
            return;
        }
        currentState = STATE_LOADING;
        AutoList.OnLoadListener onLoadListener = autoList.getOnLoadListener();
        if (onLoadListener != null) {
            onLoadListener.onLoad();
        }
    }

    /**
     * 通过position获取Item所需刷新的数据
     *
     * @param position position
     * @return data
     */
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


    /**
     * 获取ItemViewType
     * 返回的int即为 ItemView 的layoutId
     *
     * @param position position
     * @return type(layoutId)
     */
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


    /**
     * 检查differ是否为空，为空则创建
     *
     * @param list 列表
     */
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

    /**
     * 提交列表
     *
     * @param autoList 列表
     */
    public void submitList(final AutoList<T> autoList) {
        checkNullDiffer(autoList);
        Runnable runnable = null;
        //如果头或者尾需要刷新，则创建一个Runnable 在item刷新后执行
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
        currentState = autoList.getCurrentState();
        differ.submitList(autoList, runnable);
    }

    /**
     * 计算头是否需要插入、删除、刷新
     *
     * @param autoList 列表
     */
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

    /**
     * 计算尾是否需要插入、删除、刷新
     *
     * @param autoList 列表
     */
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


    /**
     * 使用DataBinding的ViewHolder
     */
    static class BindingViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding dataBinding;
        private final AutoAdapter autoAdapter;
        private int position = -1;

        BindingViewHolder(@NonNull ViewDataBinding dataBinding, int i, AutoAdapter autoAdapter) {
            super(dataBinding.getRoot());
            this.autoAdapter = autoAdapter;
            this.dataBinding = dataBinding;

        }

        <T> void bind(T item, int brId) {
            //brId == -1 说明View不需要数据
            if (dataBinding.setVariable(brId, item)) {
                dataBinding.executePendingBindings();
            }
            position = getAdapterPosition() - autoAdapter.getHeaderCount();
            View root = dataBinding.getRoot();
            if (autoAdapter.autoList.getOnItemClickListener() != null && !root.hasOnClickListeners()) {
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position >= 0) {
                            autoAdapter.autoList.getOnItemClickListener().onItemClick(position);
                        }
                    }
                });
            }
        }
    }
}




