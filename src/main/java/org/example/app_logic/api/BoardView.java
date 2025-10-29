package org.example.app_logic.api;

public interface BoardView {
    char at(int x, int y);

    default void print() {
        System.out.println("+---+---+---+");
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) System.out.print("| " + at(x, y) + " ");
            System.out.println("|\n+---+---+---+");
        }
    }
}