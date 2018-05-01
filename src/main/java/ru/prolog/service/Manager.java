package ru.prolog.service;

import ru.prolog.model.ModelObject;
import ru.prolog.service.option.Option;

import java.util.List;

public interface Manager<T> extends ModelObject {
    List<Option<T>> getOptions();
    void addOption(Option<T> option);
}
