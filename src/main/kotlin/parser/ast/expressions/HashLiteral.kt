package org.example.parser.ast.expressions

import org.example.lexer.Token

class HashLiteral(private val token: Token) : Expression() {
    var expressionMap: MutableMap<Expression, Expression> = mutableMapOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        val pairs = expressionMap.entries.map { "${it.key}:${it.value}" }
        return "{${pairs.joinToString { it }}}"
    }
}