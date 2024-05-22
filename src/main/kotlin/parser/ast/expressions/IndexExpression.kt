package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*

class IndexExpression(private val token: Token, val arr: Expression) : Expression() {
    var index: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "($arr[$index])"
    }

    override fun eval(env: Environment): MonkeyObject {
        val arrVal = arr.eval(env)
        if (arrVal is MonkeyError) {
            return arrVal
        }

        val iVal = index?.eval(env)
        if (iVal is MonkeyError) {
            return iVal
        }

        if (arrVal !is MonkeyArray || iVal !is MonkeyInt) {
            return MonkeyError("index operator not supported: ${arrVal?.getType()}")
        }

        val idx = iVal.value
        val max = arrVal.elements.size - 1

        if (idx < 0 || idx > max) {
            return MonkeyNull.NULL
        }

        return arrVal.elements[idx]
    }
}