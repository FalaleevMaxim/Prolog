package ru.prolog.util.window;

import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDevice;

/**
 * Представляет собой окно, близкое к стандартному окну, создаваемому предикатом MakeWindow в Turbo Prolog.
 * Окно должно поддерживать возможность ввода, вывода и установки курсора на заданную позицию.
 * Для окна при создании определяется заголовок, размер (в символах), позиция, цвет рамки, фона и текста.
 */
public interface PrologWindow extends OutputDevice, InputDevice {
    /**
     * Устанавливает курсор в заданную позицию
     *
     * @param line строка
     * @param col  столбец (номер символа в строке)
     */
    void setCursor(int line, int col);

    /**
     * Устанавливает фокус в данное окно
     */
    void setFocus();

    /**
     * Очищает содержимое окна.
     */
    void clear();
}
