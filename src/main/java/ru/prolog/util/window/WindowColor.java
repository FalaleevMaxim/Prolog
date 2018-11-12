package ru.prolog.util.window;

import java.util.Objects;

/**
 * Представляет собой цвет в формате RGB.
 * Содержит статические методы для перевода кодов цвета Turbo Prolog в RGB-цвета.
 */
public class WindowColor {
    /**
     * Красная компонента цвета
     */
    private final int r;
    /**
     * Зелёная компонента цвета
     */
    private final int g;
    /**
     * Синяя компонента цвета
     */
    private final int b;

    /**
     * Создаёт цвет по трём компонентам, переданным отдельно
     *
     * @param r {@link #r Красная компонента цвета  }
     * @param g {@link #g Зелёная компонента цвета  }
     * @param b {@link #b Синяя компонента цвета    }
     */
    public WindowColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Создаёт цвет по трём компонентам, упакованным в один int.
     * Например, для {@code 0x123456}, {@link #r} = 0x12; {@link #g} = 0x34;  {@link #b} = 0x56;
     *
     * @param rgb
     */
    public WindowColor(int rgb) {
        b = rgb & 0xFF;
        rgb >>>= 8;
        g = rgb & 0xFF;
        rgb >>>= 8;
        r = rgb & 0xFF;
    }

    public int r() {
        return r;
    }

    public int g() {
        return g;
    }

    public int b() {
        return b;
    }

    public int rgb() {
        return (r << 16) + (g << 8) + b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowColor that = (WindowColor) o;
        return r == that.r &&
                g == that.g &&
                b == that.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }

    /**
     * Приводит к строке в виде шестнадцатеричного кода цвета со знаком '#' в начале.
     *
     * @return Строка с hex-кодом цвета
     */
    @Override
    public String toString() {
        return "#" + Integer.toHexString(r)
                + Integer.toHexString(g)
                + Integer.toHexString(b);
    }

    /**
     * Возвращает цвет текста по коду для предиката makeWindow
     * Второй аргумент предиката makeWindow получается сложением кода цвета текста и кода цвета фона ({@link #windowBackgroundColor(int)})
     * Цвет близкий к реальному цвету в Turbo Prolog.
     * Список кодов цветов:
     * код  |Возвращаемый   |Документация TP
     * 0    |Black          |Чёрный
     * 1    |Blue           |Синий
     * 2    |Light green    |Зелёный
     * 3    |LightBlue      |Голубой
     * 4    |Red            |Красный
     * 5    |Purple         |Фиолетовый
     * 6    |DarkOrange     |Коричневый
     * 7    |Silver         |Белый
     * 8    |Gray           |Серый
     * 9    |SlateBlue      |Светло-синий
     * 10   |LightGreen     |Светло-зелёный
     * 11   |Cyan           |Светло-голубой
     * 12   |LightCoral     |Светло-красный
     * 13   |Violet         |светло-фиолетовый
     * 14   |Yellow         |Жёлтый
     * 15   |White          |Интенсивно-белый
     *
     * @param code код цвета текста от 0 до 15
     * @return Цвет текста по его коду
     * @throws IllegalArgumentException Если код не в допустимом интервале
     */
    public static WindowColor windowTextColor(int code) {
        switch (code) {
            case 0:
                //Black
                return new WindowColor(0x000000);
            case 1:
                //Blue
                return new WindowColor(0x0000FF);
            case 2:
                //Green
                return new WindowColor(0x008000);
            case 3:
                //LightBlue
                return new WindowColor(0x87CEEB);
            case 4:
                //Red
                return new WindowColor(0xFF0000);
            case 5:
                //Purple
                return new WindowColor(0x800080);
            case 6:
                //DarkOrange
                return new WindowColor(0xFF8C00);
            case 7:
                //Silver
                return new WindowColor(0xC0C0C0);
            case 8:
                //Gray
                return new WindowColor(0x808080);
            case 9:
                //SlateBlue
                return new WindowColor(0x6A5ACD);
            case 10:
                //LightGreen
                return new WindowColor(0x90EE90);
            case 11:
                //Cyan
                return new WindowColor(0x00FFFF);
            case 12:
                //LightCoral
                return new WindowColor(0xF08080);
            case 13:
                //Violet
                return new WindowColor(0xEE82EE);
            case 14:
                //Yellow
                return new WindowColor(0xFFFF00);
            case 15:
                //White
                return new WindowColor(0xFFFFFF);
            default:
                throw new IllegalArgumentException("Codes for text colors are from 0 to 15");
        }
    }

    /**
     * Возвращает цвет фона по коду для предиката makeWindow
     * Второй аргумент предиката makeWindow получается сложением кода цвета фона и кода цвета текста ({@link #windowTextColor(int)})
     * Цвет близкий к реальному цвету в Turbo Prolog.
     * Список кодов цветов:
     * код  |Возвращаемый   |Документация TP
     * 0    |Black          |Чёрный
     * 16    |Blue          |Синий
     * 32    |Light green   |Зелёный
     * 48    |LightBlue     |Голубой
     * 64    |Red           |Красный
     * 80    |Purple        |Фиолетовый
     * 96    |DarkOrange    |Коричневый
     * 112   |Silver        |Белый
     *
     * @param code код цвета текста от 0 до 15
     * @return Цвет текста по его коду
     * @throws IllegalArgumentException Если код не в допустимом интервале
     */
    public static WindowColor windowBackgroundColor(int code) {
        switch (code) {
            case 0:
                //Black
                return new WindowColor(0x000000);
            case 16:
                //Blue
                return new WindowColor(0x0000FF);
            case 32:
                //Green
                return new WindowColor(0x008000);
            case 48:
                //LightBlue
                return new WindowColor(0x87CEEB);
            case 64:
                //Red
                return new WindowColor(0xFF0000);
            case 80:
                //Purple
                return new WindowColor(0x800080);
            case 96:
                //DarkOrange
                return new WindowColor(0xFF8C00);
            case 112:
                //Silver
                return new WindowColor(0xC0C0C0);
            default:
                throw new IllegalArgumentException("llegal background code");
        }
    }

    /**
     * Возвращает цвет рамки окна по его коду. Коды цветов рамки совпадают с кодами цветов текста {@link #windowTextColor(int)}
     *
     * @param code код цвета рамки окна
     * @return цвет для рамки окна
     */
    public static WindowColor windowBorderColor(int code) {
        return windowTextColor(code);
    }

    /**
     * Разбивает код - второй аргумент предиката makewindow с помощью битовой маски на две части:
     * код цвета текста и код цвета фона, получает соответствующие цвета и возвращает их в виде массива из 2 элементов.
     * Код получается суммированием кодов цветов текста ({@link #windowTextColor(int)}) и фона ({@link #windowBackgroundColor(int)})
     *
     * @param code код цветов текста и фона - второй аргумент предиката makewindow
     * @return массив из двух цветов: первый элемент - цвет текста; второй - цвет фона.
     * @see #windowTextColor(int)
     * @see #windowBackgroundColor(int)
     */
    public static WindowColor[] textAndBackgroundColors(int code) {
        return new WindowColor[]{
                windowTextColor(code & 0xF),
                windowBackgroundColor(code & 0xF0)
        };
    }
}
