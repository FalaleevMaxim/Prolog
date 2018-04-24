package ru.prolog.model.rule;

import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule with no body.
 * All methods already implemented in {@link AbstractRule Rule} class.
 * This class is made to separate facts from rules with body because facts have different purposes and rules do not extend them. For example, only facts are allowed to be put in database.
 */
public class FactRule extends AbstractRule {
    public FactRule(Predicate predicate, List<Value> toUnificateList) {
        super(predicate, toUnificateList);
    }

    public static class Builder implements RuleBuilder<FactRule>{
        private String name;
        private List<Value> toUnifyList;
        private FactRule created;
        boolean closed = false;

        public Builder(String name){
            this.name = name;
        }

        @Override
        public FactRule create() {
            if(created!=null) return created;
            if(toUnifyList ==null) toUnifyList = Collections.emptyList();
            created = new FactRule(null, toUnifyList);
            return created;
        }

        @Override
        @SuppressWarnings("Duplicates")
        public void setPredicate(Predicate predicate) {
            if(closed) throw new IllegalStateException("Builder is closed");
            if(!predicate.getName().equals(name)) throw new IllegalArgumentException("Predicate functorName does not match rule functorName");
            if(predicate.getArgTypes().size()!=toUnifyList.size()) throw new IllegalArgumentException("Predicate args list has different size");
            for (int i = 0; i < predicate.getArgTypes().size(); i++) {
                Type predicateArgType = predicate.getTypeStorage().get(predicate.getArgTypes().get(i));
                Type ruleArgType = toUnifyList.get(i).getType();
                if(!predicateArgType.equals(ruleArgType)){
                    throw new WrongTypeException("Type of predicate and rule argument do not match.", predicateArgType, ruleArgType);
                }
            }
            if(created==null) create();
            created.predicate = predicate;
        }

        @Override
        public void addUnifyValue(Value val) {
            if(toUnifyList ==null) toUnifyList = new ArrayList<>();
            toUnifyList.add(val);
        }

        @Override
        public List<Value> getUnifyArgs() {
            return Collections.unmodifiableList(toUnifyList);
        }

        @Override
        public FactRule close() {
            closed = true;
            return create();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }
    }
}
