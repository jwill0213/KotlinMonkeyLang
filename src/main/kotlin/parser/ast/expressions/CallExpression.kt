package org.example.parser.ast.expressions

import org.example.lexer.Token

class CallExpression(private val token: Token, val fn: Expression) : Expression() {
    var args: List<Expression> = listOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${fn.toString()}(${args.joinToString { it.toString() }})"
    }
}