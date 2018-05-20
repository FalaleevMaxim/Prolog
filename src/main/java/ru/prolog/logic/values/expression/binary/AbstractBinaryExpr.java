package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.expression.AbstractExprValue;
import ru.prolog.logic.values.expression.ExprValue;
import ru.prolog.logic.model.values.ExprValueModel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBinaryExpr extends AbstractExprValue{
    protected ExprValue left, right;

    protected AbstractBinaryExpr(String name, ExprValue left, ExprValue right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    public ExprValue getLeft() {
        return left;
    }

    public ExprValue getRight() {
        return right;
    }

    @Override
    public List<Variable> innerFreeVariables() {
        List<Variable> variables = new ArrayList<>();
        variables.addAll(left.innerFreeVariables());
        variables.addAll(right.innerFreeVariables());
        return variables;
    }

    @Override
    public ExprValueModel toModel() {
        return new ExprValueModel(name, left.toModel(), right.toModel());
    }
}
