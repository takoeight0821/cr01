grammar Cr01;

@header {
package language.parser;
}

prog : (funDecl ';')+ EOF ;

funDecl : name=ID (params+=ID)+ '=' expr ;

simpleExpr : '(' expr ')' #parensExpr
           | 'true' #trueExpr
           | 'false' #falseExpr
           | name=ID #varExpr
           | value=NUM #numberExpr
           ;

expr : func=simpleExpr (args+=simpleExpr)* #applyExpr
     | expr op=('*' | '/') expr #infixExpr
     | expr op=('+' | '-') expr #infixExpr
     | expr op=('==' | '!=') expr #infixExpr
     | 'if' expr 'then' expr 'else' expr #ifExpr
     | 'fn' (params+=ID)+ '->' body=expr #fnExpr
     | 'let' var_decl 'in' body=expr #letExpr
     ;

var_decl : name=ID '=' value=expr #simpleDecl ;

OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';
OP_EQ: '==';
OP_NE: '!=';

ID: [a-z_][a-zA-Z0-9_]*;
NUM: [0-9]+;
WS: [ \t\r\n]+ -> skip;
NEWLINE: '\r'? '\n';