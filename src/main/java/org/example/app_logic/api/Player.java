package org.example.app_logic.api;

public enum Player {
    X, O;

    public Player other() {
        return this == X ? O : X;
    }
}

