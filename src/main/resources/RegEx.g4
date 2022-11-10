grammar RegEx;

regex
    : CHAR                                              #character
    | group                                             #groups
    | main=regex PLUS                                   #plus
    | left=regex right=regex                            #concat
    | left=regex OR right=regex                         #alternation
    | left=regex '$' ref=NUMBER                         #reference
    ;

group
    : '(' inner=regex ')'                               #simpleGroup
    | '(?:' inner=regex ')'                             #nonCapturing
    ;

OR: '|';
PLUS: '+';

NUMBER: [0-9]+;
CHAR: [a-zA-Z] | NUMBER;

WS: [ \t\n\r]+ -> skip;