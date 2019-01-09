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
import java.util.Objects;

/**
 * @author liji
 * @date 2019/1/3 17:01
 * description
 */


public class AutoList<T> implements List<T> {
    private OnLoadListener onLoadListener;
    private List<T> itemData = new ArrayList<>();
    private List<Hf> headerList, footerList;
    @LayoutRes
    private int layoutId, loadLayoutId;
    private boolean enableLoad;
    @IdRes
    private int brId;
    private boolean shouldRefreshHeader, shouldRefreshFooter;
    private AsyncDifferConfig<T> config;
    private int currentState = AutoAdapter.STATE_NORMAL;

    private AutoList(
            int brId,
            int layoutId,
            List<Hf> headerDataSet,
            List<Hf> footersDataSet,
            AsyncDifferConfig<T> config,
            OnLoadListener onLoadListener
    ) {
        this.onLoadListener = onLoadListener;
        this.config = config;
        this.brId = brId;
        this.layoutId = layoutId;
        this.headerList = headerDataSet;
        this.footerList = footersDataSet;
        if (headerList != null) {
            shouldRefreshHeader = true;
        }

        if (footerList != null) {
            shouldRefreshFooter = true;
        }
    }

    public List<Hf> getHeaderList() {
        return headerList;
    }


    public List<Hf> getFooterList() {
        return footerList;
    }

    public void addHeader(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId) {
        addHeader(key, layoutId, brId, null);
    }

    public void addFooter(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId) {
        addFooter(key, layoutId, brId, null);
    }

    public void addHeader(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId, Object data) {
        if (headerList == null) {
            headerList = new ArrayList<>();
        }
        int size = headerList.size();
        for (int i = 0; i < size; i++) {
            if (Objects.equals(headerList.get(i).getKey(), key)) {
                updateHeader(key, data);
                return;
            }
        }
        headerList.add(new Hf(key, layoutId, brId, data));
//        getNeedHeaderRefreshKeys().add(key);
        shouldRefreshHeader = true;
    }

    public void addFooter(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId, Object data) {
        if (footerList == null) {
            footerList = new ArrayList<>();
        }
        int size = footerList.size();

        for (int i = size - 1; i > -1; i--) {
            Hf hf = footerList.get(i);
            if (Objects.equals(hf.getKey(), key)) {
                hf.setData(data);
                shouldRefreshFooter = true;
                return;
            }
        }
        if (size > 0 && Objects.equals(footerList.get(size - 1).getKey(), LiveDataBuilder.LOAD_VIEW_KTY)) {
            footerList.add(size - 1, new Hf(key, layoutId, brId, data));
        } else {
            footerList.add(new Hf(key, layoutId, brId, data));
        }

        shouldRefreshFooter = true;
    }

    public void removeHeader(@NonNull String key) {
        int size = headerList.size();
        for (int i = 0; i < size; i++) {
            if (key.equals(headerList.get(i).getKey())) {
                headerList.remove(i);
//                getNeedHeaderRefreshKeys().add(key);
                shouldRefreshHeader = true;
                break;
            }
        }
    }

    public void removeFooter(@NonNull String key) {
        int size = footerList.size();
        for (int i = 0; i < size; i++) {
            if (key.equals(footerList.get(i).getKey())) {
                footerList.remove(i);
//                getNeedFooterRefreshKeys().add(key);
                shouldRefreshFooter = true;
                break;
            }
        }
    }


    public void updateHeader(@NonNull String key, Object o) {
        int size = headerList.size();
        for (int i = 0; i < size; i++) {
            Hf hf = headerList.get(i);
            if (key.equals(hf.getKey())) {
                hf.setData(o);
//                getNeedHeaderRefreshKeys().add(key);
                shouldRefreshHeader = true;
                break;
            }
        }
    }

    public void updateFooter(@NonNull String key, Object o) {
        int size = footerList.size();
        for (int i = 0; i < size; i++) {
            Hf hf = footerList.get(i);
            if (key.equals(hf.getKey())) {
                hf.setData(o);
//                getNeedFooterRefreshKeys().add(key);
                shouldRefreshFooter = true;
                break;
            }
        }
    }

