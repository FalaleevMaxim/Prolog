package ru.prolog.logic.context.rule.statements;

import java.util.ArrayList;
import java.util.List;

public class ExecutedStatements {
    public List<ExecutedStatement> executions = new ArrayList<>();
    public int cutIndex = -1;
    public int currentStatement=0;
    public int currentList = 0;
}
