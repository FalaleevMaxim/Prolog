package ru.prolog.logic.model.predicate;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ExceptionsCatcherProgramContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDevice;

import java.io.IOException;
import java.util.*;

public class InteractiveGoalPredicate extends GoalPredicate{
    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        OutputDevice out = context.programContext().getOutputDevices();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new ExceptionsCatcherProgramContext.ProgramInterruptedException();
            }
            try {
                out.println("\nWrite goal:");
                String str = context.programContext().getInputDevice().readLine();
                Collection<CompileException> exceptions = new ArrayList<>();
                Rule goal = PrologCompiler.parseOuterGoal(context.programContext().program(), str, exceptions);
                if (goal == null) {
                    for (CompileException e : exceptions) {
                        System.err.println(e);
                    }
                    continue;
                }

                exceptions.addAll(goal.exceptions());
                if (!exceptions.isEmpty()) {
                    for (CompileException e : exceptions) {
                        System.err.println(e);
                    }
                    continue;
                }

                goal.fix();
                RuleContext ruleContext = context.getRuleManager().context(goal, Collections.emptyList(), context);
                boolean r = ruleContext.execute();
                int solutions = 0;
                boolean hasVars = false;
                if(r) {
                    hasVars = printVariables(out, ruleContext, ++solutions);
                }
                else out.println("No solutions");
                while (r){
                    r = ruleContext.redo();
                    if(r){
                        if(hasVars || !ruleContext.getVariables().isEmpty())
                        hasVars = printVariables(out, ruleContext, ++solutions);
                    }
                }
                if(solutions>0) out.println("Found "+solutions+ " solutions");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean printVariables(OutputDevice out, RuleContext ruleContext, int number){
        out.print("\n"+ number+") ");
        if (ruleContext.getVariables().isEmpty()){
            out.println("yes\n");
            return false;
        }
        Set<Variable> seen = new HashSet<>();
        for (Variable var : ruleContext.getVariables()) {
            if(seen.contains(var)) continue;
            if(var.isFree()){
                Set<Variable> related = traverseRelated(var, seen);
                if(related.isEmpty())
                    out.print(var.getName()+ "=_");
                else {
                    out.print(var.getName());
                    for (Variable rel : related) {
                        if(var.getRuleContext()==rel.getRuleContext()) {
                            out.print("=" + rel);
                        }
                    }
                }
            }else{
                out.print(var.getName()+"="+var);
            }
            out.print("; ");
        }
        out.print("\n");
        return true;
    }

    private Set<Variable> traverseRelated(Variable start, Set<Variable> seen){
        Stack<Variable> traverse = new Stack<>();
        Set<Variable> related = new HashSet<>();
        traverse.push(start);
        while(!traverse.isEmpty()){
            Variable var = traverse.pop();
            if(!seen.add(var)) continue;
            if(var!=start && var.getRuleContext()==start.getRuleContext()) related.add(var);
            traverse.addAll(var.getRelated());
        }
        return related;
    }
}