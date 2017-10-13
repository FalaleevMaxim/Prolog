grammar Prolog;

program:domain?
        predicates?
        clauses?
        goal?
        ;

domain:DOMAIN typedef*;
typedef:NAME (',' NAME)* '=' type;
type:typeName=NAME
    |listOf=NAME '*'
    |structType
    ;
structType:NAME '(' type (',' type)*')';

predicates:PREDICATES predDef*;
predDef:NAME
       |NAME '(' argTypes ')'
       ;
argTypes: NAME (',' NAME)*;

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
    ;
predExec:NAME
        |NAME '(' argList ')'
        ;
argList:value (',' value)*;
equality:left=eqVal operator='=' right=eqVal;
eqVal:value
      |expr
      ;
expr:left=expr operator=('*'|'/'|'div'|'mod') right=expr
    |left=expr operator=('+'|'-') right=expr
    |'(' expr ')'
    |real
    |integer
    |VARNAME
    |negative
    ;
negative:'-' '(' expr ')'
        | '-' VARNAME
        ;
compare:left=eqVal operator=('>'|'<'|'<='|'>=') right=eqVal;
value:VARNAME
     |real
     |integer
     |STRING
     |symbol=NAME
     |list
     |struct
     ;
real:'-'? REAL;
integer:'-'? INTEGER;
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
PREDICATES:'predicates';
CLAUSES   :'clauses';
GOAL      :'goal';

ASSIGN:':-'
      |'if'
      |'IF'
      ;

NAME:  LOWER NAMECHAR*;
VARNAME:UPPER NAMECHAR*
       |'_'
       ;

REAL:INT '.' INT;
INTEGER:INT;
STRING:'"' (ESC|~'"')* '"';

LSQBR:'[';
RSQBR:']';
LPAR :'(';
RPAR :')';

COMMENT: '/*' ('/'*? COMMENT | ('/'* | '*'*) ~[/*])*? '*'*? '*/' -> channel(HIDDEN);
LINE_COMMENT:'//' ~[\r\n]* -> channel(HIDDEN);

WS:[ \t]->skip;
NL:'\r'?'\n'->skip;

fragment LOWER:'a'..'z';
fragment UPPER:'A'..'Z';
fragment DIGIT:'0'..'9';
fragment NAMECHAR: (LOWER|UPPER|DIGIT|'_');
fragment INT:DIGIT+;
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