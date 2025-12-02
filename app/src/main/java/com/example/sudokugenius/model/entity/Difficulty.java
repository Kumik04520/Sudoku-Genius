package com.example.sudokugenius.model.entity;

public enum Difficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
    EXPERT("expert");

    private final String key;

    Difficulty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static Difficulty fromKey(String key) {
        for (Difficulty difficulty : values()) {
            if (difficulty.key.equals(key)) {
                return difficulty;
            }
        }
        return EASY;
    }
}