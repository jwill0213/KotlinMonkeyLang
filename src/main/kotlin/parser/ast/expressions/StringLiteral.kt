package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyObject
import org.example.`object`.MonkeyString

class StringLiteral(private val token: Token) : Expression() {
    val value: String = token.literal

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }

    override fun eval(env: Environment): MonkeyObject {
        return MonkeyString(value)
    }
}