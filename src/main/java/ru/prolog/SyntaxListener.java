package ru.prolog;

public class SyntaxListener extends PrologBaseListener {

    @Override
    public void enterClauses(PrologParser.ClausesContext ctx) {
        System.out.println("Clauses:");
    }

    @Override
    public void enterClause(PrologParser.ClauseContext ctx) {
        System.out.println(ctx.ruleLeft.getText());
    }

    @Override
    public void enterList(PrologParser.ListContext ctx) {

    }

}
