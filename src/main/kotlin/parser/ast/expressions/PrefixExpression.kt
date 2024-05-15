package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*

class PrefixExpression(private val token: Token) : Expression() {
    var operator: String = getTokenLiteral()
    var right: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "($operator${right.toString()})"
    }

    override fun eval(): MonkeyObject {
        val rightEval = right?.eval() ?: MonkeyNull.NULL
        if (rightEval is MonkeyError) {
            return rightEval
        }

        return when (operator) {
            "!" -> evalBangOperatorExpression(rightEval)
            "-" -> evalMinusOperatorExpression(rightEval)
            else -> MonkeyError("unknown operator: $operator${rightEval.getType()}")
        }
    }

    private fun evalBangOperatorExpression(rightObj: MonkeyObject?): MonkeyObject {
        return when (rightObj) {
            is MonkeyBool -> MonkeyBool.negate(rightObj)
            is MonkeyInt -> MonkeyBool.negate(MonkeyBool.fromInt(rightObj))
            is MonkeyNull -> MonkeyBool.TRUE
            else -> MonkeyBool.FALSE
        }
    }

    private fun evalMinusOperatorExpression(rightObj: MonkeyObject?): MonkeyObject {
        if (rightObj !is MonkeyInt) {
            return MonkeyError("unknown operator: $operator${rightObj?.getType()}")
        }

        return MonkeyInt(-rightObj.value)
    }
}