grammar Cr01;

@header {
package language.parser;
}

prog : expr (EOF | NEWLINE) ;

expr : '(' expr ')' #parensExpr
     | left=expr op=('*' | '/') right=expr #infixExpr
     | left=expr op=('+' | '-') right=expr #infixExpr
     | name=ID #varExpr
     | value=NUM #numberExpr
     | 'let' decl 'in' body=expr #letExpr
     ;

decl : name=ID '=' value=expr #simpleDecl
     ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';

ID: [a-z][a-zA-Z0-9]*;
NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';