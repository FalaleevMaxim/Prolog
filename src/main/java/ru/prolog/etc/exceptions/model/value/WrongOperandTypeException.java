package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.values.ExprValueModel;

public class WrongOperandTypeException extends ValueStateException {
    private final ExprValueModel operand;

    public WrongOperandTypeException(ExprValueModel expr, ExprValueModel operand, String message) {
        super(expr, message);
        this.operand = operand;
    }

    public ExprValueModel getOperand() {
        return operand;
    }
}
