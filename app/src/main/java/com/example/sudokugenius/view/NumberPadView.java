package com.example.sudokugenius.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sudokugenius.R;

public class NumberPadView extends LinearLayout {

    private OnNumberSelectedListener numberSelectedListener;

    public NumberPadView(Context context) {
        super(context);
        init(context);
    }

    public NumberPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_number_pad, this, true);
        setupNumberButtons();
    }

    private void setupNumberButtons() {
        for (int i = 1; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("btn_num_" + i, "id", getContext().getPackageName());
            Button button = findViewById(buttonId);
            final int number = i;

            if (button != null) {
                button.setOnClickListener(v -> {
                    if (numberSelectedListener != null) {
                        numberSelectedListener.onNumberSelected(number);
                    }
                });
            }
        }

        // 清除按钮
        Button btnClear = findViewById(R.id.btn_clear);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> {
                if (numberSelectedListener != null) {
                    numberSelectedListener.onNumberSelected(0);
                }
            });
        }

        // 铅笔标记按钮
        Button btnPencil = findViewById(R.id.btn_pencil);
        if (btnPencil != null) {
            btnPencil.setOnClickListener(v -> {
                if (numberSelectedListener != null) {
                    numberSelectedListener.onPencilModeToggled();
                }
            });
        }
    }

    public void setOnNumberSelectedListener(OnNumberSelectedListener listener) {
        this.numberSelectedListener = listener;
    }

    public interface OnNumberSelectedListener {
        void onNumberSelected(int number);
        void onPencilModeToggled();
    }
}