package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.MonkeyBool
import org.example.`object`.MonkeyInt
import org.example.`object`.MonkeyNull
import org.example.`object`.MonkeyObject

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
        return when (operator) {
            "!" -> evalBangOperatorExpression(right?.eval())
            "-" -> evalMinusOperatorExpression(right?.eval())
            else -> MonkeyNull.NULL
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
            return MonkeyNull.NULL
        }

        return MonkeyInt(-rightObj.value)
    }
}