package ru.prolog.logic.storage.predicates;

import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.predicate.PrologPredicate;
import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.std.*;
import ru.prolog.logic.std.compare.*;
import ru.prolog.logic.std.db.*;
import ru.prolog.logic.std.io.*;
import ru.prolog.logic.std.string.FormatPredicate;
import ru.prolog.logic.std.string.FrontCharPredicate;
import ru.prolog.logic.storage.predicates.exceptions.SamePredicateException;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.model.values.ValueModel;

import static ru.prolog.logic.storage.predicates.PredicateStorage.isBuiltInPredicate;

import java.util.*;
import java.util.stream.Collectors;

public class PredicateStorageImpl extends AbstractModelObject implements PredicateStorage {
    private Map<String, SortedMap<Integer, Predicate>> predicates = new HashMap<>();
    private TypeStorage typeStorage;

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
        if(!predicates.containsKey(name)) return Collections.emptyList();
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
        if(!predicates.containsKey(name)) return null;
        Predicate p = get(name, types.size());
        if(p==null) p = getVarArgPredicate(name);
        if(p!=null){
            if(matchArgTypes(p, types)==types.size())
                return p;
            else p=null;
        }
        int match = 0;
        for (Map.Entry<Integer, Predicate> entry : predicates.get(name).entrySet()) {
            int m = matchArgTypes(entry.getValue(), types);
            if(m>match){
                match = m;
                p = entry.getValue();
            }
        }
        return p;
    }

    private int matchArgTypes(Predicate p, List<Type> types){
        int count = 0;
        for(int i = 0; i<p.getArgTypeNames().size() && i<types.size(); i++){
            Type predType = p.getTypeStorage().get(p.getArgTypeNames().get(i));
            Type reqType = types.get(i);
            if (predType.isCommonType() || predType.equals(reqType)) {
                count++;
            }
        }
        return count;
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
                        for (List<Statement> list : ((StatementExecutorRule) r).getStatements()) {
                            for (Statement st : list){
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
        add(new MoreEqualsOperatorPredicate(typeStorage));
        add(new LessEqualsOperatorPredicate(typeStorage));
        add(new RandomPredicate(typeStorage));
        add(new WritePredicate(typeStorage));
        add(new WriteFPredicate(typeStorage));
        add(new ReadLnPredicate(typeStorage));
        add(new ReadCharPredicate(typeStorage));
        add(new ReadIntPredicate(typeStorage));
        add(new ReadRealPredicate(typeStorage));
        add(new FormatPredicate(typeStorage));
        add(new FrontCharPredicate(typeStorage));
        add(new AssertPredicate(typeStorage));
        add(new AssertaPredicate(typeStorage));
        add(new AssertzPredicate(typeStorage));
        add(new RetractPredicate(typeStorage));
        add(new RetractAllPredicate(typeStorage));
        add(new SavePredicate(typeStorage));
        add(new ConsultPredicate(typeStorage));
    }
}
