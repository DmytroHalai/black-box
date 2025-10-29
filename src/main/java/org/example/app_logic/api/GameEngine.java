package org.example.app_logic.api;

import java.util.Optional;

public interface GameEngine {
    void playTurn(Move move);

    void reset();

    BoardView getState();

    Optional<Player> getWinner();

    boolean isTerminal();

    void validateMove(Move move);

    Player turn();

    void initBoard();

    boolean isBoardFull();

    void setLines();

    boolean hasWin();

    boolean threeInRow(int i, int j, int k);

    Cell[] getBoard();
    enum Cell {
        EMPTY, X, O
    }
}