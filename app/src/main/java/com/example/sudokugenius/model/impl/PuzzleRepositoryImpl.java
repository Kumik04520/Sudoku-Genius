// PuzzleRepositoryImpl.java
package com.example.sudokugenius.model.impl;

import com.example.sudokugenius.model.entity.Puzzle;
import com.example.sudokugenius.model.entity.Difficulty;
import com.example.sudokugenius.model.repository.PuzzleRepository;

import java.util.Random;

public class PuzzleRepositoryImpl implements PuzzleRepository {
    private Random random = new Random();

    @Override
    public Puzzle generatePuzzle(Difficulty difficulty) {
        // 生成完整的数独解决方案
        int[][] solution = generateSolution();
        // 根据难度移除数字
        int[][] puzzle = removeNumbers(solution, getNumbersToRemove(difficulty));

        return new Puzzle(puzzle, solution, difficulty);
    }

    private int[][] generateSolution() {
        int[][] board = new int[9][9];
        solveSudoku(board);
        return board;
    }

    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValidPlacement(board, row, col, num)) {
                            board[row][col] = num;

                            if (solveSudoku(board)) {
                                return true;
                            }

                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPlacement(int[][] board, int row, int col, int num) {
        // 检查行
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) return false;
        }

        // 检查列
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == num) return false;
        }

        // 检查3x3宫格
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[startRow + i][startCol + j] == num) return false;
            }
        }

        return true;
    }

    private int[][] removeNumbers(int[][] solution, int numbersToRemove) {
        int[][] puzzle = new int[9][9];
        // 复制解决方案
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
        }

        // 随机移除数字
        int removed = 0;
        while (removed < numbersToRemove) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0;
                removed++;
            }
        }

        return puzzle;
    }

    private int getNumbersToRemove(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return 30;    // 保留51个数字
            case MEDIUM: return 40;  // 保留41个数字
            case HARD: return 50;    // 保留31个数字
            case EXPERT: return 55;  // 保留26个数字
            default: return 30;
        }
    }

    @Override
    public boolean validateMove(Puzzle puzzle, int row, int col, int value) {
        if (!puzzle.isValidPosition(row, col)) {
            return false;
        }

        // 如果是初始单元格，不允许修改
        if (puzzle.isInitialCell(row, col)) {
            return false;
        }

        // 如果值是0（清除），总是允许
        if (value == 0) {
            return true;
        }

        // 获取当前棋盘状态（不包括当前要验证的移动）
        int[][] currentBoard = copyBoard(puzzle.getCurrentBoard());

        // 临时移除当前位置的值，以便验证新值
        currentBoard[row][col] = 0;

        // 检查移动是否违反数独规则
        return isValidPlacement(currentBoard, row, col, value);
    }

    @Override
    public boolean isPuzzleComplete(Puzzle puzzle) {
        int[][] currentBoard = puzzle.getCurrentBoard();
        int[][] solution = puzzle.getSolution();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // 检查所有单元格是否已填充且正确
                if (currentBoard[i][j] == 0 || currentBoard[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int[][] solvePuzzle(int[][] board) {
        int[][] solution = copyBoard(board);
        solveSudoku(solution);
        return solution;
    }

    @Override
    public boolean isValidBoard(int[][] board) {
        // 检查当前棋盘是否有效（没有冲突）
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    int temp = board[i][j];
                    board[i][j] = 0;
                    if (!isValidPlacement(board, i, j, temp)) {
                        board[i][j] = temp;
                        return false;
                    }
                    board[i][j] = temp;
                }
            }
        }
        return true;
    }

    @Override
    public int getHint(Puzzle puzzle, int row, int col) {
        if (puzzle.isValidPosition(row, col)) {
            return puzzle.getSolution()[row][col];
        }
        return 0;
    }

    @Override
    public boolean isMoveCorrect(Puzzle puzzle, int row, int col, int value) {
        if (!puzzle.isValidPosition(row, col)) {
            return false;
        }
        return puzzle.getSolution()[row][col] == value;
    }

    // 辅助方法：复制棋盘
    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 9);
        }
        return copy;
    }
}