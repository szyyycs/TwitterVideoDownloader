package com.ycs.smartcanteen.ui.cookbook;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dlong.rep.dlflipviewpage.indicator.CirclePageIndicator;
import com.freddy.silhouette.widget.button.SleTextButton;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.oneday.OneDayFragment;
import com.ycs.smartcanteen.util.DateUtil;
import com.ycs.smartcanteen.util.MyViewPager;

import org.w3c.dom.Text;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class CookbookFragment extends Fragment {
    private CookbookViewModel mViewModel;
    private View root;
    private View layout;
    private View layout_empty;
    MyViewPager viewPager;
    //ViewPager viewPager;
    private CirclePageIndicator cpi;
    private TextView time;
    private Button submit;
    private TextView tip;
    private Context context;
    private Activity activity;
    String[] date;
    private int viewpageNum;
    Fragment[] fragments;
    ViewStub vs;
    public static CookbookFragment newInstance() {
        return new CookbookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context=getContext();
        activity=getActivity();

        root=inflater.inflate(R.layout.cookbook_fragment, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();

    }

//    private void initData() {
//
//
//    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CookbookViewModel.class);
        mViewModel.checkEmpty().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean){
                showEmptyView();
                destroyFragment();
            }else{
                showContentView();
                initContentView();
               }
        });
        mViewModel.getDates().observe(getViewLifecycleOwner(), dates -> {
            if(dates==null||dates.size()==0){
                return;
            }
            date=new String[dates.size()];
            for(int i=0;i<dates.size();i++){
                date[i]=getDateString(dates.get(i))+" "+DateUtil.getWeek(dates.get(i));
               // Log.d("yyy", "initViewModel: "+date[i]);
            }
            viewpageNum=date.length;
            fragments=new Fragment[date.length];
        });
        mViewModel.getIsSubmit().observe(getViewLifecycleOwner(),isSubmit->{
            if(submit==null)return;
            if(isSubmit){
                submit.setVisibility(View.GONE);
                tip.setVisibility(View.VISIBLE);
            }else{
                submit.setVisibility(View.VISIBLE);
                tip.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void destroyFragment(){
        fragments=null;
        date=null;
        viewPager=null;
        cpi=null;
        System.gc();
    }
    private void initContentView() {
        viewPager=layout.findViewById(R.id.view_pager);
        time=layout.findViewById(R.id.tv_time);
        cpi=layout.findViewById(R.id.indicator);
        submit=layout.findViewById(R.id.submit);
        tip=layout.findViewById(R.id.tip_no_change);
        time.setText(date[0]);
        submit.setOnClickListener((view -> {
            mViewModel.isSubmit.setValue(true);
            Toasty.normal(context,"提交成功！").show();
        }));
        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                for(int i=0;i<date.length;i++){
                    fragments[i]=new OneDayFragment(position,date,viewPager);
                }
                return fragments[position];
            }

            @Override
            public int getCount() {
                return viewpageNum;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                time.setText(date[position]);
                viewPager.resetHeight(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(viewpageNum);
        viewPager.resetHeight(0);
        cpi.setViewPager(viewPager);

    }

    private void showEmptyView(){
        vs=root.findViewById(R.id.layout_empty);
        if(vs!=null){
            layout_empty=vs.inflate();
        }
        if(layout!=null){
            layout.setVisibility(View.GONE);
        }
        if(layout_empty!=null){
            layout_empty.setVisibility(View.VISIBLE);
        }
    }
    private void showContentView(){
        vs=root.findViewById(R.id.layout_not_empty);
        if(vs!=null){
            layout=vs.inflate();
        }
        if(layout!=null){
            layout.setVisibility(View.VISIBLE);
        }
        if(layout_empty!=null){
            layout_empty.setVisibility(View.GONE);
        }

    }
    public static String getDateString(Date currentTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}