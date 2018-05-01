package ru.prolog.model.exceptions.value;

import ru.prolog.model.type.Type;
import ru.prolog.values.model.ExprValueModel;

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
