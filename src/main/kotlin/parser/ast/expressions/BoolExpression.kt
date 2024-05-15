package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.lexer.TokenType
import org.example.`object`.Environment
import org.example.`object`.MonkeyBool
import org.example.`object`.MonkeyObject

class BoolExpression(private val token: Token) : Expression() {
    var value: Boolean = token.tokenType == TokenType.TRUE

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }

    override fun eval(env: Environment): MonkeyObject {
        return if (value) MonkeyBool.TRUE else MonkeyBool.FALSE
    }
}