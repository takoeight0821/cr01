grammar Cr01;

prog : expr (EOF | NEWLINE) ;

expr : 'let' name=ID '=' value=expr 'in' body=expr #letExpr
     | '(' expr ')' #parensExpr
     | left=expr op=('*' | '/') right=expr #infixExpr
     | left=expr op=('+' | '-') right=expr #infixExpr
     | value=ID #varExpr
     | value=NUM #numberExpr
     ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';

ID: [a-z][a-zA-Z0-9]*;
NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';