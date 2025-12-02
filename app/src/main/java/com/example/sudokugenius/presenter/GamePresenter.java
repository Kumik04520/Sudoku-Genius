package com.example.sudokugenius.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sudokugenius.BasePresenter;
import com.example.sudokugenius.BaseView;
import com.example.sudokugenius.model.entity.Difficulty;
import com.example.sudokugenius.model.entity.Puzzle;
import com.example.sudokugenius.model.impl.PuzzleRepositoryImpl;
import com.example.sudokugenius.model.repository.PuzzleRepository;
import com.example.sudokugenius.view.activity.GameActivity;

public class GamePresenter implements BasePresenter {

    private BaseView view;
    private PuzzleRepository puzzleRepository;
    private Puzzle currentPuzzle;
    private long startTime;
    private int movesCount;
    private Difficulty currentDifficulty;

    public GamePresenter() {
        this.puzzleRepository = new PuzzleRepositoryImpl();
        this.movesCount = 0;
        this.currentDifficulty = Difficulty.EASY;
    }

    @Override
    public void attachView(BaseView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void start() {
        // 默认启动逻辑
    }

    public void generateNewPuzzle(Difficulty difficulty) {
        if (view != null) view.showLoading();

        this.currentDifficulty = difficulty;
        // 重置时间
        this.startTime = System.currentTimeMillis();
        this.movesCount = 0;

        currentPuzzle = puzzleRepository.generatePuzzle(difficulty);

        if (view instanceof GameActivity) {
            ((GameActivity) view).onPuzzleGenerated(difficulty);
        }
        if (view != null) view.hideLoading();
    }

    public void toggleNote(int row, int col, int number) {
        if (currentPuzzle != null && !currentPuzzle.isInitialCell(row, col)) {
            if (currentPuzzle.getValueAt(row, col) != 0) {
                return;
            }
            currentPuzzle.toggleNote(row, col, number);
            if (view instanceof GameActivity) {
                ((GameActivity) view).updateSudokuBoard();
            }
        }
    }

    public void makeMove(int row, int col, int value) {
        if (currentPuzzle == null) return;

        if (value == 0) {
            currentPuzzle.setValueAt(row, col, 0);
            if (view instanceof GameActivity) ((GameActivity) view).onCellCleared(row, col);
            return;
        }

        movesCount++;
        boolean isCorrect = puzzleRepository.isMoveCorrect(currentPuzzle, row, col, value);
        boolean isValid = puzzleRepository.validateMove(currentPuzzle, row, col, value);

        if (isCorrect) {
            currentPuzzle.setValueAt(row, col, value);
            currentPuzzle.clearNotes(row, col);
            if (view instanceof GameActivity) ((GameActivity) view).onCorrectMove(row, col, value);

            if (puzzleRepository.isPuzzleComplete(currentPuzzle)) {
                if (view instanceof GameActivity) {
                    ((GameActivity) view).onPuzzleCompleted(getElapsedTime(), movesCount);
                }
            }
        } else if (isValid) {
            currentPuzzle.setValueAt(row, col, value);
            currentPuzzle.clearNotes(row, col);
            if (view instanceof GameActivity) ((GameActivity) view).onValidButIncorrectMove(row, col, value);
        } else {
            if (view instanceof GameActivity) ((GameActivity) view).onInvalidMove(row, col, value);
        }
    }

    public void requestHint(int row, int col) {
        if (currentPuzzle != null) {
            int hint = puzzleRepository.getHint(currentPuzzle, row, col);
            if (view instanceof GameActivity) {
                ((GameActivity) view).onHintReceived(row, col, hint);
                currentPuzzle.setValueAt(row, col, hint);
                currentPuzzle.clearNotes(row, col);
            }
        }
    }

    public Puzzle getCurrentPuzzle() { return currentPuzzle; }
    public int getMovesCount() { return movesCount; }

    // 获取经过的时间（秒）
    public long getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public boolean canEditCell(int row, int col) {
        return currentPuzzle != null && !currentPuzzle.isInitialCell(row, col);
    }

    // --- 新增：保存与加载游戏状态 ---

    public void saveGameState(Context context) {
        if (currentPuzzle == null || currentPuzzle.isCompleted()) {
            // 如果没开始或已完成，清除存档
            clearSavedGame(context);
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences("SudokuState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("current_board", boardToString(currentPuzzle.getCurrentBoard()));
        editor.putString("initial_board", boardToString(currentPuzzle.getInitialBoard()));
        editor.putString("solution_board", boardToString(currentPuzzle.getSolution()));
        editor.putLong("elapsed_time_ms", System.currentTimeMillis() - startTime); // 保存毫秒差值
        editor.putInt("moves_count", movesCount);
        editor.putString("difficulty", currentDifficulty.getKey());
        editor.putBoolean("has_saved_game", true);

        editor.apply();
    }

    public boolean loadGameState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SudokuState", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("has_saved_game", false)) return false;

        try {
            String diffKey = prefs.getString("difficulty", "easy");
            Difficulty diff = Difficulty.fromKey(diffKey);
            this.currentDifficulty = diff;

            int[][] current = stringToBoard(prefs.getString("current_board", ""));
            int[][] initial = stringToBoard(prefs.getString("initial_board", ""));
            int[][] solution = stringToBoard(prefs.getString("solution_board", ""));

            currentPuzzle = new Puzzle(initial, solution, diff);
            currentPuzzle.setCurrentBoard(current);

            // 恢复时间：当前时间 - 之前玩过的时长
            long savedElapsedTime = prefs.getLong("elapsed_time_ms", 0);
            this.startTime = System.currentTimeMillis() - savedElapsedTime;
            this.movesCount = prefs.getInt("moves_count", 0);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearSavedGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SudokuState", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private String boardToString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append(board[i][j]).append(",");
            }
        }
        return sb.toString();
    }

    private int[][] stringToBoard(String str) {
        int[][] board = new int[9][9];
        String[] parts = str.split(",");
        int k = 0;
        if (parts.length < 81) return new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = Integer.parseInt(parts[k++]);
            }
        }
        return board;
    }
}