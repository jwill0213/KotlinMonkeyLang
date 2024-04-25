package org.example.ast.statements

import org.example.ast.Node

open class Statement : Node {
    override fun getNodeType(): String {
        return "STATEMENT"
    }

    override fun getTokenLiteral(): String {
        TODO("Not yet implemented")
    }
}