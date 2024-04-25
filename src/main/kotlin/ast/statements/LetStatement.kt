package org.example.ast.statements

import org.example.Token
import org.example.ast.expressions.Expression
import org.example.ast.expressions.Identifier

class LetStatement(private val token: Token, val name: Identifier, val value: Expression) : Statement() {
    override fun getTokenLiteral(): String {
        return token.literal
    }
}