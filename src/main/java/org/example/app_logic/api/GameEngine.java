package org.example.app_logic.api;

import java.util.Optional;

public abstract class GameEngine {
    protected int[][] lines;

    protected Cell[] board;

    protected Player turn;

    protected Result result;

    /**
     * Виконує хід на дошці, змінює стан гри відповідно до переданого {@link Move}.
     * Перевіряє, чи не завершена гра, чи коректний гравець здійснює хід,
     * і чи не зайнята вибрана клітинка.
     *
     * @param move об’єкт {@link Move}, який містить координати (x, y) та гравця, що здійснює хід
     * @throws IllegalMoveException якщо хід некоректний (гра завершена, не його черга або клітинка зайнята)
     */
    public void playTurn(Move move) {
        //realisation is in implementations
    }

    /**
     * Скидає гру до початкового стану:
     * очищує дошку, встановлює початкового гравця (звичайно {@code X})
     * та оновлює результат гри на "триває".
     */
    public void reset() {
        //realisation is in implementations
    }

    /**
     * Повертає поточний стан дошки у вигляді об’єкта {@link BoardView},
     * який може бути використаний для візуалізації.
     *
     * @return представлення поточного стану ігрового поля
     */
    public BoardView getState() {
        //realisation is in implementations
        return null;
    }

    /**
     * Повертає переможця гри (якщо він існує).
     *
     * @return {@link Optional} з {@link Player} — гравцем-переможцем, або {@code Optional.empty()} якщо ще немає переможця
     */
    public Optional<Player> getWinner() {
        //realisation is in implementations
        return Optional.empty();
    }

    /**
     * Перевіряє, чи гра досягла кінцевого стану (перемога або нічия).
     *
     * @return {@code true}, якщо гра завершена; {@code false} — якщо ще триває
     */
    public boolean isTerminal() {
        //realisation is in implementations
        return false;
    }

    /**
     * Перевіряє коректність заданого ходу {@link Move}.
     * Хід вважається некоректним, якщо:
     * <ul>
     *   <li>гра вже завершена;</li>
     *   <li>гравець намагається ходити не у свою чергу;</li>
     *   <li>координати виходять за межі дошки 3x3;</li>
     *   <li>клітинка вже зайнята.</li>
     * </ul>
     *
     * @param move об’єкт {@link Move} для перевірки
     * @throws IllegalMoveException якщо будь-яке з правил порушено
     */
    public void validateMove(Move move) {
        //realisation is in implementations
    }

    /**
     * Повертає гравця, чий зараз хід.
     *
     * @return поточний {@link Player}
     */
    public Player turn() {
        //realisation is in implementations
        return null;
    }

    /**
     * Ініціалізує ігрову дошку — створює масив клітинок {@link Cell} розміром 9 (3x3).
     * Усі клітинки заповнюються значенням {@code EMPTY}.
     */
    public void initBoard() {
        //realisation is in implementations
    }

    /**
     * Перевіряє, чи всі клітинки на дошці заповнені (тобто немає жодної {@code EMPTY}).
     *
     * @return {@code true}, якщо дошка повна; {@code false} — якщо залишились вільні клітинки
     */
    public boolean isBoardFull() {
        //realisation is in implementations
        return false;
    }

    /**
     * Встановлює набір виграшних комбінацій (ліній) для гри.
     * Це 8 комбінацій: 3 горизонталі, 3 вертикалі і 2 діагоналі.
     */
    public void setLines() {
        //realisation is in implementations
    }

    /**
     * Перевіряє, чи є на дошці виграшна комбінація.
     * Якщо будь-яка лінія з {@code setLines()} заповнена однаковими символами (X або O),
     * гра вважається виграною.
     *
     * @return {@code true}, якщо є переможець; {@code false} — якщо ні
     */
    public boolean hasWin() {
        //realisation is in implementations
        return false;
    }

    /**
     * Перевіряє, чи три клітинки з індексами {@code i}, {@code j}, {@code k}
     * утворюють виграшну комбінацію (усі однакові й не порожні).
     *
     * @param i перший індекс клітинки
     * @param j другий індекс клітинки
     * @param k третій індекс клітинки
     * @return {@code true}, якщо всі три клітинки мають однаковий непорожній символ; {@code false} — інакше
     */
    public boolean threeInRow(int i, int j, int k) {
        //realisation is in implementations
        return false;
    }

    //additional methods getters and setters for testing
    public int[][] getLines() {
        return lines;
    }

    public Cell[] getBoard() {
        return board;
    }

    public void setBoard(Cell[] board) {
        this.board = board;
    }

    public Player getTurn() {
        return turn;
    }

    public void setTurn(Player turn) {
        this.turn = turn;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Перелік можливих станів однієї клітинки дошки:
     * <ul>
     *     <li>{@code EMPTY} — порожня клітинка;</li>
     *     <li>{@code X} — клітинка, зайнята гравцем X;</li>
     *     <li>{@code O} — клітинка, зайнята гравцем O.</li>
     * </ul>
     */
    public enum Cell {
        EMPTY, X, O
    }
}