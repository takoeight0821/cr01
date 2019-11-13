grammar Cr01;

prog : expr (EOF | NEWLINE) ;

expr : '(' expr ')' #parensExpr
     | left=expr op=('*' | '/') right=expr #infixExpr
     | left=expr op=('+' | '-') right=expr #infixExpr
     | value=NUM #numberExpr
     ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';

NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';