package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression
import org.example.ast.expressions.Identifier

class LetStatement(private val token: Token) : Statement() {
    var name: Identifier? = null
    private var value: Expression? = null

    constructor(token: Token, name: Identifier?, value: Expression?) : this(token) {
        this.name = name
        this.value = value
    }

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()} ${name.toString()} = ${value.toString()};"
    }
}