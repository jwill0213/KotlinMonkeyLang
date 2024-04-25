package org.example.ast.expressions

import org.example.Token

class Identifier(private val token: Token) : Expression() {
    override fun getTokenLiteral(): String {
        return token.literal
    }
}