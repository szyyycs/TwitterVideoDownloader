package com.ycs.smartcanteen.ui.oneday;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.Bean;
import com.ycs.smartcanteen.ui.CustomLinearLayoutManager;
import com.ycs.smartcanteen.ui.adapter.CookAdapter;
import com.ycs.smartcanteen.ui.cookbook.CookbookFragment;
import com.ycs.smartcanteen.ui.cookbook.CookbookViewModel;
import com.ycs.smartcanteen.util.MyViewPager;
import com.ycs.smartcanteen.util.WindowsUtil;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OneDayFragment extends Fragment {

    private OneDayViewModel mViewModel;
    private View root;
    private RecyclerView rv_breakfast;
    private RecyclerView rv_lunch;
    private RecyclerView rv_dinner;
    private CookAdapter bfAdapter;
    private CookAdapter lunAdapter;
    private Context context;
    private CookAdapter dinAdapter;
    private ArrayList<Bean.DishItem> itemListBf=new ArrayList<>();
    private ArrayList<Bean.DishItem> itemListLun=new ArrayList<>();
    private ArrayList<Bean.DishItem> itemListDin=new ArrayList<>();
    private MyViewPager vp;
    private int position;
    private String[] date;
    private int popupWidth;
    CookbookViewModel cvm;
    Activity activity;
//    private ArrayList<Bean.DishItem> itemListBf;
//    private ArrayList<Bean.DishItem> itemListLun;
//    private ArrayList<Bean.DishItem> itemListDin;
//    public OneDayFragment(ArrayList<Bean.DishItem> itemListBf,ArrayList<Bean.DishItem> itemListLun,ArrayList<Bean.DishItem> itemListDin){
//        this.itemListBf=itemListBf;
//        this.itemListLun=itemListLun;
//        this.itemListDin=itemListDin;
//    }
    public OneDayFragment(int position, String[] date, MyViewPager viewPager){
        this.position=position;
        this.vp=viewPager;
        this.date=date;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.one_day_fragment, container, false);
        activity=getActivity();
        vp.setObjectForPosition(root,position);
        initView();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OneDayViewModel.class);
        cvm= new ViewModelProvider((ViewModelStoreOwner) activity).get(CookbookViewModel.class);
        //initViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        itemListBf.clear();
        itemListLun.clear();
        itemListDin.clear();
    }

    private void initView() {

        context=getContext();
        popupWidth=(int)(WindowsUtil.getAppWidth(context)* 0.6f);
        rv_breakfast=root.findViewById(R.id.rv_breakfast);
        rv_lunch=root.findViewById(R.id.rv_lunch);
        rv_dinner=root.findViewById(R.id.rv_dinner);
        bfAdapter=new CookAdapter(itemListBf);
        lunAdapter=new CookAdapter(itemListLun);
        dinAdapter=new CookAdapter(itemListDin);
        //CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(context);
        //layoutManager.setScrollEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_breakfast.setLayoutManager(layoutManager);
        rv_breakfast.setNestedScrollingEnabled(false);
        rv_breakfast.setAdapter(bfAdapter);

        LinearLayoutManager layoutManagerr = new LinearLayoutManager(context);
        layoutManagerr.setOrientation(RecyclerView.VERTICAL);
        rv_lunch.setLayoutManager(layoutManagerr);
        rv_lunch.setNestedScrollingEnabled(false);
        rv_lunch.setAdapter(lunAdapter);

        LinearLayoutManager layoutManagerrr = new LinearLayoutManager(context);
        layoutManagerrr.setOrientation(RecyclerView.VERTICAL);
        rv_dinner.setLayoutManager(layoutManagerrr);
        rv_dinner.setNestedScrollingEnabled(false);
        rv_dinner.setAdapter(dinAdapter);

        itemListDin.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
        itemListDin.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
        itemListDin.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
        itemListLun.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
        itemListLun.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
        itemListLun.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
        //Log.d("yyy", "initView: "+position);
        switch (position){
            case 0:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
                break;
            case 1:
                itemListBf.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
                break;
            case 2:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
                break;
            case 3:
                itemListBf.add(new Bean.DishItem("牛奶","饮品", R.mipmap.milk));
                itemListBf.add(new Bean.DishItem("烤土豆","主食", R.mipmap.potato));
                itemListBf.add(new Bean.DishItem("榛子","坚果", R.mipmap.hazel));
                break;
        }
        bfAdapter.update(itemListBf);
        lunAdapter.update(itemListLun);
        dinAdapter.update(itemListDin);

        bfAdapter.setOnItemClickListener((view, postion) -> {
            if(cvm.isSubmit.getValue()){
                Toasty.normal(context,"食谱已提交，不可以再变更哦！").show();
                return;
            }
            new XPopup.Builder(getContext())
                    .popupWidth(popupWidth)
                    .asCenterList("变更食谱", new String[]{"更改"+date[position]+"早餐的"+itemListBf.get(postion).getName()}, (OnSelectListener) (position, text) -> {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    })

                    .show();
        });
        lunAdapter.setOnItemClickListener((view, postion) -> {
            if(cvm.isSubmit.getValue()){
                Toasty.normal(context,"食谱已提交，不可以再变更哦！",Toasty.LENGTH_SHORT).show();
                return;
            }
            new XPopup.Builder(getContext())
                    .popupWidth(popupWidth)
                    .asCenterList("变更食谱", new String[]{"更改"+date[position]+"午餐的"+itemListLun.get(postion).getName()}, (position, text) -> {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    })
                    .show();

        });
        dinAdapter.setOnItemClickListener((view, postion) -> {
            if(cvm.isSubmit.getValue()){
                Toasty.normal(context,"食谱已提交，不可以再变更哦！").show();
                return;
            }
            new XPopup.Builder(getContext())
                    .popupWidth(popupWidth)
                    .asCenterList("变更食谱", new String[]{"更改"+date[position]+"晚餐的"+itemListDin.get(postion).getName()}, (OnSelectListener) (position, text) -> {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    })
                    .show();

        });
        bfAdapter.setOnItemLongClickListener((view, postion) -> {

        });
    }

//    private void initViewModel() {
//        mViewModel.getBFList(position).observe(getViewLifecycleOwner(), dishItems -> {
//            bfAdapter.update(dishItems);
//        });
//        mViewModel.getLUNList(position).observe(getViewLifecycleOwner(), dishItems -> {
//            lunAdapter.update(dishItems);
//        });
//        mViewModel.getDINList(position).observe(getViewLifecycleOwner(), dishItems -> {
//            dinAdapter.update(dishItems);
//        });
//    }

}