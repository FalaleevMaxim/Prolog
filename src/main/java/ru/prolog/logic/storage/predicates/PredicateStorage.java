package ru.prolog.logic.storage.predicates;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.std.*;
import ru.prolog.logic.std.cast.*;
import ru.prolog.logic.std.compare.*;
import ru.prolog.logic.std.db.*;
import ru.prolog.logic.std.io.*;
import ru.prolog.logic.std.string.*;

import java.util.Collection;
import java.util.List;

public interface PredicateStorage extends ModelObject {
    Collection<Predicate> all();
    Collection<Predicate> get(String name);
    Predicate get(String name, int arity);
    Predicate getVarArgPredicate(String name);
    Predicate getFitting(String name, List<Type> types);
    void add(Predicate predicate);

    static boolean isBuiltInPredicate(Predicate p){
        return p instanceof Cut
                || p instanceof Cut2
                || p instanceof Fail
                || p instanceof Nl
                || p instanceof EqualsOperatorPredicate
                || p instanceof NotEqualsOperatorPredicate
                || p instanceof LessOperatorPredicate
                || p instanceof MoreOperatorPredicate
                || p instanceof MoreEqualsOperatorPredicate
                || p instanceof LessEqualsOperatorPredicate
                || p instanceof RandomPredicate
                || p instanceof WritePredicate
                || p instanceof WriteFPredicate
                || p instanceof ReadLnPredicate
                || p instanceof ReadCharPredicate
                || p instanceof ReadIntPredicate
                || p instanceof ReadRealPredicate
                || p instanceof FormatPredicate
                || p instanceof FrontCharPredicate
                || p instanceof ConcatPredicate
                || p instanceof AssertPredicate
                || p instanceof AssertaPredicate
                || p instanceof AssertzPredicate
                || p instanceof RetractPredicate
                || p instanceof RetractAllPredicate
                || p instanceof ConsultPredicate
                || p instanceof SavePredicate
                || p instanceof RealIntCastPredicate
                || p instanceof IntCharCastPredicate
                || p instanceof StrCharCastPredicate
                || p instanceof StringSymbolCastPredicate
                || p instanceof StrIntCastPredicate
                || p instanceof StrRealCastPredicate;
    }
}