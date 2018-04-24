package ru.prolog.service;

import ru.prolog.service.option.Option;

import java.util.List;

public abstract class AbstractManager<T> {
    private final List<Option<T>> options;

    public AbstractManager(List<Option<T>> options) {
        this.options = options;
    }

    protected List<Option<T>> getOptions() {
        return options;
    }

    protected T decorate(T base){
        for(Option<T> option : options){
            base = option.decorate(base);
        }
        return base;
    }
}
