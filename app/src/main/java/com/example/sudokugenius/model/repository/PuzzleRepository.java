package com.example.sudokugenius.model.repository;

import com.example.sudokugenius.model.entity.Puzzle;
import com.example.sudokugenius.model.entity.Difficulty;

public interface PuzzleRepository {
    Puzzle generatePuzzle(Difficulty difficulty);
    boolean validateMove(Puzzle puzzle, int row, int col, int value);
    boolean isPuzzleComplete(Puzzle puzzle);
    int[][] solvePuzzle(int[][] board);
    boolean isValidBoard(int[][] board);
    int getHint(Puzzle puzzle, int row, int col);
    boolean isMoveCorrect(Puzzle puzzle, int row, int col, int value); // 新增方法
}