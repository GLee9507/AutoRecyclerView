package com.glee.planB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.glee.autorecyclerview.R;
import com.glee.autorecyclerview.BR;
import com.glee.autorecyclerview.TestBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/4 11:09
 * description@
 */


public class BViewModel extends AndroidViewModel {
    private AutoList.LiveDataBuilder.AutoListLiveData<TestBean> listLiveData
            = new AutoList.LiveDataBuilder<>(this, R.layout.item_test, BR.bean, TestBean.itemCallback)
            .mapHeader("header1", R.layout.header_test, BR.headerText, "HEADER")
            .mapFooter("footer1", R.layout.header_test, BR.headerText, "FOOTER")
            .loadMore(R.layout.item_load, new AutoList.OnLoadListener() {
                @Override
                public void onLoad() {
                    Log.d("glee9507", "onLoad");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AutoList<TestBean> value = listLiveData.getValue();
                            int size = value.size();
                            for (int i = 0; i < 5; i++) {
                                value.add(new TestBean((size + i) + ""));
                            }
                            value.loadComplete();
                            listLiveData.postValue(value);

                        }
                    }).start();
                }
            })
            .onItemClick(new AutoList.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    Toast.makeText(getApplication(), "" + pos, Toast.LENGTH_SHORT).show();
                }
            })
//            .mapFooter(R.layout.footer_test, BR.text)
            .build();

    @SuppressWarnings("uncheck")
    public BViewModel(Application application) {
        super(application);
        final AutoList<TestBean> value = listLiveData.getValue();
        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new TestBean(i + ""));
        }
        value.addAll(list);
        listLiveData.setValue(value);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AutoList<TestBean> value1 = listLiveData.getValue();
                    if (i >= 1) {
                        value1.update(i - 1).setHighlight(false);
                    }
                    value1.update(i).setHighlight(true);
                    listLiveData.postValue(value1);
                }

            }
        }).start();
    }

    public MutableLiveData<AutoList<TestBean>> getListLiveData() {
        return listLiveData;
    }
}
