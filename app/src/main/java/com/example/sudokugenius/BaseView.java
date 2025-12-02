package com.example.sudokugenius;

public interface BaseView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showMessage(String message);
}