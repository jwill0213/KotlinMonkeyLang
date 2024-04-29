package org.example.ast

interface Node {
    fun getNodeType(): String
    fun getTokenLiteral(): String
    override fun toString(): String
}