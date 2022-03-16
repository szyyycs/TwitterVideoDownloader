package com.ycs.mvctest.BookMVC;

import java.util.ArrayList;
import java.util.List;

public class BookModel {
    Book book=new Book("",0);
    private static List<Book> list=new ArrayList<>();
    /**
     * 关于Static{}块的解释：
     * 只是在执行main之前执行的一些语句而已，并不是说里面的变量就是
     * static的，没什么特别的。
     * 临时变量只在static这个大括号中有用。
     **/
    static {
        //就是在初始化的时候执行一些语句
        list.add(new Book("一体",1));
        list.add(new Book("二体",1));
        list.add(new Book("三体",1));
        list.add(new Book("四体",1));
        list.add(new Book("五体",1));
    }
    public void 加书(String name,int image){
        list.add(new Book(name, image));

    }
    public void 删书(){
        list.remove(list.size()-1);
    }
    public List<Book> query(){
        return list;
    }
}
