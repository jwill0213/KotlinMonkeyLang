package org.example.ast.expressions

import org.example.Token

class InfixExpression(private val token: Token, var left: Expression? = null) : Expression() {
    var operator: String = getTokenLiteral()
    var right: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "(${left.toString()} $operator ${right.toString()})"
    }
}