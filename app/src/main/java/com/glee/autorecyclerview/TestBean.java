package com.glee.autorecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

/**
 * @author liji
 * @date 2019/1/2 10:44
 * description
 */


public class TestBean {
    private String text;
    private boolean highlight;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public static DiffUtil.ItemCallback<TestBean> getItemCallback() {
        return itemCallback;
    }

    public static void setItemCallback(DiffUtil.ItemCallback<TestBean> itemCallback) {
        TestBean.itemCallback = itemCallback;
    }

    public TestBean(String text) {

        this.text = text;
    }

    public static DiffUtil.ItemCallback<TestBean> itemCallback = new DiffUtil.ItemCallback<TestBean>()

    {
        @Override
        public boolean areItemsTheSame(@NonNull TestBean testBean, @NonNull TestBean t1) {
            return testBean.equals(t1);
        }

        @Override
        public boolean areContentsTheSame(@NonNull TestBean testBean, @NonNull TestBean t1) {
            return testBean.text.equals(t1.text) && testBean.isHighlight() == t1.isHighlight();
        }
    };

}
