package org.example.app_logic.app;

import org.example.Engine;
import org.example.app_logic.api.GameEngine;
import org.example.app_logic.api.Move;

import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        GameEngine game = new Engine(); // зміни тут для вибору іншої реалізації
        Scanner sc = new Scanner(System.in);

        System.out.println("TicTacToe — Human vs Human. Coordinates: x=0..2 y=0..2");
        while (true) {
            game.getState().print();
            if (game.isTerminal()) {
                System.out.println(game.getWinner().map(w -> w + " wins!").orElse("Draw!"));
                break;
            }
            System.out.printf("Turn: %s. Enter x y: ", game.turn());
            try {
                int x = sc.nextInt();
                int y = sc.nextInt();
                game.playTurn(new Move(x, y, game.turn()));
            } catch (RuntimeException e) {
                System.out.println("Invalid move: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}