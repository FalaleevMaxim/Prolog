package ru.prolog.compiler.position;

public class CodeInterval {
    private final CodePos startPos;
    private final int start, end;

    public CodeInterval(CodePos startPos, int start, int end) {
        this.startPos = startPos;
        this.start = start;
        this.end = end;
    }

    public CodePos getStartPos() {
        return startPos;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeInterval)) return false;

        CodeInterval that = (CodeInterval) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        return startPos.equals(that.startPos);
    }

    @Override
    public int hashCode() {
        int result = startPos.hashCode();
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }
}
