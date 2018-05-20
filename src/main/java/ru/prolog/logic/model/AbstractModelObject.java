package ru.prolog.logic.model;

import ru.prolog.compiler.position.ModelCodeIntervals;

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
}
