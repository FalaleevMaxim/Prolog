package ru.prolog.logic.managers;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.managers.option.Option;

import java.util.List;

public interface Manager<T> extends ModelObject {
    List<Option<T>> getOptions();
    void addOption(Option<T> option);
}
