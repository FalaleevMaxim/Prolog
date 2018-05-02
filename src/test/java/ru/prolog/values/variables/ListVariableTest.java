package ru.prolog.values.variables;

import org.junit.Test;
import ru.prolog.model.type.Type;
import ru.prolog.values.simple.SimpleVariable;
import ru.prolog.values.list.PrologList;
import ru.prolog.values.list.ListValue;
import ru.prolog.values.simple.SimpleValue;
import ru.prolog.values.list.ListVariableImpl;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListVariableTest {
    private Type integerType = Type.primitives.get("integer");
    private Type intListType = new Type(integerType, "integer");

    @Test
    //X=[1,Y,3|T], T=[4], Y=2, write(X).
    public void unify() throws Exception {
        SimpleVariable y = new SimpleVariable(integerType, "Y", null);
        ListVariableImpl t = new ListVariableImpl(intListType, "T", null);
        PrologList list = PrologList.asList(t, new SimpleValue(integerType, 1), y, new SimpleValue(integerType, 3) );
        ListVariableImpl x = new ListVariableImpl(intListType, "X", null);
        x.unify(list);
        t.unify(new ListValue(intListType, new SimpleValue(integerType, 4)));
        y.unify(new SimpleValue(integerType, 2));
        assertList(x, Arrays.asList(1,2,3,4));
    }

    private static void assertList(PrologList list, java.util.List values){
        if(values.size() == 0){
            assertEquals(null, list.head());
        }else if(list.isEmpty()){
            fail("PrologList is shorter than expected");
        }else{
            assertEquals(list.head().getValue(), values.get(0));
            assertList(list.tail(), values.subList(1, values.size()));
        }
    }
}