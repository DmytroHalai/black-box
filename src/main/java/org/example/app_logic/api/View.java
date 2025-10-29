package org.example.app_logic.api;

public class View implements BoardView {
    private final GameEngine.Cell[] snap;

    public View(GameEngine.Cell[] src) {
        this.snap = src.clone();
    }

    @Override
    public char at(int x, int y) {
        GameEngine.Cell c = snap[idx(x, y)];
        return switch (c) {
            case X -> 'X';
            case O -> 'O';
            default -> ' ';
        };
    }

    public static int idx(int x, int y) {
        return y * 3 + x;
    }

}