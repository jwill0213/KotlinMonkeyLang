package org.example.parser.ast.expressions

import org.example.lexer.TokenType

enum class Precedence(val rank: Int) {
    LOWEST(1),
    EQUALS(2),
    LESSGREATER(3),
    SUM(4),
    PRODUCT(5),
    PREFIX(6),
    CALL(7),
    INDEX(8);

    companion object {
        fun findPrecedence(tokenType: TokenType): Precedence? {
            return when (tokenType) {
                TokenType.EQ -> EQUALS
                TokenType.NOT_EQ -> EQUALS
                TokenType.LT -> LESSGREATER
                TokenType.GT -> LESSGREATER
                TokenType.PLUS -> SUM
                TokenType.MINUS -> SUM
                TokenType.SLASH -> PRODUCT
                TokenType.ASTERISK -> PRODUCT
                TokenType.LPAREN -> CALL
                TokenType.LBRACKET -> INDEX
                else -> null
            }
        }
    }
}