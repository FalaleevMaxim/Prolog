package ru.prolog.model.type.descriptions;

import ru.prolog.model.type.Type;
import ru.prolog.storage.type.TypeStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctorType {
    public List<SubFunctor> subFunctors;

    private FunctorType(){}

    public FunctorType(List<SubFunctor> subFunctors) {
        this.subFunctors = Collections.unmodifiableList(new ArrayList<>(subFunctors));
        new Type("foo", this);
    }

    /**
     * Represents functor functorName and args. One functor type can contain different subfunctors
     */
    public static class SubFunctor{
        public final String name;
        //ToDo: Somehow change String to Type. Problem is, functor can be recursive or use types declared after it.
        public final List<String> argTypes;

        public SubFunctor(String name, List<String> argTypes) {
            this.name = name;
            this.argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
        }
    }
}