package org.example.app_logic.core;

import org.example.app_logic.api.BoardView;

import java.util.Arrays;

public final class BoardState implements BoardView {
    private final Cell[] cells;

    private BoardState(Cell[] src) {
        this.cells = src.clone();
    }

    private static int idx(int x, int y) {
        return y * 3 + x;
    }

    public static BoardState fromChars(char[] nineChars) {
        if (nineChars.length != 9) throw new IllegalArgumentException("Board must be 9 cells");
        Cell[] arr = new Cell[9];
        for (int i = 0; i < 9; i++) {
            arr[i] = switch (nineChars[i]) {
                case 'X' -> Cell.X;
                case 'O' -> Cell.O;
                case ' ' -> Cell.EMPTY;
                default -> throw new IllegalArgumentException("Bad cell: " + nineChars[i]);
            };
        }
        return new BoardState(arr);
    }

    @Override
    public char at(int x, int y) {
        Cell c = cells[idx(x, y)];
        return switch (c) {
            case X -> 'X';
            case O -> 'O';
            default -> ' ';
        };
    }

    @Override
    public String toString() {
        char[] out = new char[9];
        for (int i = 0; i < 9; i++) {
            out[i] = switch (cells[i]) {
                case X -> 'X';
                case O -> 'O';
                default -> ' ';
            };
        }
        return Arrays.toString(out);
    }

    private enum Cell {EMPTY, X, O}
}