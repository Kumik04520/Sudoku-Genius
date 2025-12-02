package com.example.sudokugenius.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudokugenius.R;
import com.example.sudokugenius.BaseView;
import com.example.sudokugenius.model.entity.Difficulty;
import com.example.sudokugenius.presenter.GamePresenter;
import com.example.sudokugenius.view.SudokuBoardView;
import com.example.sudokugenius.view.NumberPadView;

public class GameActivity extends AppCompatActivity implements BaseView,
        SudokuBoardView.OnCellSelectedListener,
        NumberPadView.OnNumberSelectedListener {

    private GamePresenter presenter;
    private Button btnBack, btnNewGame, btnHint;
    private TextView tvTimer, tvMoves;
    private SudokuBoardView sudokuBoardView;
    private NumberPadView numberPadView;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean isPencilMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        initializePresenter();
        setupClickListeners();

        // 核心修改：尝试加载存档，如果没有存档才开始新游戏
        if (presenter.loadGameState(this)) {
            // 恢复 UI
            if (presenter.getCurrentPuzzle() != null) {
                onPuzzleGenerated(presenter.getCurrentPuzzle().getDifficulty());
                updateMoves(presenter.getMovesCount());
                showMessage(R.string.game_resumed); // 需要在 strings.xml 添加
            }
        } else {
            startNewGameFromSettings();
        }

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 核心修改：退出或后台时保存
        if (presenter != null) {
            presenter.saveGameState(this);
        }
        stopTimer();
    }

    // 如果回到前台，重启计时器（不需要重新加载数据，因为Activity还在内存中）
    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    private void initializePresenter() {
        presenter = new GamePresenter();
        presenter.attachView(this);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        btnNewGame = findViewById(R.id.btn_new_game);
        btnHint = findViewById(R.id.btn_hint);
        tvTimer = findViewById(R.id.tv_timer);
        tvMoves = findViewById(R.id.tv_moves);
        sudokuBoardView = findViewById(R.id.sudoku_board);
        numberPadView = findViewById(R.id.number_pad);

        sudokuBoardView.setOnCellSelectedListener(this);
        numberPadView.setOnNumberSelectedListener(this);
    }

    private void startNewGameFromSettings() {
        Difficulty diff = SettingsActivity.getDifficulty(this);
        presenter.generateNewPuzzle(diff);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNewGame.setOnClickListener(v -> {
            // 点击新游戏，清除旧存档并重新开始
            presenter.clearSavedGame(this);
            startNewGameFromSettings();
            resetTimer();
        });

        btnHint.setOnClickListener(v -> {
            int r = sudokuBoardView.getSelectedRow();
            int c = sudokuBoardView.getSelectedCol();
            if (r >= 0 && c >= 0) {
                presenter.requestHint(r, c);
            } else {
                showMessage(R.string.select_cell_first);
            }
        });
    }

    private void startTimer() {
        stopTimer(); // 防止重复启动
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (presenter != null) {
                    updateTimer(presenter.getElapsedTime());
                    // 仅当步数变化时更新，或直接每秒更新也可以
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void resetTimer() {
        tvTimer.setText(getString(R.string.time_format, 0, 0));
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    // --- 交互回调 ---

    @Override
    public void onCellSelected(int row, int col) {
        sudokuBoardView.setSelectedCell(row, col);
    }

    @Override
    public void onNumberSelected(int number) {
        int r = sudokuBoardView.getSelectedRow();
        int c = sudokuBoardView.getSelectedCol();

        if (r < 0 || c < 0) {
            showMessage(R.string.select_cell_first);
            return;
        }

        if (number == 0) {
            presenter.makeMove(r, c, 0);
            return;
        }

        if (isPencilMode) {
            presenter.toggleNote(r, c, number);
        } else {
            if (!presenter.canEditCell(r, c)) {
                showMessage(R.string.cannot_edit_initial);
                return;
            }
            presenter.makeMove(r, c, number);
            updateMoves(presenter.getMovesCount()); // 每次移动后更新步数显示
        }
    }

    @Override
    public void onPencilModeToggled() {
        isPencilMode = !isPencilMode;
        showMessage(isPencilMode ? R.string.pencil_mode_on : R.string.pencil_mode_off);
    }

    // --- Presenter 更新 View ---

    public void onPuzzleGenerated(Difficulty difficulty) {
        sudokuBoardView.setPuzzle(presenter.getCurrentPuzzle());
        sudokuBoardView.invalidate();
    }

    public void updateSudokuBoard() {
        sudokuBoardView.invalidate();
    }

    public void onCorrectMove(int row, int col, int value) {
        updateSudokuBoard();
    }

    public void onValidButIncorrectMove(int row, int col, int value) {
        if (SettingsActivity.isAutoCheckEnabled(this)) {
            showMessage(R.string.number_incorrect);
        }
        updateSudokuBoard();
    }

    public void onInvalidMove(int row, int col, int value) {
        showError(getString(R.string.invalid_move));
    }

    public void onCellCleared(int row, int col) {
        updateSudokuBoard();
    }

    public void onHintReceived(int row, int col, int hint) {
        showMessage(getString(R.string.hint) + ": " + hint);
        updateSudokuBoard();
    }

    public void onPuzzleCompleted(long duration, int moves) {
        showMessage(getString(R.string.game_won, duration));
        presenter.clearSavedGame(this); // 完成后清除存档
        stopTimer();
    }

    // --- BaseView ---

    @Override
    public void showLoading() { }

    @Override
    public void hideLoading() { }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showMessage(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void updateTimer(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        tvTimer.setText(getString(R.string.time_format, minutes, secs));
    }

    public void updateMoves(int moves) {
        tvMoves.setText(getString(R.string.moves_format, moves));
    }
}