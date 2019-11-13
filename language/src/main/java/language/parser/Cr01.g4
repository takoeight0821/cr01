grammar Cr01;

prog : expr (EOF | NEWLINE) ;

expr : '(' expr ')' #parensExpr
     | left=expr op=(OP_ADD | OP_SUB) right=expr #infixExpr
     | value=NUM #numberExpr
     ;

OP_ADD: '+';
OP_SUB: '-';

NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';