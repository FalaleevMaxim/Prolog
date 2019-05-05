package ru.prolog.util.window;

public interface PrologWindowManager {

    /**
     * Возвращает активное окно.
     *
     * @return Текущее окно.
     * @see #goToWindow(int)
     * @see #shiftWindow(int)
     */
    PrologWindow getCurrentWindow();

    /**
     * Возвращает окно по его id. Id задаётся при создании окна.
     *
     * @param id Id окна.
     * @return Окно по его id или {@code null} если окна с таким id нет.
     * @see #makeWindow(int, int, int, String, int, int, int, int)
     */
    PrologWindow getWindowById(int id);

    /**
     * Соответствует предикату makewindow. Создаёт окно с заданными параметрами и делает его активным
     *
     * @param id             id окна
     * @param textAndBgColor код, шифрующий цвет текста и фона ({@link WindowColor#textAndBackgroundColors(int)})
     * @param borderColor    цвет рамки
     * @param title          Заголовок окна
     * @param y              координата y левого верхнего угла окна
     * @param x              координата x левого верхнего угла окна
     * @param height         высона окна
     * @param width          ширина окна
     * @return Созданное окно
     */
    PrologWindow makeWindow(int id, int textAndBgColor, int borderColor, String title, int y, int x, int height, int width);

    /**
     * Переключение на окно с указанным id.
     * Используется для быстрого переключения между окнами, которые не перекрываются.
     * Если для системы окон нет разницы, перекрываются окна или нет, этот метод будет эквивалентным {@link #shiftWindow(int)}
     *
     * @param id Id окна
     * @return {@code true} если окно с заданным id существует; {@code false} если окна с заданным id нет.
     */
    boolean goToWindow(int id);

    /**
     * Переключение на окно с указанным id.
     *
     * @param id Id окна
     * @return {@code true} если окно с заданным id существует; {@code false} если окна с заданным id нет.
     */
    boolean shiftWindow(int id);

    /**
     * Удаляет окно с указанным id.
     *
     * @param id Id окна
     * @return {@code true} если окно с заданным id существует; {@code false} если окна с заданным id нет.
     */
    boolean removeWindow(int id);
}