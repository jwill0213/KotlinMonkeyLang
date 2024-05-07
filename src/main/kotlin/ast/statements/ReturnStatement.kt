package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression

class ReturnStatement(private val token: Token) : Statement() {
    var value: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()} ${value.toString()};"
    }
}