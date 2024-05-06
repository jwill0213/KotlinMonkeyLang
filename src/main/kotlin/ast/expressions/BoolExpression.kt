package org.example.ast.expressions

import org.example.Token
import org.example.TokenType

class BoolExpression(private val token: Token) : Expression() {
    var value: Boolean = token.tokenType == TokenType.TRUE

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }
}