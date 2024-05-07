package org.example.ast.expressions

import org.example.Token
import org.example.ast.statements.BlockStatement

class FunctionExpression(private val token: Token) : Expression() {
    var params: List<Identifier> = mutableListOf()
    var body: BlockStatement? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()}(${params.joinToString { it.toString() }}) ${body.toString()}"
    }
}