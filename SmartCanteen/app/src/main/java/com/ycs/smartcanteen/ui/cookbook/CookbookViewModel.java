package com.ycs.smartcanteen.ui.cookbook;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;

public class CookbookViewModel extends ViewModel {
    public MutableLiveData<Boolean> isEmpty;
    public MutableLiveData<Boolean> isSubmit;
    public MutableLiveData<ArrayList<Date>> getDates() {
        return dates;
    }

    public MutableLiveData<ArrayList<Date>> dates=new MutableLiveData<>();
    public CookbookViewModel() {
        isEmpty = new MutableLiveData<>();
        isEmpty.setValue(true);
        isSubmit=new MutableLiveData<>();
        isSubmit.setValue(false);
    }

    public LiveData<Boolean> checkEmpty() {
        return isEmpty;
    }

    public MutableLiveData<Boolean> getIsSubmit() {
        return isSubmit;
    }
}