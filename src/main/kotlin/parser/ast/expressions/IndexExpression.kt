package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*

class IndexExpression(private val token: Token, val left: Expression) : Expression() {
    var index: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "($left[$index])"
    }

    override fun eval(env: Environment): MonkeyObject {
        val leftObj = left.eval(env)
        if (leftObj is MonkeyError) {
            return leftObj
        }

        val idxObj = index?.eval(env)
        if (idxObj is MonkeyError) {
            return idxObj
        }

        return if (leftObj is MonkeyArray && idxObj is MonkeyInt) {
            evalArrayIndexExpression(leftObj, idxObj)
        } else if (leftObj is MonkeyHash) {
            evalHashIndexExpression(leftObj, idxObj!!)
        } else {
            MonkeyError("index operator not supported: ${leftObj?.getType()}")
        }
    }

    private fun evalArrayIndexExpression(arr: MonkeyArray, index: MonkeyInt): MonkeyObject {
        val idxVal = index.value
        val max = arr.elements.size - 1

        if (idxVal < 0 || idxVal > max) {
            return MonkeyNull.NULL
        }

        return arr.elements[idxVal]
    }

    private fun evalHashIndexExpression(hash: MonkeyHash, index: MonkeyObject): MonkeyObject {
        if (!index.getType().isHashable()) {
            return MonkeyError("unusable as hash key: ${index.getType()}")
        }

        val returnObj = hash.objMap[index.hashCode()] ?: return MonkeyNull.NULL

        return returnObj
    }
}