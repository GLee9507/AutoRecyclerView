package com.glee.planB;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.glee.autorecyclerview.R;
import com.glee.autorecyclerview.BR;
import com.glee.autorecyclerview.TestBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liji
 * @date 2019/1/4 11:09
 * description@
 */


public class BViewModel extends ViewModel {
    private AutoList.LiveDataBuilder.AutoListLiveData<TestBean> listLiveData = new AutoList.LiveDataBuilder<TestBean>(R.layout.item_test, BR.bean, TestBean.itemCallback)
            .mapHeader("header1", R.layout.header_test, BR.headerText,"HEADER")
            .mapFooter("footer1", R.layout.header_test, BR.headerText,"FOOTER")
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
                            for (int i = 0; i < 5; i++) {
                                value.add(new TestBean("aaa"));
                            }
                            value.loadComplete();
                            listLiveData.postValue(value);

                        }
                    }).start();
                }
            })
//            .mapFooter(R.layout.footer_test, BR.text)
            .build();

    @SuppressWarnings("uncheck")
    public BViewModel() {

        AutoList<TestBean> value = listLiveData.getValue();
        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new TestBean(i + ""));
        }
        value.addAll(list);
        listLiveData.setValue(value);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i = 0;
//                while (true) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    i++;
//                    AutoList<TestBean> value1 = listLiveData.getValue();
////                    value1.addFooter("FOOT", R.layout.header_test, BR.headerText, "" + i);
////                    value1.updateHeader("header1", i + "");
////                    value1.add(new TestBean("aa"));
//                    listLiveData.postValue(value1);
//                }
//            }
//        }).start();
    }

    public MutableLiveData<AutoList<TestBean>> getListLiveData() {
        return listLiveData;
    }
}
