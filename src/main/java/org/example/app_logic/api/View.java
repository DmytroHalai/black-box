package org.example.app_logic.api;

public class View implements BoardView {
    /**
     * Копія поточного стану ігрового поля.
     * Кожен елемент відповідає одній клітинці дошки 3×3.
     * <p>Використовується для збереження стану у момент створення об’єкта {@code View}.</p>
     */
    private final GameEngine.Cell[] snap;

    /**
     * Створює новий об’єкт {@code View} на основі переданого масиву клітинок.
     * <p>Виконується копіювання масиву, щоб запобігти зміні стану
     * при подальших ходах у грі.</p>
     *
     * @param src вихідний масив {@link GameEngine.Cell} із поточного стану гри
     */
    public View(GameEngine.Cell[] src) {
        this.snap = src.clone();
    }

    /**
     * Повертає символ ('X', 'O' або ' ') із клітинки за заданими координатами {@code (x, y)}.
     *
     * <ul>
     *     <li>{@code x} — горизонтальна координата (0–2)</li>
     *     <li>{@code y} — вертикальна координата (0–2)</li>
     * </ul>
     *
     * <p>Якщо клітинка порожня ({@link GameEngine.Cell#EMPTY}), повертається пробіл ' '.</p>
     *
     * @param x координата стовпця
     * @param y координата рядка
     * @return символ, що представляє стан клітинки ('X', 'O' або ' ')
     */
    @Override
    public char at(int x, int y) {
        GameEngine.Cell c = snap[idx(x, y)];
        return switch (c) {
            case X -> 'X';
            case O -> 'O';
            default -> ' ';
        };
    }

    /**
     * Обчислює лінійний індекс клітинки у масиві розміром 9 (3×3)
     * на основі координат {@code (x, y)}.
     *
     * <p>Формула: {@code index = y * 3 + x}</p>
     *
     * <p>Наприклад:
     * <ul>
     *     <li>{@code idx(0, 0)} → 0 (верхній лівий кут)</li>
     *     <li>{@code idx(2, 1)} → 5 (середній ряд, правий стовпець)</li>
     *     <li>{@code idx(1, 2)} → 7 (нижній ряд, середина)</li>
     * </ul></p>
     *
     * @param x координата стовпця (0–2)
     * @param y координата рядка (0–2)
     * @return одновимірний індекс елемента у масиві {@link GameEngine.Cell}
     */
    public static int idx(int x, int y) {
        return y * 3 + x;
    }

}