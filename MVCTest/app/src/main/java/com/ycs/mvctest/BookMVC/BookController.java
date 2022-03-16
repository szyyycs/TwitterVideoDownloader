package com.ycs.mvctest.BookMVC;

import java.util.List;

public class BookController {
    private BookModel model;
    public BookController(){
        model=new BookModel();

    }
    public void add(onAddBookListener listener){
        model.加书("java从入门到入土",21);
        if(listener!=null){
            listener.onComplete();
        }
    }
    public void delete(onDeleteBookListener listener){
        if(model.query().isEmpty()){
            return;
        }else{
            model.删书();
        }
        if(listener!=null){
            listener.onComplete();
        }
    }
    public List<Book> query() {
        return model.query();
    }
    public interface onAddBookListener{
        void onComplete();
    }
    public interface  onDeleteBookListener{
        void onComplete();
    }
}
