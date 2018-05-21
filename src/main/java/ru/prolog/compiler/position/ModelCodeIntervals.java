package ru.prolog.compiler.position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelCodeIntervals {
    private final CodeInterval baseInterval;
    private final CodeInterval fullInterval;
    private List<CodeInterval> args = Collections.emptyList();
    private int lBr=-1, rBr=-1;

    /**
     * For objects having different base and full intervals, such as predicates, functors, etc
     * @param baseInterval base interval of object, which can not intersect with any other base interval.
     * @param fullInterval full interval belonging to object, including all its parts.
     */
    public ModelCodeIntervals(CodeInterval baseInterval, CodeInterval fullInterval) {
        this.baseInterval = baseInterval;
        this.fullInterval = fullInterval;
    }

    /**
     * For objects which have same full and base interval (e.g. variables or primitives)
     * @param interval interval of code belonging to the object.
     */
    public ModelCodeIntervals(CodeInterval interval) {
        this.fullInterval = interval;
        this.baseInterval = interval;
    }

    /**
     * For list values. Sets full interval from left to right bracket and positions for brackets
     * @param fullInterval code interval from left to right bracket
     * @param lBr left bracket position
     * @param rBr right bracket position
     */
    public ModelCodeIntervals(CodeInterval fullInterval, int lBr, int rBr) {
        this.fullInterval = fullInterval;
        this.baseInterval = null;
        this.lBr = lBr;
        this.rBr = rBr;
    }

    /**
     * For rules and statements
     * @param baseInterval base interval of object, which can not intersect with any other base interval.
     * @param fullInterval full interval belonging to object, including all its parts.
     * @param lBr left bracket position (<0 if no brackets)
     * @param rBr right bracket position (<0 if no brackets)
     */
    public ModelCodeIntervals(CodeInterval baseInterval, CodeInterval fullInterval, int lBr, int rBr) {
        this.baseInterval = baseInterval;
        this.fullInterval = fullInterval;
        this.args = Collections.unmodifiableList(new ArrayList<>(args));
        this.lBr = lBr;
        this.rBr = rBr;
    }

    /**
     * Allows to set all attributes: base and full intervals, args intervals, brackets.
     * @param baseInterval base interval of object, which can not intersect with any other base interval.
     * @param fullInterval full interval belonging to object, including all its parts.
     * @param args args positions if args are not ModelObject
     * @param lBr left bracket position (<0 if no brackets)
     * @param rBr right bracket position (<0 if no brackets)
     */
    public ModelCodeIntervals(CodeInterval baseInterval, CodeInterval fullInterval, List<CodeInterval> args, int lBr, int rBr) {
        this.baseInterval = baseInterval;
        this.fullInterval = fullInterval;
        this.args = Collections.unmodifiableList(new ArrayList<>(args));
        this.lBr = lBr;
        this.rBr = rBr;
    }

    /**
     * Returns code interval for basic part of object which can not intersect with any other object's basic interval.
     * For example, for "write(X)", base interval will include only "write".
     * Variables and primitive values have base interval same as {@link #getFullInterval()} full interval
     * Lists do not have base interval, but have full interval.
     * @return base interval, which identifies object or null if object does not have it.
     */
    public CodeInterval getBaseInterval() {
        return baseInterval;
    }

    /**
     * Returns full interval, i.e. full space of code belonging to object, including all its parts.
     * For example, all code "ancestor(X,Y):-parent(X,Y)" will be full interval for rule "ancestor", and it will include intervals for X, Y and statement "parent(X,Y)".
     * For the same rule, the base interval will be only "ancestor"
     * @return full interval belonging to object
     */
    public CodeInterval getFullInterval() {
        return fullInterval;
    }

    /**
     * Returns intervals for args, which are not instance of ModelObject. It can be used for type names in functor or predicate definitions
     * For example, for predicate "append(list, integer, list)", this method will return intervals for "list", "integer", "list"
     * @return code intervals for args which are not ModelObject, or empty list if object does not contain any
     */
    public List<CodeInterval> getArgs() {
        return args;
    }

    /**
     * @return character number of left bracket or -1 if object does not have one
     */
    public int getLbr() {
        return lBr;
    }

    /**
     * @return character number of right bracket or -1 if object does not have one
     */
    public int getRbr() {
        return rBr;
    }
}
