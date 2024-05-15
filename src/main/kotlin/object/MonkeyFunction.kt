package org.example.`object`

import org.example.parser.ast.expressions.Identifier
import org.example.parser.ast.statements.BlockStatement

class MonkeyFunction : MonkeyObject {
    var params: List<Identifier> = mutableListOf()
    var body: BlockStatement? = null
    var env: Environment = Environment()

    override fun getType(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        return "fn(${params.joinToString { it.toString() }}) ${body.toString()}"
    }
}