package ru.prolog.model.values.variables;

import org.junit.Test;
import ru.prolog.model.Type;
import ru.prolog.model.values.PrologList;
import ru.prolog.model.values.ListValue;
import ru.prolog.model.values.SimpleValue;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListVariableTest {
    private Type integerType = Type.getType("integer");
    private Type intListType = Type.listType("intList", integerType);

    @Test
    //X=[1,Y,3|T], T=[4], Y=2, write(X).
    public void unify() throws Exception {
        SimpleVariable y = new SimpleVariable(integerType);
        ListVariable t = new ListVariable(intListType);
        PrologList list = PrologList.asList(t, new SimpleValue(integerType, 1), y, new SimpleValue(integerType, 3) );
        ListVariable x = new ListVariable(intListType);
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