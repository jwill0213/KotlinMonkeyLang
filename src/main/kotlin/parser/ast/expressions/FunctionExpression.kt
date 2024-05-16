package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyFunction
import org.example.`object`.MonkeyObject
import org.example.parser.ast.statements.BlockStatement

class FunctionExpression(private val token: Token) : Expression() {
    var params: List<Identifier> = mutableListOf()
    lateinit var body: BlockStatement

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()}(${params.joinToString { it.toString() }}) ${body.toString()}"
    }

    override fun eval(env: Environment): MonkeyObject {
        return MonkeyFunction(params, body, env)
    }
}