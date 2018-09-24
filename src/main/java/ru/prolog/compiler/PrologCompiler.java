package ru.prolog.compiler;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ru.prolog.PrologLexer;
import ru.prolog.PrologParser;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.runtime.context.predicate.DebuggerPredicateContextDecorator;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.DebuggerRuleContextDecorator;
import ru.prolog.util.io.LogFileOutput;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrologCompiler {
    private String filePath;
    private String debugFileName;
    private List<CompileException> exceptions = new ArrayList<>();

    public PrologCompiler(String filePath) {
        this.filePath = filePath;
    }

    public PrologCompiler(String filePath, String debugFileName) {
        this.filePath = filePath;
        this.debugFileName = debugFileName;
    }

    public Program compileProgram() throws IOException {
        exceptions.clear();
        CharStream input = CharStreams.fromFileName(filePath);
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        PrologParseListener compiler = new PrologParseListener();
        parser.removeErrorListeners();
        parser.addErrorListener(compiler);
        lexer.removeErrorListeners();
        lexer.addErrorListener(compiler);

        PrologParser.ProgramContext parseTree = parser.program();
        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
            return null;
        }

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(compiler, parseTree);
        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
            return null;
        }

        if(debugFileName!=null){
            compiler.getProgram().managers().getRuleManager().addOption(DebuggerRuleContextDecorator::new);
            compiler.getProgram().managers().getPredicateManager().addOption(DebuggerPredicateContextDecorator::new);
            compiler.getProgram().managers().getProgramManager()
                    .addOption(context->{
                        //Clear debug file
                        try (PrintWriter pw = new PrintWriter(debugFileName)) {
                            pw.print("");
                        } catch (FileNotFoundException ignored) { }
                        //Put file name to context data
                        context.putContextData(ProgramContext.KEY_DEBUG_FILE, debugFileName);
                        //Write all output and exceptions to debug file
                        LogFileOutput fileWriter = new LogFileOutput(debugFileName);
                        context.getErrorListeners().add(fileWriter);
                        context.getOutputDevices().add(fileWriter);
                        return context;
                    });
        }


        return compiler.getProgram();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDebugFileName() {
        return debugFileName;
    }

    public Collection<CompileException> getExceptions() {
        return Collections.unmodifiableList(exceptions);
    }

    public static StatementExecutorRule parseOuterGoal(Program program, String goal, Collection<CompileException> exceptions){
        return PrologParseListener.parseOuterGoal(program, goal, exceptions);
    }

    public static List<FactRule> consult(Program program, String dbFile, Collection<CompileException> exceptions) throws IOException {
        return PrologParseListener.parseDbFile(program, dbFile, exceptions);
    }
}