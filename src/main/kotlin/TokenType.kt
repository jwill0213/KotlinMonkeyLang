package org.example

data class Token(val tokenType: TokenType, val literal: String)

enum class TokenType(val token: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // Identifiers + literals
    IDENT("IDENT"),
    INT("INT"),

    // Operators
    ASSIGN("="),
    PLUS("+"),

    // Delimiters
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("LET")
}