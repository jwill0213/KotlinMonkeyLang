package org.example

import org.example.ast.Program
import org.example.ast.expressions.Expression
import org.example.ast.expressions.Identifier
import org.example.ast.expressions.IntegerLiteral
import org.example.ast.expressions.Precedence
import org.example.ast.statements.ExpressionStatement
import org.example.ast.statements.LetStatement
import org.example.ast.statements.ReturnStatement
import org.example.ast.statements.Statement

class Parser(private val lexer: Lexer) {
    var errors: MutableList<String> = mutableListOf()

    private var currToken: Token = Token(TokenType.ILLEGAL)
    private var peekToken: Token = Token(TokenType.ILLEGAL)

    private val prefixParseFnMap = mutableMapOf<TokenType, () -> Expression>()
    private val infixParseFnMap = mutableMapOf<TokenType, () -> Expression>()

    // For init call nextToken twice to ensure both tokens are set
    init {
        nextToken()
        nextToken()

        registerPrefix(TokenType.IDENT) { parseIdentifier() }
        registerPrefix(TokenType.INT) { parseIntegerLiteral() }
    }

    private fun nextToken() {
        currToken = peekToken
        peekToken = lexer.nextToken()
    }

    fun parseProgram(): Program {
        val program = Program()

        while (!currTokenIs(TokenType.EOF)) {
            val statement = parseStatement()
            if (statement != null) {
                program.statements.add(statement)
            }
            nextToken()
        }

        return program
    }

    private fun parseStatement(): Statement? {
        return when (currToken.tokenType) {
            TokenType.LET -> parseLetStatement()
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseLetStatement(): Statement? {
        val stmt = LetStatement(currToken)

        if (!expectPeek(TokenType.IDENT)) {
            return null
        }

        stmt.name = Identifier(currToken)

        if (!expectPeek(TokenType.ASSIGN)) {
            return null
        }

        // TODO skip expression for now, read until semicolon
        while (currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseReturnStatement(): Statement {
        val stmt = ReturnStatement(currToken)

        nextToken()

        // TODO skip expression for now, read until semicolon
        while (currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseExpressionStatement(): Statement {
        val stmt = ExpressionStatement(currToken)

        stmt.expression = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        val prefix = prefixParseFnMap[currToken.tokenType]

        if (prefix == null) {
            errors.add("No prefix parse function for $currToken found")
            return null
        }

        val leftExp = prefix.invoke()

        return leftExp
    }

    private fun parseIdentifier(): Expression {
        return Identifier(currToken)
    }

    private fun parseIntegerLiteral(): Expression {
        return IntegerLiteral(currToken)
    }

    private fun parsePrefixFn(): Expression? {
        return null
    }

    private fun parseInfixFn(leftExp: Expression): Expression? {
        return null
    }

    private fun currTokenIs(type:TokenType): Boolean {
        return currToken.tokenType == type
    }

    private fun peekTokenIs(type:TokenType): Boolean {
        return peekToken.tokenType == type
    }

    private fun expectPeek(type: TokenType): Boolean {
        if (peekTokenIs(type)) {
            nextToken()
            return true
        } else {
            errors.add("Expected next token to be $type but was ${peekToken.tokenType}")
            return false
        }
    }

    private fun registerPrefix(type: TokenType, fn: () -> Expression) {
        prefixParseFnMap[type] = fn
    }

    private fun registerInfix(type: TokenType, fn: () -> Expression) {
        infixParseFnMap[type] = fn
    }
}