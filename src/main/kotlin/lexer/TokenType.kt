package org.example.lexer

enum class TokenType(val token: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // Identifiers + literals
    IDENT("IDENT"),
    INT("INT"),
    STRING("STRING"),

    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),

    LT("<"),
    GT(">"),

    EQ("=="),
    NOT_EQ("!="),

    // Delimiters
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),

    // Keywords
    FUNCTION("fn"),
    LET("let"),
    TRUE("true"),
    FALSE("false"),
    IF("if"),
    ELSE("else"),
    RETURN("return");

    companion object {
        fun findTokenType(literal: String): TokenType {
            return when (literal) {
                "fn" -> FUNCTION
                "let" -> LET
                "true" -> TRUE
                "false" -> FALSE
                "if" -> IF
                "else" -> ELSE
                "return" -> RETURN
                else -> IDENT
            }
        }
    }
}