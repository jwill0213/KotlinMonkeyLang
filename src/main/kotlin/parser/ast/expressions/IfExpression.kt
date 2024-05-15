package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*
import org.example.parser.ast.statements.BlockStatement

class IfExpression(private val token: Token) : Expression() {
    var condition: Expression? = null
    var consequence: BlockStatement? = null
    var alternative: BlockStatement? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        var output = "if${condition.toString()} ${consequence.toString()} "
        if (alternative != null) {
            output += "else ${alternative.toString()}"
        }
        return output
    }

    override fun eval(env: Environment): MonkeyObject {
        val condResult: MonkeyObject = condition?.eval(env) ?: MonkeyNull.NULL
        if (condResult is MonkeyError) {
            return condResult
        }

        return if (MonkeyBool.fromMonkeyObj(condResult) == MonkeyBool.TRUE) {
            consequence?.eval(env) ?: MonkeyNull.NULL
        } else {
            alternative?.eval(env) ?: MonkeyNull.NULL
        }
    }
}