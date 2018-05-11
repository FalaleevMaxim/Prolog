package ru.prolog.logic.model.type.descriptions;

/**
 * Type for predicate arguments which can be any type
 * Values and variables can not use this type
 */
public class CommonType {
    public final Type type;

    public CommonType(Type type) {
        this.type = type;
    }

    public enum Type{
        ANY,//one argument of any type
        VARARG//one or more arguments of any types
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonType)) return false;

        CommonType that = (CommonType) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
