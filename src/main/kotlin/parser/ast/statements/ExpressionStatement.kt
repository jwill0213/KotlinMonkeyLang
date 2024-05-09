package org.example.parser.ast.statements

import org.example.lexer.Token
import org.example.`object`.MonkeyObject
import org.example.parser.ast.expressions.Expression

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

    override fun eval(): MonkeyObject? {
        return expression?.eval()
    }
}