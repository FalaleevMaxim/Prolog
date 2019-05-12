package ru.prolog.syntaxmodel.util;

/**
 * Метод {@link String#substring(int)} копирует массив символов из исходной строки в объект подстроки.
 * Это неоптимально при работе с частями исходного кода. Оптимальнее отображать часть массива символов исходнго кода как подстроку.
 */
public class MappingCharSequence implements CharSequence {
    private final String source;
    private final int start;
    private final int length;

    public MappingCharSequence(String source, int start, int length) {
        if (source == null) throw new IllegalArgumentException("Source string is null");
        if (start < 0 || start > source.length()) throw new StringIndexOutOfBoundsException(start);
        if (start + length >= source.length()) throw new StringIndexOutOfBoundsException(start + length);
        this.source = source;
        this.start = start;
        this.length = length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        if (index >= length) throw new StringIndexOutOfBoundsException(index);
        return source.charAt(start + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start >= length) throw new StringIndexOutOfBoundsException(start);
        if (end >= length) throw new StringIndexOutOfBoundsException(end);
        if (start == 0 && end == length - 1) return this;
        return new MappingCharSequence(source, this.start + start, end - start);
    }

    @Override
    public String toString() {
        return source.substring(start, start + length);
    }
}
