package org.example.parser.ast.expressions

import org.example.lexer.Token

class Identifier(private val token: Token) : Expression() {
    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return token.literal
    }
}