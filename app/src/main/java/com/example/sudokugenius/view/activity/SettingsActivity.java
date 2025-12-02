package com.example.sudokugenius.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokugenius.R;
import com.example.sudokugenius.model.entity.Difficulty;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SudokuSettings";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_HINTS = "hints";
    private static final String KEY_AUTO_CHECK = "auto_check"; // 确保定义了这个 Key

    private RadioGroup rgDifficulty;
    private CheckBox cbHints;
    private CheckBox cbAutoCheck; // 声明变量
    private Button btnSave, btnReset,btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        initializeViews();
        loadSettings(); // 关键步骤：读取设置
        setupClickListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        rgDifficulty = findViewById(R.id.rg_difficulty);
        cbHints = findViewById(R.id.cb_hints);

        // 【修正 1】确保这里绑定了 XML 中的 ID
        // 请检查 activity_settings.xml 里有没有 id 为 cb_auto_check_errors 的 CheckBox
        cbAutoCheck = findViewById(R.id.cb_auto_check_errors);

        btnSave = findViewById(R.id.btn_save);
        btnReset = findViewById(R.id.btn_reset);
        btnAbout = findViewById(R.id.btn_about);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 1. 加载难度
        String diffKey = prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.getKey());
        Difficulty currentDiff = Difficulty.fromKey(diffKey);
        switch (currentDiff) {
            case EXPERT: rgDifficulty.check(R.id.rb_expert); break;
            case HARD: rgDifficulty.check(R.id.rb_hard); break;
            case MEDIUM: rgDifficulty.check(R.id.rb_medium); break;
            case EASY: default: rgDifficulty.check(R.id.rb_easy); break;
        }

        // 2. 加载提示开关
        if (cbHints != null) {
            cbHints.setChecked(prefs.getBoolean(KEY_HINTS, true));
        }

        // 【修正 2】关键修复：加载自动检查错误的状态
        // 默认值设为 true 或 false 看你的需求
        if (cbAutoCheck != null) {
            boolean isAutoCheckEnabled = prefs.getBoolean(KEY_AUTO_CHECK, true);
            cbAutoCheck.setChecked(isAutoCheckEnabled);
        }
    }

    private void setupClickListeners() {
        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveSettings());

        // 重置按钮点击事件
        btnReset.setOnClickListener(v -> {
            // 重置为默认设置
            rgDifficulty.check(R.id.rb_easy);
            if (cbHints != null) cbHints.setChecked(true);
            if (cbAutoCheck != null) cbAutoCheck.setChecked(true); // 默认开启

            Toast.makeText(this, R.string.settings_reset, Toast.LENGTH_SHORT).show();
        });

        btnAbout.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        // 保存难度
        int selectedId = rgDifficulty.getCheckedRadioButtonId();
        Difficulty difficulty;
        if (selectedId == R.id.rb_expert) difficulty = Difficulty.EXPERT;
        else if (selectedId == R.id.rb_hard) difficulty = Difficulty.HARD;
        else if (selectedId == R.id.rb_medium) difficulty = Difficulty.MEDIUM;
        else difficulty = Difficulty.EASY;
        editor.putString(KEY_DIFFICULTY, difficulty.getKey());

        // 保存提示开关
        if (cbHints != null) {
            editor.putBoolean(KEY_HINTS, cbHints.isChecked());
        }

        // 【修正 3】保存自动检查错误的状态
        if (cbAutoCheck != null) {
            editor.putBoolean(KEY_AUTO_CHECK, cbAutoCheck.isChecked());
        }

        editor.apply();
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    // --- 静态工具方法 (供 GameActivity 使用) ---

    public static Difficulty getDifficulty(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String key = prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.getKey());
        return Difficulty.fromKey(key);
    }

    // 【修正 4】提供获取 Auto Check 状态的方法
    public static boolean isAutoCheckEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // 这里的默认值 true 必须和 loadSettings 里的一致
        return prefs.getBoolean(KEY_AUTO_CHECK, true);
    }
}