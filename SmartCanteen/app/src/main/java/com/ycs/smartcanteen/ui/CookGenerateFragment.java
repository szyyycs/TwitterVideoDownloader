package com.ycs.smartcanteen.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.tencent.mmkv.MMKV;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.cookbook.CookbookFragment;
import com.ycs.smartcanteen.ui.cookbook.CookbookViewModel;
import com.ycs.smartcanteen.ui.dashboard.DashboardFragment;
import com.ycs.smartcanteen.util.WindowsUtil;

import es.dmoral.toasty.Toasty;

public class CookGenerateFragment extends Fragment {
    private String[] mTabTitles = new String[]{"生成食谱","确定食谱"};
    private Fragment[] fragments=new Fragment[]{new DashboardFragment(),new CookbookFragment()};
    private ViewPager vp;
    private TabLayout tl;
    private LinearLayout back;
    private CookbookViewModel cvm;
    private Context context;
    private TextView tip;
    private MMKV mv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_cook_generate, container, false);
        context=getContext();
        initView(root);
        return root;
    }

    private void initView(View root) {
        mv=MMKV.mmkvWithID("flag");
        back=root.findViewById(R.id.back);
        cvm=new ViewModelProvider((ViewModelStoreOwner) getActivity()).get(CookbookViewModel.class);
        cvm.checkEmpty().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean&&!cvm.isSubmit.getValue()){
                back.setVisibility(View.INVISIBLE);
            }else{
                back.setVisibility(View.VISIBLE);
            }
        });
        cvm.getIsSubmit().observe(getViewLifecycleOwner(), aBoolean -> {//提交就不显示，不提交就显示
            if(!cvm.isEmpty.getValue()&&!aBoolean){//如果不是空，没提交，显示删除
                back.setVisibility(View.VISIBLE);
            }else{
                back.setVisibility(View.INVISIBLE);
            }
        });
        back.setOnClickListener(view -> {
            new XPopup.Builder(context)
                    .popupWidth((int)(WindowsUtil.getAppWidth(context)*0.6))
                    .asConfirm("提示", "确认删除生成的食谱？", () -> {
                        cvm.isEmpty.setValue(true);
                        //   tl.selectTab(tl.getTabAt(0));
                        Toasty.normal(context,"删除食谱成功！").show();
                        mv.encode("use_time",1);
                        tip.setVisibility(View.INVISIBLE);
                    })

                    .show();
        });
        tip=root.findViewById(R.id.tip_text);
        if(mv.decodeInt("use_time",0)>0){
            tip.setVisibility(View.INVISIBLE);
        }
        vp=root.findViewById(R.id.viewPage);
        tl=root.findViewById(R.id.tabLayout);
        tl.setTabMode(TabLayout.MODE_FIXED);
        vp.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return mTabTitles.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles[position];
            }
        });
        tl.setupWithViewPager(vp);
        InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    back.setVisibility(View.INVISIBLE);
                }else{
                    if(!cvm.isEmpty.getValue()&&!cvm.isSubmit.getValue()){//如果不是空，没提交，显示删除
                        back.setVisibility(View.VISIBLE);
                    }else{
                        back.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

}