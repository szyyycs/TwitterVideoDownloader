package com.ycs.smartcanteen.ui.dashboard;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ycs.smartcanteen.R;

import java.util.ArrayList;
import java.util.Date;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Boolean> cookbookIsExist;
    public String[] peopleStrings;
    public String[] stoStrings;
    public String[] cookStrings;
    public String[] radioStrings;
    MutableLiveData<String> strPeo;
    MutableLiveData<String> strSto;
    MutableLiveData<String> strCook;
    MutableLiveData<String> strRadio;
    MutableLiveData<Boolean> selectBf;
    MutableLiveData<Boolean> selectLun;
    MutableLiveData<Boolean> selectDin;
    String tip="";


    MutableLiveData<ArrayList<Date>> dates=new MutableLiveData<>();
    MutableLiveData<Integer> strNum;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        cookbookIsExist=new MutableLiveData<>();
        strSto=new MutableLiveData<>();
        strCook=new MutableLiveData<>();
        strRadio=new MutableLiveData<>();
        strPeo=new MutableLiveData<>();
        selectBf=new MutableLiveData<>();
        selectLun=new MutableLiveData<>();
        selectDin=new MutableLiveData<>();
        strNum=new MutableLiveData<>();
        selectBf.setValue(false);
        selectLun.setValue(false);
        selectDin.setValue(false);


        cookbookIsExist.setValue(true);
        stoStrings=application.getResources().getStringArray(R.array.stoves_type);
        cookStrings=new String[]{"高","中","低"};
        radioStrings=new String[]{"高","中","低"};
        peopleStrings=application.getResources().getStringArray(R.array.person_type);

    }

    public String validateCanGenerate(){
        if(strPeo.getValue()==null||strPeo.getValue().isEmpty()){
            return "请选择人群";
        }
        if(strSto.getValue()==null||strSto.getValue().isEmpty()){
            return "请选择灶类";
        }
        if(strCook.getValue()==null||strCook.getValue().isEmpty()) {
            return "请选择餐标";
        }
        if(strRadio.getValue()==null||strRadio.getValue().isEmpty()){
            return "请选择系数";
        }

        if(strNum.getValue()==null||strNum.getValue()==0) return "请填写就餐人数";
        if(selectLun.getValue()==false&&selectBf.getValue()==false&&selectDin.getValue()==false) return "请选择餐次";
        if(dates.getValue()==null||dates.getValue().isEmpty())return "请选择食谱要生成的日期";
        return null;
    }
    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<Boolean> isExist(){return cookbookIsExist;}

    public MutableLiveData<Integer> getStrNum() {
        return strNum;
    }
    public MutableLiveData<String> getStrPeo() {
        return strPeo;
    }
    public MutableLiveData<ArrayList<Date>> getDates() {
        return dates;
    }
    public MutableLiveData<String> getStrSto() {
        return strSto;
    }

    public MutableLiveData<String> getStrCook() {
        return strCook;
    }

    public MutableLiveData<String> getStrRadio() {
        return strRadio;
    }
    public MutableLiveData<Boolean> getSelectBf() {
        return selectBf;
    }

    public MutableLiveData<Boolean> getSelectLun() {
        return selectLun;
    }

    public MutableLiveData<Boolean> getSelectDin() {
        return selectDin;
    }
}