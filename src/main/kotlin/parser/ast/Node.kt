package org.example.parser.ast

import org.example.`object`.MonkeyObject

interface Node {
    fun getNodeType(): String
    fun getTokenLiteral(): String
    override fun toString(): String
    fun eval(): MonkeyObject? {
        return null
    }
}