package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyInt
import org.example.`object`.MonkeyObject

class IntegerLiteral(private val token: Token) : Expression() {
    val value: Int = token.literal.toInt()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }

    override fun eval(env: Environment): MonkeyObject {
        return MonkeyInt(value)
    }
}