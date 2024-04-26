package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression
import org.example.ast.expressions.Identifier

class LetStatement(private val token: Token) : Statement() {
    var name: Identifier? = null
    val value: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }
}