package org.example.parser.ast.statements

import org.example.parser.ast.Node

abstract class Statement : Node {
    override fun getNodeType(): String {
        return "STATEMENT"
    }

    abstract override fun getTokenLiteral(): String

    abstract override fun toString(): String
}