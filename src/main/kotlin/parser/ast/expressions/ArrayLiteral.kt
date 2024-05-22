package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyArray
import org.example.`object`.MonkeyError
import org.example.`object`.MonkeyObject

class ArrayLiteral(private val token: Token) : Expression() {
    var elements: List<Expression> = listOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "[${elements.joinToString { it.toString() }}]"
    }

    override fun eval(env: Environment): MonkeyObject {

        val elementObjects = mutableListOf<MonkeyObject>()

        for (ele in elements) {
            val evalElement = ele.eval(env)
            if (evalElement is MonkeyError) {
                return evalElement
            }
            elementObjects.add(evalElement!!)
        }

        if (elementObjects.size == 1 && elementObjects[0] is MonkeyError) {
            return elementObjects[0]
        }

        return MonkeyArray(elementObjects)
    }
}