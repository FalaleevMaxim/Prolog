package ru.prolog.runtime.values.expression.binary;

import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.expression.ExprValue;
import ru.prolog.runtime.values.simple.SimpleValue;

public class PlusExpr extends IntRealExpr {

    public PlusExpr(ExprValue left, ExprValue right) {
        super("+", left, right);
    }

    @Override
    protected Double realOperation(double left, double right) {
        return left+right;
    }

    @Override
    protected Integer intOperation(int left, int right) {
        return left+right;
    }

    @Override
    protected void reverse(Value res) {
        ExprValue free;
        ExprValue nonFree;
        if (left.innerFreeVariables().size() == 0) {
            nonFree = left;
            free = right;
        } else {
            free = left;
            nonFree = right;
        }
        if (res.getType().getPrimitiveType().isInteger()) {
            //Если тип результата integer, то оба слагаемых тоже integer, и вычисления можно производить в целых числах
            Integer nonFreeVal = (Integer) nonFree.getContent();
            Integer resVal = (Integer) res.getContent();
            res = new SimpleValue(free.getType(), resVal - nonFreeVal);
            free.unify(res);
        } else {
            //Для real сложнее. Оба значения приводятся к Double
            Double nonFreeVal = nonFree.getContent().doubleValue();
            Double resVal = ((Number) res.getContent()).doubleValue();
            //Результат создаётся в зависимости от типа слагаемого, содержащего свободную переменную
            if (free.getType().getPrimitiveType().isReal()) {
                res = new SimpleValue(free.getType(), (resVal - nonFreeVal));
            } else {
                res = new SimpleValue(free.getType(), (int) (resVal - nonFreeVal));
            }
            free.unify(res);
        }
    }
}
