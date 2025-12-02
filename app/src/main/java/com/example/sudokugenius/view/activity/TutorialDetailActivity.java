package com.example.sudokugenius.view.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sudokugenius.R;

public class TutorialDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CONTENT = "content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_detail);

        // 启用左上角返回箭头
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("教程详情");
        }

        TextView tvTitle = findViewById(R.id.tv_tutorial_title);
        TextView tvContent = findViewById(R.id.tv_tutorial_content);
        Button btnFinish = findViewById(R.id.btn_finish_tutorial);

        // 获取传递过来的数据
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);

        tvTitle.setText(title);
        tvContent.setText(content);

        btnFinish.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 处理返回箭头点击
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}