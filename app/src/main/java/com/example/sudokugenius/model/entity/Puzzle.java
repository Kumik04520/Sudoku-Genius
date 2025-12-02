package com.example.sudokugenius.model.entity;

public class Puzzle {
    private String id;
    private int[][] initialBoard;
    private int[][] solution;
    private int[][] currentBoard;
    // 新增：notes[行][列][数字] -> true表示该位置标记了该数字
    // 数字下标使用 1-9，下标0留空
    private boolean[][][] notes;
    private Difficulty difficulty;
    private long createdAt;
    private boolean isCompleted;

    public Puzzle() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.initialBoard = new int[9][9];
        this.solution = new int[9][9];
        this.currentBoard = new int[9][9];
        this.notes = new boolean[9][9][10]; // 初始化笔记数组
        this.difficulty = Difficulty.EASY;
        this.createdAt = System.currentTimeMillis();
        this.isCompleted = false;
    }

    public Puzzle(int[][] initialBoard, int[][] solution, Difficulty difficulty) {
        this();
        this.initialBoard = initialBoard;
        this.solution = solution;
        this.difficulty = difficulty;
        for (int i = 0; i < 9; i++) {
            System.arraycopy(initialBoard[i], 0, this.currentBoard[i], 0, 9);
        }
    }

    // --- 新增笔记相关方法 ---

    // 切换笔记状态（有则删，无则加）
    public void toggleNote(int row, int col, int number) {
        if (isValidPosition(row, col) && number >= 1 && number <= 9) {
            notes[row][col][number] = !notes[row][col][number];
        }
    }

    // 检查是否有某个笔记
    public boolean hasNote(int row, int col, int number) {
        if (isValidPosition(row, col) && number >= 1 && number <= 9) {
            return notes[row][col][number];
        }
        return false;
    }

    // 清空某个格子的笔记（通常在填入正式数字时调用）
    public void clearNotes(int row, int col) {
        if (isValidPosition(row, col)) {
            for (int i = 1; i <= 9; i++) {
                notes[row][col][i] = false;
            }
        }
    }

    // --- 原有方法保持不变 ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int[][] getInitialBoard() { return initialBoard; }
    public void setInitialBoard(int[][] initialBoard) {
        this.initialBoard = initialBoard;
        for (int i = 0; i < 9; i++) {
            System.arraycopy(initialBoard[i], 0, this.currentBoard[i], 0, 9);
        }
    }
    public int[][] getSolution() { return solution; }
    public void setSolution(int[][] solution) { this.solution = solution; }
    public int[][] getCurrentBoard() { return currentBoard; }
    public void setCurrentBoard(int[][] currentBoard) { this.currentBoard = currentBoard; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getValueAt(int row, int col) {
        if (isValidPosition(row, col)) return currentBoard[row][col];
        return 0;
    }

    public void setValueAt(int row, int col, int value) {
        if (isValidPosition(row, col) && value >= 0 && value <= 9) {
            currentBoard[row][col] = value;
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 9 && col >= 0 && col < 9;
    }

    public boolean isInitialCell(int row, int col) {
        return isValidPosition(row, col) && initialBoard[row][col] != 0;
    }
}