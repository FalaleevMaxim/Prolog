package ru.prolog.runtime.values.expression.binary;

import ru.prolog.runtime.values.expression.ExprValue;
import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.expression.ExprValue;
import ru.prolog.runtime.values.simple.SimpleValue;

public class MinusExpr extends IntRealExpr {
    public MinusExpr(ExprValue left, ExprValue right) {
        super("-", left, right);
    }

    @Override
    protected Double realOperation(double left, double right) {
        return left-right;
    }

    @Override
    protected Integer intOperation(int left, int right) {
        return left-right;
    }

    @Override
    protected void reverse(Value res) {
        if (res.getType().getPrimitiveType().isInteger()) {
            reverseInteger(res);
        } else {
            reverseReal(res);
        }
    }

    private void reverseInteger(Value res) {
        if (left.innerFreeVariables().size() > 0) {
            Integer rightVal = (Integer) right.getValue();
            Integer resVal = (Integer) res.getValue();
            res = new SimpleValue(left.getType(), rightVal + resVal);
            left.unify(res);
        } else {
            Integer leftVal = (Integer) left.getValue();
            Integer resVal = (Integer) res.getValue();
            res = new SimpleValue(left.getType(), leftVal - resVal);
            right.unify(res);
        }
    }

    private void reverseReal(Value res) {
        if (left.innerFreeVariables().size() > 0) {
            checkFreeNotInteger(left);
            Double rightVal = right.getValue().doubleValue();
            Double resVal = (Double) res.getValue();
            res = new SimpleValue(left.getType(), rightVal + resVal);
            left.unify(res);
        } else {
            checkFreeNotInteger(right);
            Double leftVal = left.getValue().doubleValue();
            Double resVal = (Double) res.getValue();
            res = new SimpleValue(left.getType(), leftVal - resVal);
            right.unify(res);
        }
    }

    private void checkFreeNotInteger(ExprValue free) {
        if (free.getType().getPrimitiveType().isInteger()) {
            throw new FreeVariableException(
                    "Free variable in minus must be real when result is real",
                    free.innerFreeVariables().get(0));
        }
    }
}
