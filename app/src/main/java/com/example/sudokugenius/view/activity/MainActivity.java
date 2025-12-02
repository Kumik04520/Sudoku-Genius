package com.example.sudokugenius.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokugenius.R;
import com.example.sudokugenius.BaseView;
import com.example.sudokugenius.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements BaseView {

    private MainPresenter presenter;
    private Button btnStartGame, btnLearningCenter,btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializePresenter();
        initializeViews();
        setupClickListeners();
    }

    private void initializePresenter() {
        presenter = new MainPresenter();
        presenter.attachView(this);
        presenter.start();
    }

    private void initializeViews() {
        btnStartGame = findViewById(R.id.btn_start_game);
        btnLearningCenter = findViewById(R.id.btn_learning_center);
        btnSettings = findViewById(R.id.btn_settings);
    }

    private void setupClickListeners() {
        // 使用 Lambda 表达式连接 Presenter
        btnStartGame.setOnClickListener(v -> presenter.onStartGameClicked());
        btnLearningCenter.setOnClickListener(v -> presenter.onLearningCenterClicked());
        btnSettings.setOnClickListener(v -> presenter.onSettingsClicked());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    // --- BaseView 接口实现 ---

    @Override
    public void showLoading() {}

    @Override
    public void hideLoading() {}

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showWelcomeMessage() {
        // 可选：启动时的欢迎语
        // showMessage(getString(R.string.welcome_message));
    }

    // --- 导航方法 (由 Presenter 调用) ---

    public void navigateToGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void navigateToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void navigateToLearningCenter() {
        // 之前是 Toast，现在改成跳转
        Intent intent = new Intent(this, LearningActivity.class);
        startActivity(intent);
    }


}