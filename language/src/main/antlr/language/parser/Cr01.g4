grammar Cr01;

@header {
package language.parser;
}

prog : (funDecl ';')+ EOF ;

funDecl : name=ID (params+=ID)+ '=' expr ;

simpleExpr : '(' expr ')' #parensExpr
           | name=ID #varExpr
           | value=NUM #numberExpr
           ;

expr : func=simpleExpr (args+=simpleExpr)* #applyExpr
     | left=expr op=('*' | '/') right=expr #infixExpr
     | left=expr op=('+' | '-') right=expr #infixExpr
     | 'let' var_decl 'in' body=expr #letExpr
     ;

var_decl : name=ID '=' value=expr #simpleDecl ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';

ID: [a-z_][a-zA-Z0-9_]*;
NUM: [0-9]+;
WS: [ \t\r\n]+ -> skip;
NEWLINE: '\r'? '\n';