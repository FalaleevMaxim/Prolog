package ru.prolog.std;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ru.prolog.PrologBaseListener;
import ru.prolog.PrologLexer;
import ru.prolog.PrologParser;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.GoalPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.values.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InteractiveGoalPredicate extends GoalPredicate{
    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        System.out.println("Print goal and get list of variables");
        try {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            PrologLexer lexer = new PrologLexer(CharStreams.fromString(str));
            BufferedTokenStream tokenStream = new BufferedTokenStream(lexer);
            PrologParser parser = new PrologParser(tokenStream);
            PrologParser.RuleBodyContext body = parser.ruleBody();
            ParseTreeWalker walker = new ParseTreeWalker();
            VarNamesListener listener = new VarNamesListener();
            walker.walk(listener, body);
            System.out.println(listener.varnames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

class VarNamesListener extends PrologBaseListener{
    final Set<String> varnames = new HashSet<>();

    @Override
    public void enterValue(PrologParser.ValueContext ctx) {
        if(ctx.VARNAME()!=null)
            varnames.add(ctx.VARNAME().toString());
    }

    @Override
    public void enterList(PrologParser.ListContext ctx) {
        if(ctx.tail!=null)
            varnames.add(ctx.tail.getText());
    }
}