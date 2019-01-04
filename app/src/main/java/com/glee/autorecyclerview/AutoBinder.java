package com.glee.autorecyclerview;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/2 14:26
 * description
 */


public class AutoBinder<T>{
    private final Object liveDataOwner;
    private final @LayoutRes
    int layoutId;
    private final @LayoutRes
    int brId;
    private final List<Pair<Integer, Integer>> headerIds, footerIds;
    private LoadListener<T> loadListener;

    public AutoBinder(Object liveDataOwner, LoadListener<T> loadListener, int layoutId, int brId, List<Pair<Integer, Integer>> headerIds, List<Pair<Integer, Integer>> footerIds) {
        this.liveDataOwner = liveDataOwner;
        this.layoutId = layoutId;
        this.brId = brId;
        this.loadListener = loadListener;
        this.headerIds = headerIds;
        this.footerIds = footerIds;
    }

    public static <T> AutoBinder.Builder with(@NonNull Object liveDataOwner) {
        return new AutoBinder.Builder<T>(liveDataOwner);
    }


    public static class Builder<T> {
        private Object liveDataOwner;
        private @LayoutRes
        int layoutId;
        private @LayoutRes
        int brId;
        private List<Pair<Integer, Integer>> headerIds, footerIds;
        private LoadListener<T> loadListener;


        public Builder(Object liveDataOwner) {
            this.liveDataOwner = liveDataOwner;
        }

        public Builder itemMap(@LayoutRes int layoutId, @IdRes int brId) {
            this.layoutId = layoutId;
            this.brId = brId;
            return this;
        }

        public Builder headerMap(@LayoutRes int layoutId, @IdRes int brId) {
            if (headerIds == null) {
                headerIds = new ArrayList<>();
            }
            headerIds.add(Pair.create(layoutId, brId));
            return this;
        }

        public Builder footerMap(@LayoutRes int layoutId, @IdRes int brId) {
            if (footerIds == null) {
                footerIds = new ArrayList<>();
            }
            footerIds.add(Pair.create(layoutId, brId));
            return this;
        }

        public Builder load(LoadListener<T> loadListener) {
            this.loadListener = loadListener;
            return this;
        }

        public AutoBinder build() {
            return new AutoBinder<>(liveDataOwner, loadListener, layoutId, brId, headerIds, footerIds);
        }
    }

    public interface LoadListener<T> {
        void onLoad(int page, AutoAdapter.LoadCallback<T> callback);
    }

    public Object getLiveDataOwner() {
        return liveDataOwner;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public int getBrId() {
        return brId;
    }

    public List<Pair<Integer, Integer>> getHeaderIds() {
        return headerIds;
    }

    public List<Pair<Integer, Integer>> getFooterIds() {
        return footerIds;
    }

    public LoadListener<T> getLoadListener() {
        return loadListener;
    }
}
