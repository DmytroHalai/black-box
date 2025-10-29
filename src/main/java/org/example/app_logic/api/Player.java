package org.example.app_logic.api;

public enum Player {
    /**
     * Гравець, який грає символом 'X'.
     */
    X,

    /**
     * Гравець, який грає символом 'O'.
     */
    O;

    /**
     * Повертає іншого гравця.
     * <ul>
     *     <li>Якщо поточний гравець — {@link #X}, повертає {@link #O}.</li>
     *     <li>Якщо поточний гравець — {@link #O}, повертає {@link #X}.</li>
     * </ul>
     *
     * @return інший гравець (тобто не поточний)
     */
    public Player other() {
        return this == X ? O : X;
    }
}

