package ru.prolog.logic.model.exceptions.value;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.model.ValueModel;
import ru.prolog.logic.values.model.ListValueModel;

public class WrongListElementTypeException extends ValueStateException {
    private final ValueModel element;


    public WrongListElementTypeException(ListValueModel list, ValueModel element) {
        this(list, element, "List element type does not match list type");
    }

    public WrongListElementTypeException(ListValueModel list, ValueModel element, String message) {
        super(list, message);
        this.element = element;
    }

    public ValueModel getElement() {
        return element;
    }

    public ListValueModel getList(){
        return (ListValueModel)sender;
    }

    public Type getListType(){
        return getList().getType();
    }

    public Type getElementType(){
        return getElement().getType();
    }
}
