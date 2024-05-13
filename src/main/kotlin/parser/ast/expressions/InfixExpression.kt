package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.MonkeyInt
import org.example.`object`.MonkeyNull
import org.example.`object`.MonkeyObject

class InfixExpression(private val token: Token, var left: Expression? = null) : Expression() {
    var operator: String = getTokenLiteral()
    var right: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "(${left.toString()} $operator ${right.toString()})"
    }

    override fun eval(): MonkeyObject {
        val left = left?.eval() ?: MonkeyNull.NULL
        val right = right?.eval() ?: MonkeyNull.NULL

        if (left is MonkeyInt && right is MonkeyInt) {
            return evalIntInfixExpression(left, right)
        }

        return MonkeyNull.NULL
    }

    private fun evalIntInfixExpression(left: MonkeyInt, right: MonkeyInt): MonkeyObject {
        return when (operator) {
            "+" -> MonkeyInt(left.value + right.value)
            "-" -> MonkeyInt(left.value - right.value)
            "*" -> MonkeyInt(left.value * right.value)
            "/" -> MonkeyInt(left.value / right.value)
            else -> MonkeyNull.NULL
        }
    }
}