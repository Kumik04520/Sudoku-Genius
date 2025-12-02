package com.example.sudokugenius;

public interface BasePresenter {
    void attachView(BaseView view);
    void detachView();
    void start();
}