package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression

class ReturnStatement(private val token: Token) : Statement() {
    private var value: Expression? = null

    constructor(token: Token, value: Expression?) : this(token) {
        this.value = value
    }

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()} ${value.toString()};"
    }
}