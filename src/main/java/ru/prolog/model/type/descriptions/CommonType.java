package ru.prolog.model.type.descriptions;

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
        ANYCOUNT//unlimited count of arguments of any types
    }
}
