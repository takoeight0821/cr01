grammar Cr01;

@header {
package language.parser;
}

prog : (funDecl ';')+ (EOF | NEWLINE) ;

funDecl : name=ID (params+=ID)+ '=' expr ;

expr : '(' expr ')' #parensExpr
     | left=expr op=('*' | '/') right=expr #infixExpr
     | left=expr op=('+' | '-') right=expr #infixExpr
     | name=ID #varExpr
     | value=NUM #numberExpr
     | 'let' var_decl 'in' body=expr #letExpr
     ;

var_decl : name=ID '=' value=expr #simpleDecl
     ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';

ID: [a-z][a-zA-Z0-9]*;
NUM: [0-9]+;
WS: [ \t]+ -> skip;
NEWLINE: '\r'? '\n';