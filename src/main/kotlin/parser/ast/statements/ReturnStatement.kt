package org.example.parser.ast.statements

import org.example.lexer.Token
import org.example.`object`.MonkeyNull
import org.example.`object`.MonkeyObject
import org.example.`object`.MonkeyReturn
import org.example.parser.ast.expressions.Expression

class ReturnStatement(private val token: Token) : Statement() {
    var value: Expression? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${getTokenLiteral()} ${value.toString()};"
    }

    override fun eval(): MonkeyObject {
        val retVal = value?.eval() ?: MonkeyNull.NULL
        return MonkeyReturn(retVal)
    }
}