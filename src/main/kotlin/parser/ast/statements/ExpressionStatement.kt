package org.example.parser.ast.statements

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyObject
import org.example.parser.ast.expressions.Expression

class ExpressionStatement(private val token: Token) : Statement() {
    var expression: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return expression?.toString() ?: ""
    }

    override fun eval(env: Environment): MonkeyObject? {
        return expression?.eval(env)
    }
}