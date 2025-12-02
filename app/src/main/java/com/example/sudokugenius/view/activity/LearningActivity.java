package com.example.sudokugenius.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokugenius.R;

public class LearningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.learning_center);
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.layout_topic_1).setOnClickListener(v -> openTutorial(
                getString(R.string.topic_1_title),
                getString(R.string.topic_1_content)
        ));

        findViewById(R.id.layout_topic_2).setOnClickListener(v -> openTutorial(
                getString(R.string.topic_2_title),
                getString(R.string.topic_2_content)
        ));

        findViewById(R.id.layout_topic_3).setOnClickListener(v -> openTutorial(
                getString(R.string.topic_3_title),
                getString(R.string.topic_3_content)
        ));
    }

    private void openTutorial(String title, String content) {
        Intent intent = new Intent(this, TutorialDetailActivity.class);
        intent.putExtra(TutorialDetailActivity.EXTRA_TITLE, title);
        intent.putExtra(TutorialDetailActivity.EXTRA_CONTENT, content);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}