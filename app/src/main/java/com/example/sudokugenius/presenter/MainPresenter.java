package com.example.sudokugenius.presenter;

import com.example.sudokugenius.BasePresenter;
import com.example.sudokugenius.BaseView;
import com.example.sudokugenius.view.activity.MainActivity;

public class MainPresenter implements BasePresenter {

    private MainActivity view;

    @Override
    public void attachView(BaseView view) {
        this.view = (MainActivity) view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void start() {
        // 初始化工作，比如加载用户数据等
        if (view != null) {
            view.showWelcomeMessage();
        }
    }

    public void onStartGameClicked() {
        if (view != null) {
            view.navigateToGame();
        }
    }

    public void onLearningCenterClicked() {
        if (view != null) {
            view.navigateToLearningCenter();
        }
    }



    public void onSettingsClicked() {
        if (view != null) {
            view.navigateToSettings();
        }
    }
}