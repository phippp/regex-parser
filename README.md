# RegEx Parser

This is part of my final year project for Loughborough University. I am developing a compiler that converts regular expressions with back references to the logic FC.
The plan is to then further optimize the result to make evaluation more effecient.

## Syntax

| Expression | Example |
|--------------|--------|
|Character | `[a-zA-Z]` |
|Number | `[0-9]+` |
|Concatenation| `abc` |
|Plus Operator| `a+`|
|Alternation (OR)| `x \| y` |
|Concatenation | `abc` |
|Non-Capturing group| `(?:re)`|
|Capturing group| `(re)`|
|Back references| `$NUMBER`|

## Restrictions

My version of RegEx is not without limitations on what it can do, it is lacking certain features that can be found in other Regular Expressions. 
These mainly limit the use of back-references to scenarios where the capture group *MUST* exist and cannot be rewritten. 
i.e. the capture group may not be under the plus operator or alternation rule. 
  `(?:(a+))+` `(?:(a)|(b))$1` would both be invalid according to these rules.
