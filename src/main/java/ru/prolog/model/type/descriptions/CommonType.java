package ru.prolog.model.type.descriptions;

/**
 * Тип для аргументов предиката, которые могут быть любого типа.
 * Не может использоваться в качестве типа для значения или переменной.
 */
public class CommonType {
    /**
     * Вид обобщённного типа
     */
    public final CommonTypeKind kind;

    public CommonType(CommonTypeKind kind) {
        this.kind = kind;
    }

    /**
     * Вид обобщённого типа
     */
    public enum CommonTypeKind {
        /**
         * Один аргумент любого типа
         */
        ANY,
        /**
         * Один или более аргументов любого типа
         */
        VARARG//one or more arguments of any types
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonType)) return false;

        CommonType that = (CommonType) o;

        return kind == that.kind;
    }

    @Override
    public int hashCode() {
        return kind.hashCode();
    }
}
