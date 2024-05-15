package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyError
import org.example.`object`.MonkeyObject

class Identifier(private val token: Token) : Expression() {
    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }

    override fun eval(): MonkeyObject? {
        val envVal = Environment.get(getTokenLiteral())

        // envVal.second is false when identifier is unset
        if (!envVal.second) {
            return MonkeyError("identifier not found: ${getTokenLiteral()}")
        }

        return envVal.first
    }
}