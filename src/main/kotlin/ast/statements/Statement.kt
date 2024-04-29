package org.example.ast.statements

import org.example.ast.Node

abstract class Statement : Node {
    override fun getNodeType(): String {
        return "STATEMENT"
    }

    abstract override fun getTokenLiteral(): String

    abstract override fun toString(): String
}