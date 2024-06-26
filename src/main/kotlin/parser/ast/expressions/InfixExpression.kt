package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*

class InfixExpression(private val token: Token, var left: Expression? = null) : Expression() {
    var operator: String = getTokenLiteral()
    var right: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "(${left.toString()} $operator ${right.toString()})"
    }

    override fun eval(env: Environment): MonkeyObject {
        val leftEval = left?.eval(env) ?: MonkeyNull.NULL
        if (leftEval is MonkeyError) {
            return leftEval
        }

        val rightEval = right?.eval(env) ?: MonkeyNull.NULL
        if (rightEval is MonkeyError) {
            return rightEval
        }

        return when {
            leftEval is MonkeyInt && rightEval is MonkeyInt -> evalIntInfixExpression(leftEval, rightEval)
            leftEval is MonkeyBool && rightEval is MonkeyBool -> evalBooleanInfixExpression(leftEval, rightEval)
            leftEval is MonkeyString && rightEval is MonkeyString -> evalStringInfixExpression(leftEval, rightEval)
            leftEval.getType() != rightEval.getType() -> MonkeyError("type mismatch: ${leftEval.getType()} $operator ${rightEval.getType()}")
            else -> MonkeyError("unknown operator: ${leftEval.getType()} $operator ${rightEval.getType()}")
        }
    }

    private fun evalIntInfixExpression(left: MonkeyInt, right: MonkeyInt): MonkeyObject {
        return when (operator) {
            "+" -> MonkeyInt(left.value + right.value)
            "-" -> MonkeyInt(left.value - right.value)
            "*" -> MonkeyInt(left.value * right.value)
            "/" -> MonkeyInt(left.value / right.value)
            "<" -> MonkeyBool.parseNativeBool(left.value < right.value)
            ">" -> MonkeyBool.parseNativeBool(left.value > right.value)
            "==" -> MonkeyBool.parseNativeBool(left.value == right.value)
            "!=" -> MonkeyBool.parseNativeBool(left.value != right.value)
            else -> MonkeyError("unknown operator: ${left.getType()} $operator ${right.getType()}")
        }
    }

    private fun evalBooleanInfixExpression(left: MonkeyBool, right: MonkeyBool): MonkeyObject {
        return when (operator) {
            "==" -> MonkeyBool.parseNativeBool(left.value == right.value)
            "!=" -> MonkeyBool.parseNativeBool(left.value != right.value)
            else -> MonkeyError("unknown operator: ${left.getType()} $operator ${right.getType()}")
        }
    }

    private fun evalStringInfixExpression(left: MonkeyString, right: MonkeyString): MonkeyObject {
        return when (operator) {
            "+" -> MonkeyString(left.value + right.value)
            else -> MonkeyError("unknown operator: ${left.getType()} $operator ${right.getType()}")
        }
    }
}