package org.example.ast.expressions

import org.example.Token

class CallExpression(private val token: Token, val fn: Expression) : Expression() {
    var args: List<Expression> = listOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${fn.toString()}(${args.joinToString { it.toString() }})"
    }
}