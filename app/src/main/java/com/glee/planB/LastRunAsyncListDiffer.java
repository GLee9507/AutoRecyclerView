package com.glee.planB;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author liji
 * @date 2019/1/7 15:23
 * description
 */


public class LastRunAsyncListDiffer<T> {
    private final ListUpdateCallback mUpdateCallback;
    final AsyncDifferConfig<T> mConfig;
    final Executor mMainThreadExecutor;
    private static final Executor sMainThreadExecutor = new LastRunAsyncListDiffer.MainThreadExecutor();
    @Nullable
    private List<T> mList;
    @NonNull
    private List<T> mReadOnlyList;
    int mMaxScheduledGeneration;

    public LastRunAsyncListDiffer(@NonNull RecyclerView.Adapter adapter, @NonNull DiffUtil.ItemCallback<T> diffCallback) {
        this((ListUpdateCallback) (new AdapterListUpdateCallback(adapter)), (AsyncDifferConfig) (new AsyncDifferConfig.Builder(diffCallback)).build());
    }

    @SuppressLint("RestrictedApi")
    public LastRunAsyncListDiffer(@NonNull ListUpdateCallback listUpdateCallback, @NonNull AsyncDifferConfig<T> config) {
        this.mReadOnlyList = Collections.emptyList();
        this.mUpdateCallback = listUpdateCallback;
        this.mConfig = config;
        if (config.getMainThreadExecutor() != null) {
            this.mMainThreadExecutor = config.getMainThreadExecutor();
        } else {
            this.mMainThreadExecutor = sMainThreadExecutor;
        }


    }

    @NonNull
    public List<T> getCurrentList() {
        return this.mReadOnlyList;
    }

    public void submitList(@Nullable final List<T> newList, final Runnable doLastRunnable) {
        final int runGeneration = ++this.mMaxScheduledGeneration;
        if (newList != this.mList) {
            if (newList == null) {
                int countRemoved = this.mList.size();
                this.mList = null;
                this.mReadOnlyList = Collections.emptyList();
                this.mUpdateCallback.onRemoved(0, countRemoved);
                if (doLastRunnable != null) {
                    doLastRunnable.run();
                }
            } else if (this.mList == null) {
                this.mList = new ArrayList<>(newList);
                this.mReadOnlyList = Collections.unmodifiableList(newList);
                this.mUpdateCallback.onInserted(0, newList.size());
                if (doLastRunnable != null) {
                    doLastRunnable.run();
                }
            } else {
                final List<T> oldList = this.mList;
                this.mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
                    public void run() {
                        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                            @Override
                            public int getOldListSize() {
                                return oldList.size();
                            }

                            @Override
                            public int getNewListSize() {
                                return newList.size();
                            }

                            @Override
                            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return LastRunAsyncListDiffer.this.mConfig.getDiffCallback().areItemsTheSame(oldItem, newItem);
                                } else {
                                    return oldItem == null && newItem == null;
                                }
                            }

                            @Override
                            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return LastRunAsyncListDiffer.this.mConfig.getDiffCallback().areContentsTheSame(oldItem, newItem);
                                } else if (oldItem == null && newItem == null) {
                                    return true;
                                } else {
                                    throw new AssertionError();
                                }
                            }

                            @Override
                            @Nullable
                            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return LastRunAsyncListDiffer.this.mConfig.getDiffCallback().getChangePayload(oldItem, newItem);
                                } else {
                                    throw new AssertionError();
                                }
                            }
                        });
                        LastRunAsyncListDiffer.this.mMainThreadExecutor.execute(new Runnable() {
                            public void run() {
                                if (LastRunAsyncListDiffer.this.mMaxScheduledGeneration == runGeneration) {
                                    LastRunAsyncListDiffer.this.latchList(newList, result);
                                    if (doLastRunnable != null) {
                                        doLastRunnable.run();
                                    }
                                }

                            }
                        });
                    }
                });
            }
        } else {
            if (doLastRunnable != null) {
                doLastRunnable.run();
            }
        }
    }

    void latchList(@NonNull List<T> newList, @NonNull DiffUtil.DiffResult diffResult) {
        this.mList = new ArrayList<>(newList);
        this.mReadOnlyList = Collections.unmodifiableList(newList);
        diffResult.dispatchUpdatesTo(this.mUpdateCallback);
    }

    private static class MainThreadExecutor implements Executor {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        MainThreadExecutor() {
        }

        @Override
        public void execute(@NonNull Runnable command) {
            this.mHandler.post(command);
        }
    }

    private static class BackgroundExecutor implements Executor {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        BackgroundExecutor() {
        }

        @Override
        public void execute(@NonNull Runnable command) {
            this.mHandler.post(command);
        }
    }


}
