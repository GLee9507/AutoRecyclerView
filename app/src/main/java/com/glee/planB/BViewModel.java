package com.glee.planB;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.glee.autorecyclerview.R;
import com.glee.autorecyclerview.BR;
import com.glee.autorecyclerview.TestBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/4 11:09
 * description
 */


public class BViewModel extends ViewModel {
    private MutableLiveData<AutoList<TestBean>> listLiveData = new AutoList.LiveDataBuilder<TestBean>(R.layout.item_test, BR.bean, null)
            .mapHeader(R.layout.header_test, BR.headerText, null)
            .mapFooter(R.layout.footer_test, BR.text, null)
            .build();

    public BViewModel() {
        AutoList<TestBean> value = listLiveData.getValue();
        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new TestBean(i + ""));
        }
        value.addAll(list);
        listLiveData.setValue(value);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    AutoList<TestBean> value1 = listLiveData.getValue();
                    value1.add(new TestBean(""+(i+99)));
                    value1.updateHeader(R.layout.header_test, i + "");
                    value1.updateFooter(R.layout.footer_test, i + "footer");
                    listLiveData.postValue(value1);
                }
            }
        }).start();
    }

    public MutableLiveData<AutoList<TestBean>> getListLiveData() {
        return listLiveData;
    }
}
