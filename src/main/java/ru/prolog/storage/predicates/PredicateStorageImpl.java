package ru.prolog.storage.predicates;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.PrologPredicate;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.type.Type;
import ru.prolog.std.*;
import ru.prolog.storage.predicates.exceptions.SamePredicateException;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.model.ValueModel;

import java.util.*;
import java.util.stream.Collectors;

public class PredicateStorageImpl implements PredicateStorage {
    private Map<String, SortedMap<Integer, Predicate>> predicates = new HashMap<>();
    private TypeStorage typeStorage;
    private boolean fixed = false;

    public PredicateStorageImpl(TypeStorage typeStorage) {
        this.typeStorage = typeStorage;
        addBuiltInPredicates();
    }

    @Override
    public Collection<Predicate> all() {
        List<Predicate> all = new ArrayList<>();
        predicates.values().forEach(pr -> all.addAll(pr.values()));
        return all;
    }

    @Override
    public Collection<Predicate> get(String name) {
        if(!predicates.containsKey(name)) return null;
        return predicates.get(name).values();
    }

    @Override
    public Predicate get(String name, int arity) {
        if(!predicates.containsKey(name)) return null;
        return predicates.get(name).get(arity);
    }

    @Override
    public Predicate getVarArgPredicate(String name) {
        if(!predicates.containsKey(name)) return null;
        return predicates.get(name).get(Integer.MAX_VALUE);
    }

    @Override
    public Predicate getFitting(String name, List<Type> types) {
        Predicate p = get(name, types.size());
        if(p==null) p = getVarArgPredicate(name);
        if(p==null) return null;
        for(int i = 0; i<p.getArgTypeNames().size(); i++){
            Type predType = p.getTypeStorage().get(p.getArgTypeNames().get(i));
            Type reqType = types.get(i);
            if (!predType.isCommonType() && reqType.equals(predType)) {
                return null;
            }
        }
        return p;
    }

    @Override
    public void add(Predicate predicate) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        SortedMap<Integer, Predicate> predMap;
        if(!predicates.containsKey(predicate.getName())) {
            predMap = new TreeMap<>();
            predicates.put(predicate.getName(), predMap);
        }else{
            predMap = predicates.get(predicate.getName());
        }
        int arity = predicate.getArity();
        if(!predMap.containsKey(arity)){
            predMap.put(arity, predicate);
        }else{
            if(!isBuiltInPredicate(predicate))
                throw new SamePredicateException(predicate, predMap.get(arity));
        }
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();

        for(Predicate p : all()){
            //This horrible construction sets predicates to statements in rules if they are not set.
            if(p instanceof RuleExecutorPredicate){
                for(Rule r : ((RuleExecutorPredicate) p).getRules()) {
                    if (r instanceof StatementExecutorRule) {
                        for (Statement st : ((StatementExecutorRule)r).getStatements()){
                            if(st.getPredicate()==null){
                                st.setPredicate(getFitting(
                                        st.getPredicateName(),
                                        st.getArgs().stream()
                                                .map(ValueModel::getType)
                                                .collect(Collectors.toList())));
                            }
                        }
                    }
                }
            }
            exceptions.addAll(p.exceptions());
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()) throw exceptions.iterator().next();
        fixed = true;
        all().forEach(Predicate::fix);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder prSb = new StringBuilder("predicates\n");
        StringBuilder clSb = new StringBuilder("clauses\n");
        for(Predicate p : all()){
            if(p instanceof PrologPredicate){
                ((PrologPredicate)p).getRules().forEach(
                        rule -> clSb.append("\t").append(rule).append(".\n"));
                if(!(p instanceof  DatabasePredicate)){
                    prSb.append("\t").append(p).append("\n");
                }
            }
        }
        return prSb.append(clSb).toString();
    }

    private void addBuiltInPredicates(){
        add(new Cut());
        add(new Cut());
        add(new Fail());
        add(new Nl());
        add(new EqualsOperatorPredicate(typeStorage));
        add(new LessOperatorPredicate(typeStorage));
        add(new MoreOperatorPredicate(typeStorage));
        add(new RandomPredicate(typeStorage));
        add(new WritePredicate(typeStorage));
    }

    private boolean isBuiltInPredicate(Predicate p){
        return p instanceof Cut
            || p instanceof Cut2
            || p instanceof Fail
            || p instanceof Nl
            || p instanceof EqualsOperatorPredicate
            || p instanceof LessOperatorPredicate
            || p instanceof MoreOperatorPredicate
            || p instanceof RandomPredicate
            || p instanceof WritePredicate;
    }
}
