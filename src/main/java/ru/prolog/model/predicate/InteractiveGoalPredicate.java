package ru.prolog.model.predicate;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.io.OutputDevice;

import java.io.IOException;
import java.util.*;

public class InteractiveGoalPredicate extends GoalPredicate {
    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        OutputDevice out = context.programContext().getOutputDevices();
        //Предикат в бесконечном цикле запраштвает цели и выполняет их.
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new ProgramInterruptedException();
            }
            try {
                //Запрос цели
                out.println("\nWrite goal:");
                String str = context.programContext().getInputDevice().readLine();
                //Проверка, не был ли прерван поток во время ожидания ввода
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    return PredicateResult.FAIL;
                }
                //Если ничего не введено, запросить заново
                if (str == null || str.equals("")) continue;

                //Компиляция цели
                Rule goal = compileGoal(context, str);
                if (goal == null) continue;

                //Создание контекста правила-цели
                RuleContext ruleContext = context.getRuleManager().context(goal, Collections.emptyList(), context);
                boolean r;
                try {
                    //Выполнение правила
                    r = ruleContext.execute();
                } catch (PrologRuntimeException e) {
                    //Вывод ошибок программы
                    context.programContext().getErrorListeners().prologRuntimeException(e);
                    continue;
                } catch (RuntimeException e) {
                    //Вывод ошибок Java
                    context.programContext().getErrorListeners().runtimeException(e);
                    continue;
                }

                int solutions = 0;
                boolean hasVars = false;
                if (r) {
                    //Если правило выполнено успешно, вывести все значения переменных
                    hasVars = printVariables(out, ruleContext, ++solutions);
                } else {
                    out.println("No solutions");
                }
                //Пока правило выполняется успешно, искать новые решения
                while (r) {
                    try {
                        r = ruleContext.redo();
                    } catch (PrologRuntimeException e) {
                        context.programContext().getErrorListeners().prologRuntimeException(e);
                        break;
                    } catch (RuntimeException e) {
                        context.programContext().getErrorListeners().runtimeException(e);
                        break;
                    }
                    if (r) {
                        if (hasVars || !ruleContext.getVariables().isEmpty())
                            hasVars = printVariables(out, ruleContext, ++solutions);
                    }
                }
                if (solutions > 0) out.println("Found " + solutions + " solutions");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Rule compileGoal(PredicateContext context, String str) {
        Collection<CompileException> exceptions = new ArrayList<>();
        Rule goal = PrologCompiler.parseOuterGoal(context.programContext().program(), str, exceptions);
        if (goal == null) {
            for (CompileException e : exceptions) {
                context.programContext().getErrorListeners().println(e.toString());
            }
            return null;
        }

        exceptions.addAll(goal.exceptions());
        if (!exceptions.isEmpty()) {
            for (CompileException e : exceptions) {
                context.programContext().getErrorListeners().println(e.toString());
            }
            return null;
        }
        goal.fix();
        return goal;
    }

    private boolean printVariables(OutputDevice out, RuleContext ruleContext, int number) {
        out.print("\n" + number + ") ");
        if (ruleContext.getVariables().isEmpty()) {
            out.println("yes\n");
            return false;
        }
        Set<Variable> seen = new HashSet<>();
        for (Variable var : ruleContext.getVariables()) {
            if (seen.contains(var)) continue;
            if (var.isFree()) {
                Set<Variable> related = traverseRelated(var, seen);
                if (related.isEmpty())
                    out.print(var.getName() + "=_");
                else {
                    out.print(var.getName());
                    for (Variable rel : related) {
                        if (var.getRuleContext() == rel.getRuleContext()) {
                            out.print("=" + rel);
                        }
                    }
                }
            } else {
                out.print(var.getName() + "=" + var);
            }
            out.print("; ");
        }
        out.print("\n");
        return true;
    }

    private Set<Variable> traverseRelated(Variable start, Set<Variable> seen) {
        Stack<Variable> traverse = new Stack<>();
        Set<Variable> related = new HashSet<>();
        traverse.push(start);
        while (!traverse.isEmpty()) {
            Variable var = traverse.pop();
            if (!seen.add(var)) continue;
            if (var != start && var.getRuleContext() == start.getRuleContext()) related.add(var);
            traverse.addAll(var.getRelated());
        }
        return related;
    }
}