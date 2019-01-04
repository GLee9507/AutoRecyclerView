package com.glee.autorecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

/**
 * @author liji
 * @date 2019/1/2 10:44
 * description
 */


public class TestBean implements Differentiable<TestBean> {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TestBean(String text) {

        this.text = text;
    }

    @Override
    public DiffUtil.ItemCallback<TestBean> getItemCallback() {
        return new DiffUtil.ItemCallback<TestBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull TestBean testBean, @NonNull TestBean t1) {
                return testBean.equals(t1);
            }

            @Override
            public boolean areContentsTheSame(@NonNull TestBean testBean, @NonNull TestBean t1) {
                return testBean.text.equals(t1.text);
            }
        };
    }
}
