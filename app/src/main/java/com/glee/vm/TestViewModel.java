package com.glee.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.glee.autorecyclerview.AutoAdapter;
import com.glee.autorecyclerview.AutoBinder;
import com.glee.autorecyclerview.BR;
import com.glee.autorecyclerview.R;
import com.glee.autorecyclerview.TestBean;

import java.util.ArrayList;

/**
 * @author liji
 * @date 2019/1/2 16:28
 * description
 */


public class TestViewModel extends ViewModel {
    private MutableLiveData<String> textData = new MutableLiveData<>();

    public LiveData<String> getTextData() {
        return textData;
    }

    AutoBinder build = new AutoBinder.Builder<TestBean>(this)
            .load(new AutoBinder.LoadListener<TestBean>() {
                @Override
                public void onLoad(int page, AutoAdapter.LoadCallback<TestBean> callback) {
                    ArrayList<TestBean> list = new ArrayList<>();
                    for (int i = 0; i < (page + 1) * 30; i++) {
                        list.add(new TestBean(i + ""));
                    }
                    callback.onResult(list, page);
                }
            })
            .itemMap(R.layout.item_test, BR.bean).build();

    public TestViewModel() {
        LiveData<PagedList<TestBean>> liveData = new LivePagedListBuilder<>(new DataSource.Factory<Integer, TestBean>() {
            @Override
            public DataSource<Integer, TestBean> create() {
                return new PageKeyedDataSource<Integer, TestBean>() {
                    @Override
                    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, TestBean> callback) {

                    }

                    @Override
                    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TestBean> callback) {

                    }

                    @Override
                    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TestBean> callback) {

                    }
                };
            }
        }, new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .build()
        ).build();

    }
}
