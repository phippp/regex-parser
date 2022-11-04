grammar RegEx;

exp
    : (start='/^')? inner=regex (end='/$')?             #anchors
    ;

regex
    : CHAR                                              #character
    | ANY                                               #dot
    | group                                             #groups
    | regex KLEENE                                      #kleene
    | regex PLUS                                        #plus
    | left=regex right=regex                            #concat
    | left=regex OR right=regex                         #alternation
    | regex '$' ref=NUMBER                              #reference
    ;

group
    : '(' inner=regex ')'                               #simpleGroup
    | '(?:' inner=regex ')'                             #nonCapturing
    ;

ANY: '-';
OR: '|';
PLUS: '+';
KLEENE: '*';

NUMBER: [0-9]+;
CHAR: [a-zA-Z] | NUMBER;

WS: [ \t\n\r]+ -> skip;