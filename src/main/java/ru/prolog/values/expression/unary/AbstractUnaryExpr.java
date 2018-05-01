package ru.prolog.values.expression.unary;

import ru.prolog.values.Variable;
import ru.prolog.values.expression.AbstractExprValue;
import ru.prolog.values.expression.ExprValue;
import ru.prolog.values.model.ExprValueModel;

import java.util.List;

public abstract class AbstractUnaryExpr extends AbstractExprValue implements UnaryExpr{
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

    public void setInnerExpr(ExprValue innerExpr) {
        this.innerExpr = innerExpr;
    }

    @Override
    public ExprValueModel toModel() {
        return new ExprValueModel(name, innerExpr.toModel());
    }
}
