package com.ycs.mvctest.BookMVC;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ycs.mvctest.BR;

public class Boook extends BaseObservable {
    public String name;
    public String price;
    public Boook(){

    }
    public Boook(String name,String price){
        this.name=name;
        this.price=price;
    }
    @Bindable
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
    @Bindable
    public String getPrice(){
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

}
