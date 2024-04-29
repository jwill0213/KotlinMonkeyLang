package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression

class ExpressionStatement(private val token: Token) : Statement() {
    var expression: Expression? = null

    constructor(token: Token, value: Expression?) : this(token) {
        this.expression = value
    }

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return expression?.toString() ?: ""
    }
}