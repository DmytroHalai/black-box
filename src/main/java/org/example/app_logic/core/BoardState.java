package org.example.app_logic.core;

import org.example.app_logic.api.BoardView;

import java.util.Arrays;

public final class BoardState implements BoardView {
    /**
     * Масив із 9 клітинок, що представляє стан дошки.
     * <p>Кожна клітинка може мати значення {@code X}, {@code O} або {@code EMPTY}.</p>
     */
    private final Cell[] cells;

    /**
     * Приватний конструктор, який створює об’єкт {@code BoardState}
     * на основі копії переданого масиву клітинок.
     *
     * @param src вихідний масив клітинок {@link Cell} розміром 9
     */
    private BoardState(Cell[] src) {
        this.cells = src.clone();
    }

    /**
     * Обчислює лінійний індекс клітинки (від 0 до 8) за її координатами {@code (x, y)}.
     * <p>Використовується для відображення координат 3×3 у одновимірний масив.</p>
     *
     * <p>Формула: {@code index = y * 3 + x}</p>
     *
     * @param x координата стовпця (0–2)
     * @param y координата рядка (0–2)
     * @return індекс елемента у масиві {@link #cells}
     */
    private static int idx(int x, int y) {
        return y * 3 + x;
    }

    /**
     * Створює новий екземпляр {@code BoardState} на основі масиву символів.
     * <p>Кожен символ повинен бути одним із: {@code 'X'}, {@code 'O'} або пробіл {@code ' '}.</p>
     *
     * <p>Якщо довжина масиву не дорівнює 9 або виявлено недопустимий символ,
     * метод викидає {@link IllegalArgumentException}.</p>
     *
     * @param nineChars масив символів, який представляє стан усіх клітинок дошки
     * @return новий об’єкт {@code BoardState} з відповідним станом
     * @throws IllegalArgumentException якщо довжина масиву не дорівнює 9 або містить невідомий символ
     */
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

    /**
     * Повертає символ ('X', 'O' або ' ') із клітинки за координатами {@code (x, y)}.
     *
     * <ul>
     *     <li>{@code x} — горизонтальна координата (0–2)</li>
     *     <li>{@code y} — вертикальна координата (0–2)</li>
     * </ul>
     *
     * @param x координата стовпця
     * @param y координата рядка
     * @return символ, який представляє вміст клітинки ('X', 'O' або ' ')
     */
    @Override
    public char at(int x, int y) {
        Cell c = cells[idx(x, y)];
        return switch (c) {
            case X -> 'X';
            case O -> 'O';
            default -> ' ';
        };
    }

    /**
     * Повертає текстове представлення поточного стану дошки у вигляді масиву символів.
     * <p>Корисно для відлагодження або журналювання стану гри.</p>
     *
     * @return рядок із символами, що відповідають клітинкам дошки (наприклад: {@code [X, O,  ,  , X,  , O,  ,  ]})
     */
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

    /**
     * Внутрішній перелік, що представляє можливі стани однієї клітинки:
     * <ul>
     *     <li>{@code EMPTY} — порожня клітинка</li>
     *     <li>{@code X} — клітинка зайнята гравцем X</li>
     *     <li>{@code O} — клітинка зайнята гравцем O</li>
     * </ul>
     *
     * <p>Використовується лише всередині {@code BoardState}.</p>
     */
    private enum Cell {EMPTY, X, O}
}