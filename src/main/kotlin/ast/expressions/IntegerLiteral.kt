package org.example.ast.expressions

import org.example.Token

class IntegerLiteral(private val token: Token) : Expression() {
    var value: Int = token.literal.toInt()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }
}