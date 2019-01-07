package com.glee.planB;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author liji
 * @date 2019/1/3 17:01
 * description
 */


public class AutoList<T> implements List<T>, BindingItem {
    private List<T> itemData = new ArrayList<>();
    private List<HF> headerList, footerList;
    @LayoutRes
    private int layoutId;
    @IdRes
    private int brId;
    private boolean shouldRefreshHeader, shouldRefreshFooter;
    private AsyncDifferConfig<T> config;

    private AutoList(
            int brId,
            int layoutId,
            List<HF> headerDataSet,
            List<HF> footersDataSet,
            AsyncDifferConfig<T> config
    ) {
        this.config = config;
        this.brId = brId;
        this.layoutId = layoutId;
        this.headerList = headerDataSet;
        this.footerList = footersDataSet;
    }

    public List<HF> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<HF> headerList) {
        this.headerList = headerList;
    }

    public List<HF> getFooterList() {
        return footerList;
    }

    public void setFooterList(List<HF> footerList) {
        this.footerList = footerList;
    }

    void addHeader(@LayoutRes int layoutId, @IdRes int brId) {
        headerList.add(new HF(layoutId, brId));
    }

    void addFooter(@LayoutRes int layoutId, @IdRes int brId) {
        footerList.add(new HF(layoutId, brId));
    }

    public void updateHeader(@LayoutRes int layoutId, Object o) {
        for (HF hf : headerList) {
            if (hf.layoutId == layoutId) {
                hf.setData(o);
                shouldRefreshHeader = true;
                return;
            }
        }
        throw new IllegalStateException();
    }

    public void updateFooter(@LayoutRes int layoutId, Object o) {
        for (HF hf : footerList) {
            if (hf.layoutId == layoutId) {
                hf.setData(o);
                shouldRefreshHeader = true;
                return;
            }
        }
        throw new IllegalStateException();
    }


    void refreshHeaderComplete() {
        shouldRefreshHeader = false;
    }

    void refreshFooterComplete() {
        shouldRefreshFooter = false;
    }

    public boolean isShouldRefreshHeader() {
        return shouldRefreshHeader;
    }

    public void setShouldRefreshHeader(boolean shouldRefreshHeader) {
        this.shouldRefreshHeader = shouldRefreshHeader;
    }

    public boolean isShouldRefreshFooter() {
        return shouldRefreshFooter;
    }

    public void setShouldRefreshFooter(boolean shouldRefreshFooter) {
        this.shouldRefreshFooter = shouldRefreshFooter;
    }

    @Override
    public int getBrId() {
        return brId;
    }

    @Override
    public int getLayoutId() {
        return layoutId;
    }

    public static class LiveDataBuilder<T> {
        @IdRes
        private int brId;
        @LayoutRes
        private int layoutId;
        private AsyncDifferConfig<T> config;
        private List<HF> headerDataSet, footersDataSet;
        private final DiffUtil.ItemCallback<T> itemCallback;

        public LiveDataBuilder(@LayoutRes int layoutId, @IdRes int brId, @Nullable DiffUtil.ItemCallback<T> itemCallback) {
            this.layoutId = layoutId;
            this.brId = brId;
            if (itemCallback == null) {
                itemCallback = new DiffUtil.ItemCallback<T>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull T t, @NonNull T t1) {
                        return false;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull T t, @NonNull T t1) {
                        return false;
                    }
                };
            }
            this.itemCallback = itemCallback;
        }

        public LiveDataBuilder<T> mapFooter(@LayoutRes int layoutId, @IdRes int brId, @Nullable Object data) {
            getFootersDataSet().add(new HF(layoutId, brId, data));
            return this;
        }

        public LiveDataBuilder<T> mapHeader(@LayoutRes int layoutId, @IdRes int brId, @Nullable Object data) {
            getHeaderDataSet().add(new HF(layoutId, brId, data));
            return this;
        }

        public LiveDataBuilder<T> mapFooter(@LayoutRes int layoutId, @IdRes int brId) {
            return mapFooter(layoutId, brId, null);

        }

        public LiveDataBuilder<T> mapHeader(@LayoutRes int layoutId, @IdRes int brId) {
            return mapHeader(layoutId, brId, null);
        }

        public MutableLiveData<AutoList<T>> build() {
            MutableLiveData<AutoList<T>> liveData = new MutableLiveData<>();
            assert itemCallback != null;
            config = new AsyncDifferConfig.Builder<>(itemCallback).build();
            liveData.setValue(new AutoList<>(brId, layoutId, headerDataSet, footersDataSet, config));
            return liveData;
        }

        private List<HF> getFootersDataSet() {
            if (footersDataSet == null) {
                footersDataSet = new ArrayList<>();
            }
            return footersDataSet;
        }


        private List<HF> getHeaderDataSet() {
            if (headerDataSet == null) {
                headerDataSet = new ArrayList<>();
            }
            return headerDataSet;
        }
    }


    @Override
    public int size() {
        return itemData.size();
    }

    @Override
    public boolean isEmpty() {
        return itemData.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return itemData.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return itemData.iterator();
    }

    @Override
    public Object[] toArray() {
        return itemData.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return itemData.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return itemData.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return itemData.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return itemData.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return itemData.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return itemData.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return itemData.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return itemData.retainAll(c);
    }

    @Override
    public void clear() {
        itemData.clear();
    }

    @Override
    public T get(int index) {
        return itemData.get(index);
    }

    @Override
    public T set(int index, T element) {
        return itemData.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        itemData.add(index, element);
    }

    @Override
    public T remove(int index) {
        return itemData.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return itemData.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return itemData.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return itemData.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return itemData.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return itemData.subList(fromIndex, toIndex);
    }


    public AsyncDifferConfig<T> getConfig() {
        return config;
    }

    static class HF implements BindingItem {
        @IdRes
        private final int brId;

        @LayoutRes
        private final int layoutId;

        private Object data;

        public HF(int brId, int layoutId) {
            this.brId = brId;
            this.layoutId = layoutId;
        }

        public HF(int layoutId, int brId, Object data) {
            this.data = data;
            this.brId = brId;
            this.layoutId = layoutId;
        }

        @Override
        public int getBrId() {
            return brId;
        }

        @Override
        public int getLayoutId() {
            return layoutId;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
