grammar RegEx;

exp
    : '/^'? inner=regex '/$'?           #anchors
    ;

regex
    : CHAR                              #character
    | ANY                               #dot
    | group                             #groups
    | regex KLEENE                      #kleene
    | regex PLUS                        #plus
    | left=regex right=regex            #concat
    | left=regex OR right=regex         #alternation
    | regex '$' NUMBER                  #reference
    ;

group
    : '(' inner=regex ')'               #simple_group
    | '(?:' inner=regex ')'             #non_capturing
    ;

ANY: '-';
OR: '|';
PLUS: '+';
KLEENE: '*';

NUMBER: [0-9]+;
CHAR: [a-zA-Z] | NUMBER;

WS: [ \t\n\r]+ -> skip;