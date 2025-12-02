package com.example.sudokugenius.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.sudokugenius.model.entity.Puzzle;

public class SudokuBoardView extends View {
    private static final int BOARD_SIZE = 9;
    private float cellSize;

    private Paint gridPaint;
    private Paint thickGridPaint;
    private Paint numberPaint;
    private Paint fixedNumberPaint;
    private Paint notePaint; // 新增：笔记画笔
    private Paint selectedCellPaint;
    private Paint errorCellPaint; // 可选：错误高亮

    private Puzzle currentPuzzle;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private OnCellSelectedListener cellSelectedListener;

    public SudokuBoardView(Context context) {
        super(context);
        init();
    }

    public SudokuBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(2);

        thickGridPaint = new Paint();
        thickGridPaint.setColor(Color.BLACK);
        thickGridPaint.setStrokeWidth(5);

        selectedCellPaint = new Paint();
        selectedCellPaint.setColor(Color.parseColor("#BBDEFB")); // 浅蓝色选中效果
        selectedCellPaint.setStyle(Paint.Style.FILL);

        // 填入的数字（蓝色）
        numberPaint = new Paint();
        numberPaint.setColor(Color.BLUE);
        numberPaint.setTextSize(60);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setAntiAlias(true);

        // 初始固定的数字（黑色）
        fixedNumberPaint = new Paint();
        fixedNumberPaint.setColor(Color.BLACK);
        fixedNumberPaint.setTextSize(60);
        fixedNumberPaint.setTextAlign(Paint.Align.CENTER);
        fixedNumberPaint.setAntiAlias(true);

        // 新增：笔记数字（灰色，小号字体）
        notePaint = new Paint();
        notePaint.setColor(Color.DKGRAY);
        notePaint.setTextSize(30); // 字体小一点
        notePaint.setTextAlign(Paint.Align.CENTER);
        notePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int size;

        // 如果没有给高度限制（比如在ScrollView里），就以宽度为准
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            size = width;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            size = height;
        } else {
            // 既有宽又有高（通常情况），取较小值，确保完全显示在屏幕内
            // 减去一些内边距缓冲，防止贴边太紧
            size = Math.min(width, height);
        }

        // 强制设为正方形
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        cellSize = width / (float) BOARD_SIZE;

        // 1. 绘制选中背景
        drawSelectedCell(canvas);

        // 2. 绘制网格线
        drawGrid(canvas);

        // 3. 绘制数字和笔记
        drawNumbersAndNotes(canvas);
    }

    private void drawSelectedCell(Canvas canvas) {
        if (selectedRow >= 0 && selectedCol >= 0) {
            canvas.drawRect(
                    selectedCol * cellSize,
                    selectedRow * cellSize,
                    (selectedCol + 1) * cellSize,
                    (selectedRow + 1) * cellSize,
                    selectedCellPaint
            );
        }
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i <= BOARD_SIZE; i++) {
            float pos = i * cellSize;
            Paint paint = (i % 3 == 0) ? thickGridPaint : gridPaint;
            canvas.drawLine(pos, 0, pos, getHeight(), paint);
            canvas.drawLine(0, pos, getWidth(), pos, paint);
        }
    }

    private void drawNumbersAndNotes(Canvas canvas) {
        if (currentPuzzle == null) return;

        // 根据格子大小动态调整字体
        numberPaint.setTextSize(cellSize * 0.7f);
        fixedNumberPaint.setTextSize(cellSize * 0.7f);
        notePaint.setTextSize(cellSize * 0.25f); // 笔记字体大小为格子的1/4

        Paint.FontMetrics fm = numberPaint.getFontMetrics();
        float numberOffsetY = -(fm.ascent + fm.descent) / 2;

        Paint.FontMetrics fmNote = notePaint.getFontMetrics();
        float noteOffsetY = -(fmNote.ascent + fmNote.descent) / 2;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int val = currentPuzzle.getValueAt(r, c);

                float centerX = c * cellSize + cellSize / 2;
                float centerY = r * cellSize + cellSize / 2 + numberOffsetY;

                if (val != 0) {
                    // 绘制大数字
                    if (currentPuzzle.isInitialCell(r, c)) {
                        canvas.drawText(String.valueOf(val), centerX, centerY, fixedNumberPaint);
                    } else {
                        canvas.drawText(String.valueOf(val), centerX, centerY, numberPaint);
                    }
                } else {
                    // 如果格子为空，绘制笔记
                    drawNotesInCell(canvas, r, c, noteOffsetY);
                }
            }
        }
    }

    private void drawNotesInCell(Canvas canvas, int row, int col, float textOffsetY) {
        // 笔记在格子内呈 3x3 排列
        // 1 2 3
        // 4 5 6
        // 7 8 9
        float subCellSize = cellSize / 3f;

        for (int num = 1; num <= 9; num++) {
            if (currentPuzzle.hasNote(row, col, num)) {
                int noteRow = (num - 1) / 3; // 0, 1, 2
                int noteCol = (num - 1) % 3; // 0, 1, 2

                float noteX = col * cellSize + noteCol * subCellSize + subCellSize / 2;
                float noteY = row * cellSize + noteRow * subCellSize + subCellSize / 2 + textOffsetY;

                canvas.drawText(String.valueOf(num), noteX, noteY, notePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                selectedRow = row;
                selectedCol = col;
                invalidate();
                if (cellSelectedListener != null) {
                    cellSelectedListener.onCellSelected(row, col);
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
        invalidate();
    }

    public void setSelectedCell(int row, int col) {
        this.selectedRow = row;
        this.selectedCol = col;
        invalidate();
    }

    public void setOnCellSelectedListener(OnCellSelectedListener listener) {
        this.cellSelectedListener = listener;
    }

    public int getSelectedRow() { return selectedRow; }
    public int getSelectedCol() { return selectedCol; }

    public interface OnCellSelectedListener {
        void onCellSelected(int row, int col);
    }
}