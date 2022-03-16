package com.ycs.smartcanteen.ui.oneday;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.Bean;

import java.util.ArrayList;
import java.util.List;



public class OneDayViewModel extends ViewModel {
    private MutableLiveData<List<Bean.DishItem>> ld_itemList_bf;
    private MutableLiveData<List<Bean.DishItem>> ld_itemList_lun;
    private MutableLiveData<List<Bean.DishItem>> ld_itemList_din;
    private List<Bean.DishItem> itemListBf=new ArrayList<>();
    private List<Bean.DishItem> itemListLun=new ArrayList<>();
    private List<Bean.DishItem> itemListDin=new ArrayList<>();
    public OneDayViewModel(){
        ld_itemList_bf = new MutableLiveData<>();
        ld_itemList_lun = new MutableLiveData<>();
        ld_itemList_din = new MutableLiveData<>();

    }
    public LiveData<List<Bean.DishItem>> getBFList(int position){
        getDataBF(position);

        ld_itemList_bf.setValue(itemListBf);
        return ld_itemList_bf;
    }
    public LiveData<List<Bean.DishItem>> getLUNList(int position){
        getDataLUN(position);
        ld_itemList_lun.setValue(itemListLun);
        return ld_itemList_lun;
    }
    public LiveData<List<Bean.DishItem>> getDINList(int position){
        getDataDIN(position);
        ld_itemList_din.setValue(itemListDin);
        return ld_itemList_din;
    }

    public List<Bean.DishItem> getDataBF(int position){
        switch (position){
            case 0:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
            case 1:
                itemListBf.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
            case 2:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
            case 3:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
                itemListBf.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
        }


        return itemListBf;
    }
    public List<Bean.DishItem> getDataDIN(int position){
        itemListDin.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
        itemListDin.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
        itemListDin.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
        return itemListDin;
    }
    public List<Bean.DishItem> getDataLUN(int position){
        itemListLun.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
        itemListLun.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
        itemListLun.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
        return itemListLun;
    }
}