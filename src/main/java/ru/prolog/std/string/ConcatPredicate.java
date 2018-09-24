package ru.prolog.std.string;

import ru.prolog.logic.etc.backup.Backup;
import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.managers.backup.BackupManager;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Arrays;
import java.util.List;

public class ConcatPredicate extends AbstractPredicate {
    public ConcatPredicate(TypeStorage typeStorage) {
        super("concat", Arrays.asList("string", "string", "string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        Type string = typeStorage.get("string");

        Value strVal1 = args.get(0);
        Value strVal2 = args.get(1);
        Value strSumVal = args.get(2);

        //If first two not free, unify their sum with third arg.
        if(!isFreeVariable(strVal1) && !isFreeVariable(strVal2)){
            String s1 = (String) strVal1.getValue();
            String s2 = (String) strVal2.getValue();
            return new SimpleValue(string, s1 + s2).unify(strSumVal) ? 0 : -1;
        }

        //If sum and one of parts are free variables, throw exception
        if(isFreeVariable(strSumVal)){
            Variable free = null;
            if(isFreeVariable(strVal2)) free = (Variable) strVal2;
            if(isFreeVariable(strVal1)) free = (Variable) strVal1;
            if(free!=null)
                throw new FreeVariableException("One of strings ans sum string ("+free+" and "+strSumVal+") in concat predicate are free variables", free);
        }

        String sum = (String) strSumVal.getValue();

        //Calculate 2nd string from 1st and sum
        if(!isFreeVariable(strVal1)){
            String s1 = (String) strVal1.getValue();
            if(sum.length()<s1.length()) return -1;
            String s1FromSum = sum.substring(0, s1.length());
            if(!s1.equals(s1FromSum)) return -1;
            String s2 = sum.substring(s1.length());
            return new SimpleValue(string, s2).unify(strVal2) ? 0 : -1;
        }

        //Calculate 1st string from 2nd and sum
        if(!isFreeVariable(strVal2)){
            String s2 = (String) strVal2.getValue();
            if(sum.length()<s2.length()) return -1;
            String s2FromSum = sum.substring(s2.length());
            if(!s2.equals(s2FromSum)) return -1;
            String s1 = sum.substring(0, sum.length()-s2.length());
            return new SimpleValue(string, s1).unify(strVal2) ? 0 : -1;
        }

        //If both parts ate free variables, loop over sum split variants
        for (int i = startWith; i <= sum.length(); i++) {
            String s1 = sum.substring(0,startWith);
            String s2 = sum.substring(startWith);
            BackupManager backupManager = context.programContext().program().managers().getBackupManager();
            //Make backups to rollback and try next variant if unify fails.
            // It actually does not make sense now, because free variables always unify successfully, but better to do it just in case of other variable behavior in future
            Backup arg1Backup = backupManager.backup((Variable)strVal2);
            Backup arg2Backup = backupManager.backup((Variable)strVal2);
            if (strVal1.unify(new SimpleValue(string, s1)) && strVal2.unify(new SimpleValue(string, s2)))
                return  startWith;
            arg1Backup.rollback();
            arg2Backup.rollback();
        }

        return -1;
    }
}
