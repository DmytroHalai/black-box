package org.example.app_logic.api;

public class IllegalMoveException extends RuntimeException {
    /**
     * Створює новий виняток {@code IllegalMoveException} з детальним повідомленням.
     *
     * @param message текстове пояснення причини помилки (наприклад, "Гра вже завершена" або "Хід за межами дошки")
     */
    public IllegalMoveException(String message) {
        super(message);
    }
}