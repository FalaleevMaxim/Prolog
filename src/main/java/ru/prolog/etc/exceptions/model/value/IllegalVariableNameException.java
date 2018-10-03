package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.values.ValueModel;

public class IllegalVariableNameException extends ValueStateException {
    private final String name;

    public IllegalVariableNameException(ValueModel sender, String name) {
        this(sender, name, "Name \"" + name + "\" can not be variable name");
    }

    public IllegalVariableNameException(ValueModel sender, String name, String message) {
        super(sender, message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
