package org.example.ast.expressions

import org.example.TokenType

enum class Precedence(val rank: Int) {
    LOWEST(1),
    EQUALS(2),
    LESSGREATER(3),
    SUM(4),
    PRODUCT(5),
    PREFIX(6),
    CALL(7);

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
                else -> null
            }
        }
    }
}