package ru.prolog.logic.managers;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.managers.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractManager<T> implements Manager<T> {
    private List<Option<T>> options;
    private boolean fixed = false;

    public AbstractManager() {
    }

    public AbstractManager(List<Option<T>> options) {
        this.options = options;
    }

    public List<Option<T>> getOptions() {
        return options;
    }

    protected T decorate(T base){
        for(Option<T> option : options){
            base = option.decorate(base);
        }
        return base;
    }

    @Override
    public void addOption(Option<T> option) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if(options==null) options = new ArrayList<>();
        options.add(option);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        return Collections.emptyList();
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()) throw exceptions.iterator().next();
        fixed = true;
        if(options==null) options = Collections.emptyList();
        else options = Collections.unmodifiableList(new ArrayList<>(options));
        return this;
    }
}
