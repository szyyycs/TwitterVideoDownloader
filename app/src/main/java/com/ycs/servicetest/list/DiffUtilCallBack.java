package com.ycs.servicetest.list;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/05/20
 *     desc   :
 * </pre>
 */
public class DiffUtilCallBack extends DiffUtil.Callback {
    public List<ListItems> newList;
    public List<ListItems> oldList;

    public DiffUtilCallBack(List newList, List oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

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
        return newList.get(newItemPosition).getText().equals(oldList.get(oldItemPosition).getText());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getText().equals(oldList.get(oldItemPosition).getText());
    }
}
