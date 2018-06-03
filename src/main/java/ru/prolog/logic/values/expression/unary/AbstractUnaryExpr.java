package ru.prolog.logic.values.expression.unary;

import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.expression.AbstractExprValue;
import ru.prolog.logic.values.expression.ExprValue;
import ru.prolog.logic.model.values.ExprValueModel;

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
