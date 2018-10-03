package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.values.ExprValueModel;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.expression.AbstractExprValue;
import ru.prolog.runtime.values.expression.ExprValue;

import java.util.List;

public abstract class AbstractUnaryExpr extends AbstractExprValue{
    protected ExprValue innerExpr;

    public AbstractUnaryExpr(String name, ExprValue innerExpr) {
        super(name);
        this.innerExpr = innerExpr;
    }

    @Override
    public List<Variable> innerFreeVariables() {
        return innerExpr.innerFreeVariables();
    }

    public ExprValue getInnerExpr() {
        return innerExpr;
    }

    @Override
    public ExprValueModel toModel() {
        return new ExprValueModel(name, innerExpr.toModel());
    }
}
