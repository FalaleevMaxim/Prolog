package ru.prolog;

import ru.prolog.model.Type;
import ru.prolog.model.values.PrologList;
import ru.prolog.model.values.SimpleValue;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.SimpleVariable;
import ru.prolog.model.values.variables.Variable;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        /*CharStream input = CharStreams.fromFileName("C:\\Users\\Admin\\IdeaProjects\\AntlrTest\\src\\main\\resources\\sampleCode.pl");
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        ParseTree parseTree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new SyntaxListener(), parseTree);*/

        Type integerType = Type.getType("integer");
        //X=1, write(X).
        {
            Variable x = new SimpleVariable(integerType);
            x.unify(new SimpleValue(integerType, 1));
            System.out.println(x.getValue());
        }

        //X=1, X=Y, write(Y).
        {
            Variable x = new SimpleVariable(integerType);
            x.unify(new SimpleValue(integerType, 1));
            Variable y = new SimpleVariable(integerType);
            x.unify(y);
            System.out.println(y.getValue());
        }

        //X=Y, X=1, write(Y)
        {
            Variable x = new SimpleVariable(integerType);
            Variable y = new SimpleVariable(integerType);
            x.unify(y);
            x.unify(new SimpleValue(integerType, 1));
            System.out.println(y.getValue());
        }

        //X=Y, Y=Z, X=1, write(Z)
        {
            Variable x = new SimpleVariable(integerType);
            Variable y = new SimpleVariable(integerType);
            x.unify(y);
            Variable z = new SimpleVariable(integerType);
            y.unify(z);
            x.unify(new SimpleValue(integerType, 1));
            System.out.println(z.getValue());
        }

        // p(X,Z), X=1, write(Z).
        // p(Y,Y).
        {
            Variable x = new SimpleVariable(integerType);
            Variable z = new SimpleVariable(integerType);
            Variable y = new SimpleVariable(integerType);
            x.unify(y);
            z.unify(y);
            y.dismiss();
            x.unify(new SimpleValue(integerType, 1));
            System.out.println(z.getValue());
        }

        Type intListType = Type.listType("intList", integerType);
        //write([1,2,3,4,5]).
        {
            Integer[] intValues = new Integer[]{1,2,3,4,5};
            Value[] values = new Value[intValues.length];
            for (int i = 0; i < intValues.length; i++) {
                values[i] = new SimpleValue(integerType, intValues[i]);
            }
            printList(PrologList.asList(intListType, values));
        }
    }

    private static void printList(PrologList list){
        System.out.print('[');
        while (!list.isEmpty()){
            System.out.print(list.head().getValue());
            list = list.tail();
            if(!list.isEmpty())
                System.out.print(',');
        }
        System.out.println(']');
    }
}