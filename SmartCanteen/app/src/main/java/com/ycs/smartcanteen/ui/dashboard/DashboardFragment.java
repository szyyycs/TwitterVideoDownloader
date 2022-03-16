package com.ycs.smartcanteen.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.freddy.silhouette.widget.layout.SleRelativeLayout;
import com.google.android.material.tabs.TabLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.BlurAnimator;
import com.lxj.xpopup.animator.TranslateAlphaAnimator;
import com.lxj.xpopup.animator.TranslateAnimator;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.cookbook.CookbookViewModel;
import com.ycs.smartcanteen.util.StatusBarUtil;
import com.ycs.smartcanteen.util.WindowsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class DashboardFragment extends Fragment {

    private static final String TAG ="yyy" ;
    private DashboardViewModel dashboardViewModel;
    private CookbookViewModel cookbookViewModel;
    private ImageView bfImg;
    private ImageView luImg;
    private ImageView diImg;
    private TextView enter_per;
    private TextView enter_sto;
    private TextView enter_cook;
    private TextView enter_radio;
    private Button btn_genrate;
    private EditText etNum;
    private Activity activity;
    private Context mContext;
    private String []selectPer;
    private String []selectSto;
    private String []selectCook;
    private String []selectRadio;


    private CalendarPickerView calendar;
    private int calendarPosition=0;
    protected boolean mIsVisible = false;
    private int popupWidth=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activity=getActivity();
        mContext=getContext();
        dashboardViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(DashboardViewModel.class);
        cookbookViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CookbookViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initCalendar(root);
        initView(root);

        initViewModel(dashboardViewModel);
        return root;
    }

    private void initViewModel(DashboardViewModel vm) {
        vm.getStrPeo().observe(getViewLifecycleOwner(), s -> {
            enter_per.setText(s);
            initSelect(vm);
        });
        vm.getStrCook().observe(getViewLifecycleOwner(), s -> {
            enter_cook.setText(s);
        });
        vm.getStrRadio().observe(getViewLifecycleOwner(), s -> {
            enter_radio.setText(s);
        });
        vm.getStrSto().observe(getViewLifecycleOwner(), s -> {
            enter_sto.setText(s);
        });
        vm.getSelectBf().observe(getViewLifecycleOwner(), s -> {
            setSelect(R.id.breakfast,s);
        });
        vm.getSelectDin().observe(getViewLifecycleOwner(), s -> {
            setSelect(R.id.dinner,s);
        });
        vm.getSelectLun().observe(getViewLifecycleOwner(), s -> {

            setSelect(R.id.lunch,s);
        });
//        vm.getStrNum().observe(getViewLifecycleOwner(), s -> {
//            etNum.setText(String.valueOf(s));
//        });
    }
    private void initSelect(DashboardViewModel vm){
        vm.strCook.setValue(vm.cookStrings[0]);
        vm.strSto.setValue(vm.stoStrings[0]);
        vm.strRadio.setValue(vm.radioStrings[0]);
        calendar.clearSelectedDates();
        calendar.selectDate(new Date(),true);
        calendar.selectDate(getDate(7-getNowIndex()),true);
        vm.dates.setValue((ArrayList<Date>)calendar.getSelectedDates());
        vm.selectBf.setValue(true);
        vm.selectLun.setValue(true);
        vm.selectDin.setValue(true);
        vm.strNum.setValue(100);
        etNum.setText(String.valueOf(100));
    }

    private void initView(View root) {
        popupWidth= (int)(WindowsUtil.getAppWidth(mContext)* 0.5f);
        StatusBarUtil.setStatusBarColor(activity);
        bfImg=root.findViewById(R.id.breakfast);
        luImg=root.findViewById(R.id.lunch);
        diImg=root.findViewById(R.id.dinner);
        enter_cook=root.findViewById(R.id.enter_cook);
        etNum=root.findViewById(R.id.num);
        enter_per=root.findViewById(R.id.enter_per);
        enter_sto=root.findViewById(R.id.enter_sto);
        enter_radio=root.findViewById(R.id.enter_radio);
        btn_genrate=root.findViewById(R.id.btn_generate);
        RelativeLayout layout_per=root.findViewById(R.id.layout_per);
        RelativeLayout layout_radio=root.findViewById(R.id.layout_radio);
        RelativeLayout layout_cook=root.findViewById(R.id.layout_cook);
        RelativeLayout layout_sto=root.findViewById(R.id.layout_sto);
        RelativeLayout bfRe=root.findViewById(R.id.bf_re);
        RelativeLayout lunRe=root.findViewById(R.id.lun_re);
        RelativeLayout dinRe=root.findViewById(R.id.din_re);
        ImageView turnLeft=root.findViewById(R.id.turn_left);
        ImageView turnRight=root.findViewById(R.id.turn_right);
        turnLeft.setOnClickListener((view -> {
            calendar.smoothScrollToPosition(0);
            calendarPosition=0;
        }));
        turnRight.setOnClickListener((view -> {
            calendar.smoothScrollToPosition(++calendarPosition);
        }));
        etNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int num=0;
                try{
                    num=Integer.valueOf(charSequence.toString());

                }catch (Exception e){
                    num=0;

                }
                dashboardViewModel.strNum.setValue(num);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_genrate.setOnClickListener((view -> {
            String tip=dashboardViewModel.validateCanGenerate();
            if(tip!=null){
                Toasty.normal(mContext, tip).show();
            }else{
                generateCookBook();
            }
        }));
        bfRe.setOnClickListener((v)->{
            dashboardViewModel.selectBf.setValue(!dashboardViewModel.selectBf.getValue());
        });
        lunRe.setOnClickListener((v)->{
            dashboardViewModel.selectLun.setValue(!dashboardViewModel.selectLun.getValue());
        });
        dinRe.setOnClickListener((v)->{
            dashboardViewModel.selectDin.setValue(!dashboardViewModel.selectDin.getValue());
        });
        selectPer=dashboardViewModel.peopleStrings;
        selectSto=dashboardViewModel.stoStrings;
        selectCook=dashboardViewModel.cookStrings;
        selectRadio=dashboardViewModel.radioStrings;
        layout_per.setOnClickListener(view -> {
            new XPopup.Builder(mContext)
                    .popupWidth((int)(popupWidth*1.2))
                    .popupAnimation(PopupAnimation.values()[12])
                    .asCenterList("选择人群", selectPer, (position, text) -> {
                        dashboardViewModel.strPeo.setValue(dashboardViewModel.peopleStrings[position]);
                    })
                    
                    .show();

        });
        layout_sto.setOnClickListener(view -> {
            new XPopup.Builder(mContext)
                    .popupWidth(popupWidth)
                    .popupAnimation(PopupAnimation.values()[12])
                    .asCenterList("选择灶类", selectSto, (position, text) -> {

                        dashboardViewModel.strSto.setValue(dashboardViewModel.stoStrings[position]);

                    })
                    .show();

        });
        layout_cook.setOnClickListener(view -> {
            new XPopup.Builder(mContext)
                    .popupWidth(popupWidth)
                    .popupAnimation(PopupAnimation.values()[12])
                    .asCenterList("选择餐标", selectCook, (position, text) -> {
                        dashboardViewModel.strCook.setValue(dashboardViewModel.cookStrings[position]);
                    })
                    .show();

        });
        layout_radio.setOnClickListener(view -> {
            new XPopup.Builder(mContext)
                    .popupWidth(popupWidth)
                    .popupAnimation(PopupAnimation.values()[12])
                    .asCenterList("选择系数", selectRadio, (position, text) -> {
                        dashboardViewModel.strRadio.setValue(dashboardViewModel.radioStrings[position]);
                    })

                    .show();


        });
    }

    private void generateCookBook(){
        if(cookbookViewModel.isEmpty.getValue()!=null&&cookbookViewModel.isEmpty.getValue()){
            cookbookViewModel.dates.setValue(dashboardViewModel.dates.getValue());
            cookbookViewModel.isEmpty.setValue(false);
            TabLayout tl=activity.findViewById(R.id.tabLayout);
            tl.selectTab(tl.getTabAt(1));
            Toasty.normal(mContext, "生成成功！").show();
        }else{
            if(cookbookViewModel.isSubmit.getValue()){
                Toasty.normal(mContext, "食谱已存在！").show();
            }else{
                Toasty.normal(mContext, "已经有待提交的食谱").show();
            }

        }
        

    }
    private void setSelect(int id,Boolean isCheck){
        switch (id){
            case R.id.breakfast:
                if(isCheck){
                    bfImg.setImageResource(R.mipmap.bf);
                    bfImg.setBackgroundResource(R.drawable.oval_select_shape);
                }else{
                    bfImg.setImageResource(R.mipmap.bf_unseleted);
                    bfImg.setBackgroundResource(R.drawable.oval_shape);
                }
                break;
            case R.id.lunch:
                if(isCheck){
                    luImg.setImageResource(R.mipmap.lunch_select);
                    luImg.setBackgroundResource(R.drawable.oval_select_shape);
                }else{
                    luImg.setImageResource(R.mipmap.lunch_unselected);
                    luImg.setBackgroundResource(R.drawable.oval_shape);
                }
                break;
            case R.id.dinner:
                if(isCheck){
                    diImg.setImageResource(R.mipmap.dinner_select);
                    diImg.setBackgroundResource(R.drawable.oval_select_shape);
                }else{
                    diImg.setImageResource(R.mipmap.dinner_unselected);
                    diImg.setBackgroundResource(R.drawable.oval_shape);
                }
                break;

        }

    }

    private void initCalendar(View root) {
        calendar=root.findViewById(R.id.calendar_view);
        Date endD=getDate(Calendar.YEAR,100);
        ArrayList<Integer> deativeList=new ArrayList<>();
        ArrayList<Date> highLightList=new ArrayList<>();
//        highLightList.add(getDate(Calendar.DATE,1));
//        highLightList.add(getDate(Calendar.DATE,2));
//        highLightList.add(getDate(Calendar.DATE,3));

//        deativeList.add(1);
//        deativeList.add(7);
        calendar.init(new Date(),endD,new SimpleDateFormat("yyyy-MM", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date())
                .withDeactivateDates(deativeList)
                .withHighlightedDates(highLightList);

        calendar.setOnScrollChangeListener((view, i, i1, i2, i3) -> {

        });
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("yyy", "onDateSelected: "+calendar.getSelectedDates().size());
                dashboardViewModel.dates.setValue((ArrayList<Date>)calendar.getSelectedDates());
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    /*
    * arg1: Calendar.YEAR
    *       Calendar.MONTH
    *       Calendar.DATE
    * 参数2：距离现在的时间
    * */
    Date getDate(int type,int i){
        Calendar endDate = Calendar.getInstance();
        endDate.add(type, i);
        Date endD=endDate.getTime();
        return endD;
    }
    Date getDate(int i){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, i);
        Date endD=endDate.getTime();
        return endD;
    }

    public int getNowIndex(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day==1) {
            day=7;
        } else {
            day=day-1;
        }
        return day;
    }
}