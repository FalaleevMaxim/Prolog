domains
list=foo*
lst=integer*
foo=f(integer);f1(string, integer)
database
db(integer)
db1(string, foo, lst)
database - newdb
db2(lst)
predicates
divides(integer,integer)
allDividers1(integer,integer,list,list,integer)
allDividers(integer,integer,list)
clauses
divides(X,1).
divides(X,X).
divides(X,D):-R1=X mod D,R1=0.
allDividers(X,Y,R):-X>0,Y>0,allDividers1(X,Y,[],R,1).
allDividers1(X,Y,B,R,D):-D>X,R=B.
allDividers1(X,Y,B,R,D):-D>Y,R=B.
allDividers1(X,Y,B,R,D):-D<=X,D<=Y,divides(X,D),divides(Y,D),D1=D+1,allDividers1(X,Y,[D|B],R,D1).
allDividers1(X,Y,B,R,D):-D<=X,D<=Y,D1=D+1,allDividers1(X,Y,B,R,D1).
goal
allDividers(12,60,R),write(R)