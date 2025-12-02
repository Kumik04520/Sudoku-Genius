package com.example.sudokugenius.model.entity;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    private final Position position;
    private int value;
    private boolean isFixed;
    private Set<Integer> notes;
    private boolean isError;
    private boolean isSelected;

    public Cell(Position position) {
        this.position = position;
        this.value = 0;
        this.isFixed = false;
        this.notes = new HashSet<>();
        this.isError = false;
        this.isSelected = false;
    }

    // Getters
    public Position getPosition() { return position; }
    public int getValue() { return value; }
    public boolean isFixed() { return isFixed; }
    public Set<Integer> getNotes() { return notes; }
    public boolean isError() { return isError; }
    public boolean isSelected() { return isSelected; }
    public boolean isEmpty() { return value == 0; }

    // Setters
    public void setValue(int value) {
        if (value >= 0 && value <= 9) {
            this.value = value;
        }
    }

    public void setFixed(boolean fixed) { isFixed = fixed; }
    public void setError(boolean error) { isError = error; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // Note operations
    public void addNote(int number) {
        if (number >= 1 && number <= 9) {
            notes.add(number);
        }
    }

    public void removeNote(int number) {
        notes.remove(number);
    }

    public void clearNotes() {
        notes.clear();
    }

    public boolean hasNote(int number) {
        return notes.contains(number);
    }

    public void toggleNote(int number) {
        if (hasNote(number)) {
            removeNote(number);
        } else {
            addNote(number);
        }
    }
}