grammar Cr01;

prog : expr (EOF | NEWLINE) ;

expr : '(' expr ')'                   #parensExpr
     | left=expr op=OP_ADD right=expr #infixExpr
     | value=NUM                      #numberExpr
     ;

OP_ADD: '+';

NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';