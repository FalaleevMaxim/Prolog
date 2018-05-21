package ru.prolog.compiler.position;

public class CodePos {
    private final int line;
    private final int posInLine;

    public CodePos(int line, int posInLine) {
        this.line = line;
        this.posInLine = posInLine;
    }

    public int getLine() {
        return line;
    }

    public int getPosInLine() {
        return posInLine;
    }

    @Override
    public String toString() {
        return String.format("(%d:%d)", line, posInLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodePos)) return false;

        CodePos codePos = (CodePos) o;

        if (line != codePos.line) return false;
        return posInLine == codePos.posInLine;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + posInLine;
        return result;
    }
}
