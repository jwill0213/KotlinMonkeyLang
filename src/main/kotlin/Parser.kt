package org.example

import org.example.ast.Program
import org.example.ast.statements.Statement

class Parser(private val lexer: Lexer) {
    private var currToken: Token = Token(TokenType.ILLEGAL)
    private var peekToken: Token = Token(TokenType.ILLEGAL)

    // For init call nextToken twice to ensure both tokens are set
    init {
        nextToken()
        nextToken()
    }

    private fun nextToken() {
        currToken = peekToken
        peekToken = lexer.nextToken()
    }

    fun parseProgram(): Program? {
        val program = Program()

        while (currToken.tokenType != TokenType.EOF) {
            val statement = parseStatement()
            if (statement != null) {
                program.statements.add(statement)
            }
            nextToken()
        }
        return null
    }

    private fun parseStatement(): Statement? {
        return when (currToken.tokenType) {
            TokenType.LET -> parseLetStatement()
            else -> null
        }
    }

    private fun parseLetStatement(): Statement? {
        return null
    }
}