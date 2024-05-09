package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.parser.ast.statements.BlockStatement

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