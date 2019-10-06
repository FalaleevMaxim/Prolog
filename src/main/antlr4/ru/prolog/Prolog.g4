grammar Prolog;

program:includes?
        domain?
        databases?
        predicates?
        clauses?
        goal?
        ;
includes: INCLUDE include*;
include: (MODULE | PREDICATE) LPAR directory=STRING ',' className=STRING RPAR;

domain:DOMAIN typedef*;
typedef:NAME (',' NAME)* '=' type;
type:primitiveType=PRIMITIVE
    |listOf=typeName '*'
    |compoundType
    ;
compoundType:functor (';' functor)*;
functor:functorName=NAME
       |functorName=NAME LPAR argTypes RPAR
       ;
typeName:NAME
        |PRIMITIVE
        ;
databases:database+;
database:DATABASE predDef+
        |DATABASE '-' NAME predDef+
        ;
predicates:PREDICATES predDef*;
predDef:NAME
       |NAME LPAR argTypes? RPAR
       ;
argTypes: typeName (',' typeName)*;

clauses:CLAUSES (clause '.')*;
clause:ruleLeft=predExec
      |ruleLeft=predExec ASSIGN ruleBody
      ;
ruleBody:ruleBody or ruleBody
        |stat (and stat)*
        ;
stat:predExec
    |cut
    |compare
    |not
    ;
predExec:NAME
        |NAME LPAR argList? RPAR
        ;
argList:value (',' value)*;
compare:left=compVal operator=('='|'<>'|'>'|'<'|'<='|'>=') right=compVal;
compVal:value
     |expr
     ;
expr:negative
    |left=expr operator=('*'|'/'|'div'|'mod') right=expr
    |left=expr operator=('+'|'-') right=expr
    |LPAR inner=expr RPAR
    |funcExpr
    |real
    |integer
    |VARNAME
    ;
negative:minus='-' LPAR expr RPAR
        |minus='-' VARNAME
        |minus='-' funcExpr
        ;
funcExpr:FUNCTION LPAR expr RPAR;
not:'not' LPAR predExec RPAR;
value:VARNAME
     |real
     |integer
     |STRING
     |CHAR
     |symbol=NAME
     |list
     |functorVal
     ;
real:minus='-'? REAL;
integer:minus='-'? INTEGER;
list: LSQ RSQ
    | LSQ listValues ('|' tail=VARNAME)? RSQ
    ;
listValues:value (',' value)*;
functorVal:NAME LPAR argList? RPAR
          ;
goal:GOAL ruleBody '.'?;

outerGoal: ruleBody '.'?;

consult: predExec+;

and:','
   |'and'
   |'AND'
   ;
or :';'
   |'or'
   |'OR'
   ;
cut:'!'
   |'cut'
   ;
FUNCTION:'sin'
        |'cos'
        |'tan'
        |'abs'
        ;

INCLUDE   :'include';
MODULE    :'module';
PREDICATE :'predicate';
DOMAIN    :'domains';
DATABASE  :'database';
PREDICATES:'predicates';
CLAUSES   :'clauses';
GOAL      :'goal';

PRIMITIVE:'integer'
         |'real'
         |'char'
         |'string'
         |'symbol'
         ;

ASSIGN:':-'
      |'if'
      |'IF'
      ;

NAME:LOWER NAMECHAR*
    |'_' NAMECHAR+;
VARNAME:UPPER NAMECHAR*
       |'_'
       ;

REAL:INT '.' INT;
INTEGER:INT
       |'$' HEX_INT
       ;
STRING:'"' (ESC|~["\\])* '"';
CHAR: '\'' (ESC|~'\'') '\'';

LSQ:'[';
RSQ:']';
LPAR :'(';
RPAR :')';

COMMENT: '/*' ('/'*? COMMENT | ('/'* | '*'*) ~[/*])*? '*'*? '*/' -> skip;
LINE_COMMENT:'%' ~[\r\n]* -> skip;

WS:[ \t]->skip;
NL:'\r'?'\n'->skip;

fragment LOWER:'a'..'z'|'\u0430'..'\u044F'|'\u0451';
fragment UPPER:'A'..'Z'|'\u0410'..'\u042F'|'\u0401';
fragment DIGIT:'0'..'9';
fragment NAMECHAR: (LOWER|UPPER|DIGIT|'_');
fragment INT:DIGIT+;
fragment HEX_INT:HEX+;
fragment HEX:DIGIT
            |'A'..'F'
            |'a'..'f'
            ;
fragment ESC:'\\"'
            |'\\\\'
            |'\\t'
            |'\\r'
            |'\\n'
            |'\\u' HEX HEX HEX HEX
            ;