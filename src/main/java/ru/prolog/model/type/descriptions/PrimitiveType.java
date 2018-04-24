package ru.prolog.model.type.descriptions;

public class PrimitiveType {
    private final String name;

    private boolean isInteger;
    private boolean isReal;
    private boolean isChar;
    private boolean isString;

    public PrimitiveType(String name, boolean isInteger, boolean isReal, boolean isChar, boolean isString){
        this.name = name;
        this.isInteger = isInteger;
        this.isReal = isReal;
        this.isChar = isChar;
        this.isString = isString;

    }

    public String getName() {
        return name;
    }

    public boolean isNumber() {
        return isInteger || isReal;
    }

    public boolean isInteger() {
        return isInteger;
    }

    public boolean isReal() {
        return isReal;
    }

    public boolean isChar() {
        return isChar;
    }
    public boolean isString() {
        return isString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveType)) return false;

        PrimitiveType that = (PrimitiveType) o;

        if (isInteger != that.isInteger) return false;
        if (isReal != that.isReal) return false;
        if (isChar != that.isChar) return false;
        if (isString != that.isString) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isInteger ? 1 : 0);
        result = 31 * result + (isReal ? 1 : 0);
        result = 31 * result + (isChar ? 1 : 0);
        result = 31 * result + (isString ? 1 : 0);
        return result;
    }
}
