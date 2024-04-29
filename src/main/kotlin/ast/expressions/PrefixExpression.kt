package org.example.ast.expressions

import org.example.Token

class PrefixExpression(private val token: Token) : Expression() {
    var operator: String = getTokenLiteral()
    var right: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "($operator${right.toString()})"
    }
}