package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.Environment
import org.example.`object`.MonkeyError
import org.example.`object`.MonkeyHash
import org.example.`object`.MonkeyObject

class HashLiteral(private val token: Token) : Expression() {
    var expressionMap: MutableMap<Expression, Expression> = mutableMapOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        val pairs = expressionMap.entries.map { "${it.key}:${it.value}" }
        return "{${pairs.joinToString { it }}}"
    }

    override fun eval(env: Environment): MonkeyObject {
        val objMap: HashMap<Int, MonkeyObject> = HashMap()

        for (entry in expressionMap) {
            val keyObj = entry.key.eval(env)
            if (keyObj is MonkeyError) {
                return keyObj
            }

            if (!keyObj!!.getType().isHashable()) {
                return MonkeyError("unusable as hash key: ${keyObj.getType()}")
            }

            val valObj = entry.value.eval(env)
            if (valObj is MonkeyError) {
                return valObj
            }

            if (valObj != null) {
                objMap[keyObj.hashCode()] = valObj
            }
        }

        return MonkeyHash(objMap)
    }
}