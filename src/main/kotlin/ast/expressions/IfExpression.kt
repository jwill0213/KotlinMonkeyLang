package org.example.ast.expressions

import org.example.Token
import org.example.ast.statements.BlockStatement

class IfExpression(private val token: Token) : Expression() {
    var condition: Expression? = null
    var consequence: BlockStatement? = null
    var alternative: BlockStatement? = null

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        var output = "(if ${condition.toString()} ${consequence.toString()})"
        if (alternative != null) {
            output += "else ${alternative.toString()}"
        }
        return output
    }
}