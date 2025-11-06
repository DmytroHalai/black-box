package org.example;

import org.example.app_logic.api.*;
import org.example.app_logic.core.BoardState;

import java.util.Arrays;
import java.util.Optional;

import static org.example.app_logic.api.View.idx;

public final class Engine extends GameEngine {
    public Engine() {
        initBoard();
        reset();
        setLines();
    }

    @Override
    public void initBoard() {
        board = new Cell[9];
    }

    @Override
    public void playTurn(Move move) {
        validateMove(move);
        int i = idx(move.x(), move.y());
        board[i] = (turn == Player.X) ? Cell.X : Cell.O;

        if (hasWin()) {
            result = (turn == Player.X) ? Result.X_WINS : Result.O_WINS;
        } else if (isBoardFull()) {
            result = Result.DRAW;
        } else {
            turn = turn.other();
        }
    }

    @Override
    public void reset() {
        Arrays.fill(board, Cell.EMPTY);
        turn = Player.X;
        result = Result.ONGOING;
    }

    @Override
    public BoardView getState() {
        char[] nine = new char[board.length];
        for (int i = 0; i < board.length; i++) {
            nine[i] = switch (board[i]) {
                case X -> 'X';
                case O -> 'O';
                default -> ' ';
            };
        }
        return BoardState.fromChars(nine);
    }

    @Override
    public Optional<Player> getWinner() {
        return switch (result) {
            case X_WINS -> Optional.of(Player.X);
            case O_WINS -> Optional.of(Player.O);
            default -> Optional.empty();
        };
    }

    @Override
    public boolean isTerminal() {
        return result != Result.ONGOING;
    }

    @Override
    public void validateMove(Move move) {
        if (isTerminal()) throw new IllegalMoveException("Game is over");
        if (move.player() != turn) throw new IllegalMoveException("Wrong turn: " + move.player());
        if (move.x() < 0 || move.x() > 2 || move.y() < 0 || move.y() > 2)
            throw new IllegalMoveException("Out of board");
        if (board[idx(move.x(), move.y())] != Cell.EMPTY) throw new IllegalMoveException("Cell occupied");
    }

    @Override
    public Player turn() {
        return turn;
    }

    @Override
    public boolean isBoardFull() {
        for (Cell c : board) if (c == Cell.EMPTY) return false;
        return true;
    }

    @Override
    public boolean hasWin() {
        for (int[] line : lines) {
            if (threeInRow(line[0], line[1], line[2])) return true;
        }
        return false;
    }

    @Override
    public void setLines() {
        lines = new int[][]{
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
    }

    @Override
    public boolean threeInRow(int i, int j, int k) {
        boolean isNotEmpty = board[i] != Cell.EMPTY;
        boolean equalIJ = board[i] == board[j];
        boolean equalJK = board[j] == board[k];
        return isNotEmpty && equalIJ && equalJK;
    }

}