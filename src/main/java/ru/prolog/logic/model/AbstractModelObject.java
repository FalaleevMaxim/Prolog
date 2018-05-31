package ru.prolog.logic.model;

import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.logic.model.exceptions.ModelStateException;

import java.util.Collection;

public abstract class AbstractModelObject implements ModelObject {
    protected ModelCodeIntervals intervals;
    protected boolean fixed = false;

    @Override
    public ModelCodeIntervals getCodeIntervals() {
        return intervals;
    }

    @Override
    public void setCodeIntervals(ModelCodeIntervals pos) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.intervals = pos;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        //Throw first of exceptions if there are any.
        if(!exceptions.isEmpty())
            throw exceptions.iterator().next();
        //If no exceptions, fix.
        fixed = true;
        fixIfOk();
        return this;
    }

    //What to do on fix of no exceptions
    protected void fixIfOk(){}
}
