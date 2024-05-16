package org.example.`object`

import org.example.parser.ast.expressions.Identifier
import org.example.parser.ast.statements.BlockStatement

class MonkeyFunction(var params: List<Identifier>, var body: BlockStatement, var env: Environment) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        return "fn(${params.joinToString { it.toString() }}) ${body.toString()}"
    }
}