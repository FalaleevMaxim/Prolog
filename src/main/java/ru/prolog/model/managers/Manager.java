package ru.prolog.model.managers;

import ru.prolog.model.ModelObject;
import ru.prolog.model.managers.option.Option;

import java.util.List;

public interface Manager<T> extends ModelObject {
    List<Option<T>> getOptions();
    void addOption(Option<T> option);
}
