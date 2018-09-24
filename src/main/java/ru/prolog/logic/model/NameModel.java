package ru.prolog.logic.model;

import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.logic.etc.exceptions.model.ModelStateException;

import java.util.Collection;
import java.util.Collections;

public class NameModel implements ModelObject {
    private final String name;
    private final ModelCodeIntervals interval;

    public NameModel(String name, ModelCodeIntervals interval) {
        this.name = name;
        this.interval = interval;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        return Collections.emptyList();
    }

    @Override
    public ModelObject fix() {
        return this;
    }

    @Override
    public ModelCodeIntervals getCodeIntervals() {
        return interval;
    }

    @Override
    public void setCodeIntervals(ModelCodeIntervals pos) {
    }

    public String getName() {
        return name;
    }
}
