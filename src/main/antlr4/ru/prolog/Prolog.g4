grammar Prolog;

program:domain?
        databases?
        predicates?
        clauses?
        goal?
        ;

domain:DOMAIN typedef*;
typedef:NAME (',' NAME)* '=' type;
type:primitiveType=PRIMITIVE
    |listOf=typeName '*'
    |functorType
    ;
functorType:functor (';' functor)*;
functor:functorName=NAME
       |functorName=NAME '(' argTypes ')'
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
       |NAME '(' argTypes ')'
       ;
argTypes: typeName (',' typeName)*;

clauses:CLAUSES (clause '.')*;
clause:ruleLeft=predExec
      |ruleLeft=predExec ASSIGN ruleBody
      ;
ruleBody:ruleBody (or ruleBody)+
        |stat (and stat)*
        ;
stat:predExec
    |cut
    |equality
    |compare
    |not
    ;
predExec:NAME
        |NAME '(' argList ')'
        ;
argList:value (',' value)*;
equality:left=eqVal operator='=' right=eqVal;
eqVal:value
      |expr
      ;
expr:negative
    |left=expr operator=('*'|'/'|'div'|'mod') right=expr
    |left=expr operator=('+'|'-') right=expr
    |'(' expr ')'
    |NAME '(' expr ')'
    |real
    |integer
    |VARNAME
    ;
negative:'-' '(' expr ')'
        | '-' VARNAME
        ;
compare:left=eqVal operator=('>'|'<'|'<='|'>=') right=eqVal;
not:'not' '(' predExec ')';
value:VARNAME
     |real
     |integer
     |STRING
     |symbol=NAME
     |list
     |struct
     ;
real:minus='-'? REAL;
integer:minus='-'? INTEGER;
list: '[' rb=']'
    | '[' listValues ('|' tail=VARNAME)? ']'
    ;
listValues:value (',' value)*;
struct:NAME '(' value (',' value)* ')';

goal:GOAL ruleBody;

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

NAME:  LOWER NAMECHAR*;
VARNAME:UPPER NAMECHAR*
       |'_'
       ;

REAL:INT '.' INT;
INTEGER:INT
       |'$' HEX_INT
       ;
STRING:'"' (ESC|~'"')* '"';

LSQBR:'[';
RSQBR:']';
LPAR :'(';
RPAR :')';

COMMENT: '/*' ('/'*? COMMENT | ('/'* | '*'*) ~[/*])*? '*'*? '*/' -> channel(HIDDEN);
LINE_COMMENT:'//' ~[\r\n]* -> channel(HIDDEN);

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
            |'\\t'
            |'\\r'
            |'\\n'
            |'\\u' HEX HEX HEX HEX
            ;