    public void updateLoadView(Object o) {
        int size = footerList.size();
        for (int i = size - 1; i > -1; i--) {
            Hf hf = footerList.get(i);
            if (Objects.equals(hf.getKey(), LiveDataBuilder.LOAD_VIEW_KTY)) {
                hf.setData(o);
                shouldRefreshFooter = true;
                return;
            }
        }
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


    public boolean isShouldRefreshFooter() {
        return shouldRefreshFooter;
    }


    public int getBrId() {
        return brId;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public OnLoadListener getOnLoadListener() {
        return onLoadListener;
    }

    public boolean isEnableLoad() {
        return enableLoad;
    }

    public int getLoadLayoutId() {
        return loadLayoutId;
    }

    public void loadComplete() {
        currentState = AutoAdapter.STATE_NORMAL;
    }

    public int getCurrentState() {
        return currentState;
    }

    public static class LiveDataBuilder<T> {
        @IdRes
        private int brId;
        @LayoutRes
        private int layoutId;
        private AsyncDifferConfig<T> config;
        private List<Hf> headerDataSet, footersDataSet;
        private final DiffUtil.ItemCallback<T> itemCallback;
        public static final String LOAD_VIEW_KTY = "loadView_glee";
        private OnLoadListener onLoadListener;

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

        public LiveDataBuilder(@LayoutRes int layoutId, @IdRes int brId) {
            this(layoutId, brId, null);
        }

        public LiveDataBuilder<T> mapFooter(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId, @Nullable Object data) {
            List<Hf> footersDataSet = getFootersDataSet();
            Hf hf = new Hf(key, layoutId, brId, data);
            int index = footersDataSet.size() - 1;
            if (index > -1 && Objects.equals(footersDataSet.get(index).getKey(), LOAD_VIEW_KTY)) {
                footersDataSet.add(index, hf);
            } else {
                footersDataSet.add(hf);
            }
            return this;
        }

        public LiveDataBuilder<T> mapHeader(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId, @Nullable Object data) {
            getHeaderDataSet().add(new Hf(key, layoutId, brId, data));
            return this;
        }

        public LiveDataBuilder<T> mapFooter(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId) {
            return mapFooter(key, layoutId, brId, null);

        }

        public LiveDataBuilder<T> mapHeader(@NonNull String key, @LayoutRes int layoutId, @IdRes int brId) {
            return mapHeader(key, layoutId, brId, null);
        }

        public LiveDataBuilder<T> loadMore(@LayoutRes int layoutId, @IdRes int brId, OnLoadListener onLoadListener) {
            return loadMore(layoutId, brId, null, onLoadListener);
        }

        public LiveDataBuilder<T> loadMore(@LayoutRes int layoutId, OnLoadListener onLoadListener) {
            return loadMore(layoutId, -1, null, onLoadListener);
        }

        public LiveDataBuilder<T> loadMore(@LayoutRes int layoutId, @IdRes int brId, Object data, OnLoadListener onLoadListener) {
            this.onLoadListener = onLoadListener;
            return mapFooter(LOAD_VIEW_KTY, layoutId, brId, data);
        }

        public AutoListLiveData<T> build() {
            AutoListLiveData<T> liveData = new AutoListLiveData<>();
            assert itemCallback != null;
            config = new AsyncDifferConfig.Builder<>(itemCallback).build();
            liveData.setValue(new AutoList<>(brId, layoutId, headerDataSet, footersDataSet, config, onLoadListener));
            return liveData;
        }

        private List<Hf> getFootersDataSet() {
            if (footersDataSet == null) {
                footersDataSet = new ArrayList<>();
            }
            return footersDataSet;
        }


        private List<Hf> getHeaderDataSet() {
            if (headerDataSet == null) {
                headerDataSet = new ArrayList<>();
            }
            return headerDataSet;
        }

        public static class AutoListLiveData<T> extends MutableLiveData<AutoList<T>> {
            @NonNull
            @Override
            public AutoList<T> getValue() {
                return super.getValue();
            }
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

    public interface OnLoadListener {
        void onLoad();
    }

    static class Hf {
        @IdRes
        private int brId;

        @LayoutRes
        private int layoutId;

        private Object data;
        private String key;

        Hf(String key, int layoutId, int brId) {
            this(key, layoutId, brId, null);
        }

        Hf(String key, int layoutId, int brId, Object data) {
            this.data = data;
            this.brId = brId;
            this.layoutId = layoutId;
            this.key = key;
        }

        public int getBrId() {
            return brId;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public <T> T getData() {
            return (T) data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getKey() {
            return key;
        }
    }
}
