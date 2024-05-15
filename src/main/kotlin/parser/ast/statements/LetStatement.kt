package org.example.parser.ast.statements

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyError
import org.example.`object`.MonkeyObject
import org.example.parser.ast.expressions.Expression
import org.example.parser.ast.expressions.Identifier

class LetStatement(private val token: Token) : Statement() {
    var name: Identifier = Identifier(token)
    var value: Expression? = null

    constructor(token: Token, name: Identifier, value: Expression?) : this(token) {
        this.name = name
        this.value = value
    }

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()} ${name.toString()} = ${value.toString()};"
    }

    override fun eval(env: Environment): MonkeyObject? {
        val result = value?.eval(env) ?: MonkeyError("No expression for let statement")
        if (result is MonkeyError) {
            return result
        }

        // Add variable to the global environment
        env.set(name.getTokenLiteral(), result)

        return super.eval(env)
    }
}