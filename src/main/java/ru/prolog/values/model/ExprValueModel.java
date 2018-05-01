package ru.prolog.values.model;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.value.ValueStateException;
import ru.prolog.model.exceptions.value.WrongOperandTypeException;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;
import ru.prolog.values.expression.ValueExpr;
import ru.prolog.values.expression.binary.*;
import ru.prolog.values.expression.unary.*;

import java.util.*;

public class ExprValueModel implements ValueModel{
    private String name;
    private ExprValueModel left;
    private ExprValueModel right;
    private ValueModel value;
    private boolean fixed = false;

    public ExprValueModel(ValueModel value) {
        this.value = value;
    }

    public ExprValueModel(String name) {
        this.name = name;
    }

    public ExprValueModel(String name, ExprValueModel left) {
        this.name = name;
        this.left = left;
    }

    public ExprValueModel(String name, ExprValueModel left, ExprValueModel right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    public String getName() {
        return name;
    }

    public ExprValueModel getLeft() {
        return left;
    }

    public ExprValueModel getRight() {
        return right;
    }

    public ValueModel getValue() {
        return value;
    }

    public void setLeftOperand(ExprValueModel left) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.left = left;
    }

    public void setRightOperand(ExprValueModel right) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.right = right;
    }

    public void setValue(ValueModel value) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.value = value;
    }

    @Override
    public Type getType() {
        Type real = Type.primitives.get("real");
        Type integer = Type.primitives.get("integer");
        switch (name){
            case "sin":
            case "cos":
            case "tan":
                return real;
            case "abs":
                return integer;
            case "-":
                if(right==null)
                    return left.getType();
                else
                    return left.getType().equals(real) || right.getType().equals(real)?
                            real : integer;
            case "+":
            case "*":
                return left.getType().equals(real) || right.getType().equals(real)?
                        real : integer;
            case "/":
                return real;
            case "div":
            case "mod":
                return integer;
            case "":
                return value.getType();
        }
        return null;
    }

    @Override
    public ExprValue forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("State is not fixed. Call fix() method before using model object.");
        switch (name){
            case "sin":
                return new SinExpr(left.forContext(context));
            case "cos":
                return new CosExpr(left.forContext(context));
            case "tan":
                return new TanExpr(left.forContext(context));
            case "abs":
                return new AbsExpr(left.forContext(context));
            case "-":
                if(right==null)
                    return new MinusUnaryExpr(left.forContext(context));
                else
                    return new MinusExpr(left.forContext(context), right.forContext(context));
            case "+":
                return new PlusExpr(left.forContext(context), right.forContext(context));
            case "/":
                return new DivRealExpr(left.forContext(context), right.forContext(context));
            case "*":
                return new MultiplyExpr(left.forContext(context), right.forContext(context));
            case "div":
                return new DivIntExpr(left.forContext(context), right.forContext(context));
            case "mod":
                return new ModExpr(left.forContext(context), right.forContext(context));
            case "":
                return new ValueExpr(value.forContext(context));
        }
        return null;
    }

    @Override
    public List<VariableModel> innerModelVariables() {
        List<VariableModel> variables = new ArrayList<>();
        if(value!=null) variables.addAll(value.innerModelVariables());
        if(left!=null) variables.addAll(left.innerModelVariables());
        if(right!=null) variables.addAll(right.innerModelVariables());
        return variables;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        //Check name is not null and is existing operator name
        if(name==null)
            exceptions.add(new ValueStateException(this, "Operator name is null"));
        else if(!Arrays.asList("","sin", "cos", "tan", "abs", "+", "-", "*", "/", "div", "mod").contains(name)){
            exceptions.add(new ValueStateException(this, "Expression \"" + name + "\" does not exist"));
        }
        //Check expression is value or has at least one operand
        if(value==null && left==null) {
            exceptions.add(new ValueStateException(this, "Neither value nor operand set for expression"));
        }else{
            if(left==null && !"".equals(name))
                exceptions.add(new ValueStateException(this, "Expression is not value, but does not have operand"));
        }
        //No sense to continue if there are exceptions already
        if(!exceptions.isEmpty())
            return exceptions;
        //Check operands or value for exceptions
        if(value!=null) exceptions.addAll(value.exceptions());
        if(left!=null) exceptions.addAll(left.exceptions());
        if(right!=null) exceptions.addAll(right.exceptions());
        if(!exceptions.isEmpty())
            return exceptions;

        if(value!=null && !name.equals("")){
            exceptions.add(new ValueStateException(this, "Expression is not value, but value is not null"));
        }

        //Operator-specific checks
        Type real = Type.primitives.get("real");
        Type integer = Type.primitives.get("integer");
        switch (name){
            //Empty name means expression is value. Check value is not null and is number.
            case "":
                if(value==null){
                    exceptions.add(new ValueStateException(this, "Empty operator name means this expression is value, but value is null"));
                }else if(!value.getType().isPrimitive() || !value.getType().getPrimitiveType().isNumber()){
                    exceptions.add(new ValueStateException(this, "Wrong value type. Expected: integer or real."));
                }
                if(left!=null || right!=null)
                    exceptions.add(new ValueStateException(this, "Expression is value, operands should be null"));
                break;
            //Unary operators. Should not have right operand.
            case "sin":
            case "cos":
            case "tan":
            case "abs":
                if(right!=null)
                    exceptions.add(new ValueStateException(this, "Operator \"" + name + "\" is unary and does not require second operand"));
                break;
            //Minus can be unary or binary and can hava both real or integer operands. No exceptions there.
            case "-":
                break;
            //Binary operations with any types of arguments. Must have right argument
            case "+":
            case "*":
            case "/":
                if(right==null)
                    exceptions.add(new ValueStateException(this, "No right operand for binary operator "+name));
                break;
                //Binary operators requiring only integer operands
            case "div":
            case "mod":
                if(!left.getType().getPrimitiveType().isInteger())
                    exceptions.add(new WrongOperandTypeException(this, left, "Operands of '"+ name +"' must be integer"));
                if(right==null)
                    exceptions.add(new ValueStateException(this, "No right operand for binary operator "+name));
                else if(!right.getType().getPrimitiveType().isInteger())
                    exceptions.add(new WrongOperandTypeException(this, right, "Operands of '"+ name +"' must be integer"));
                break;
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        if(value!=null) value.fix();
        if(left!=null) left.fix();
        if(right!=null) right.fix();
        return this;
    }

    @Override
    public String toString() {
        if(value!=null) return value.toString();
        if(Arrays.asList("sin", "cos", "tan", "abs").contains(name) || ("-".equals(name) && right==null))
            return name + "(" + left.toString() + ")";
        if(Arrays.asList("+","*","/","div","mod").contains(name) || "-".equals(name))
            return "(" + left.toString() + " " + name + " " + right.toString() + ")";
        return "";
    }
}
