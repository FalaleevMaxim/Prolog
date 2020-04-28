%include
%predicate("/home/mfalaleev/IdeaProjects/Prolog/target/test-classes/", "ru.prolog.logic.model.predicate.HelloPredicate")
%predicate("/home/mfalaleev/IdeaProjects/Prolog/target/test-classes/", "ru.prolog.logic.model.predicate.Return7Predicate")
%module("/home/mfalaleev/IdeaProjects/Prolog/target/test-classes/", "ru.prolog.logic.model.predicate.IntegersListPredicateModule")

domains
имя, фамилия = string
человек = имя(имя); чел(имя, фамилия)
lst=integer*
database
	человек(человек)
	flag
predicates
    %test functors
	однофамилец(человек, человек)
	тёзка(человек, человек)
	имя(человек, имя)

	%Test parsing char as integer or char
	p(integer)
	p1(char)

	%Sum numbers from 0 to first arg. Second arg is sum. Test math operations
	sum(integer, integer)
	%equal lists
	eq(lst, lst)
	%Concatenate lists
	conc(lst,lst,lst)

	%bubble sort
	bubble(lst,lst)
	swap(lst,lst)

	int(integer)
clauses
    однофамилец(чел(_, F), чел(_, F)).
	человек(чел("Иван", "Иванов")).
	человек(чел("Иван", "Фёдоров")).
	человек(чел("Дмитрий", "Иванов")).
	человек(чел("Александр\n", "Фёдоров")).
	человек(чел("Алексей", "Иванов")).
	тёзка(X, Y):-имя(X, N), имя(Y, N).
	имя(чел(N, _), N).
	имя(имя(N), N).
	p('\u0030').
	p1('\u0030').
	sum(0,0):-!.
	sum(X,R):-X1=X-1, sum(X1,R1), R=R1+X.
	eq(X,X).
	conc([],L,L).
	conc([H|L1],L2,[H|R]):-conc(L1,L2,R).
    swap([X,Y|Rest], [Y,X|Rest]):-X>Y,!.
    swap ( [ Z | Rest ], [ Z | Rest1 ] ) :- swap (Rest, Rest1 ).
	bubble ( List, SortedList) :-
        swap ( List, List1 ), ! ,
        bubble ( List1, SortedList) .
    bubble ( List, List).

    int(_).