package org.example

import org.example.ast.Program
import org.example.ast.expressions.*
import org.example.ast.statements.ExpressionStatement
import org.example.ast.statements.LetStatement
import org.example.ast.statements.ReturnStatement
import org.example.ast.statements.Statement

class Parser(private val lexer: Lexer) {
    var errors: MutableList<String> = mutableListOf()

    private var currToken: Token = Token(TokenType.ILLEGAL)
    private var peekToken: Token = Token(TokenType.ILLEGAL)

    private val prefixParseFnMap = mutableMapOf<TokenType, () -> Expression>()
    private val infixParseFnMap = mutableMapOf<TokenType, (Expression) -> Expression>()

    // For init call nextToken twice to ensure both tokens are set
    init {
        nextToken()
        nextToken()

        registerPrefix(TokenType.IDENT) { parseIdentifier() }
        registerPrefix(TokenType.INT) { parseIntegerLiteral() }
        registerPrefix(TokenType.BANG) { parsePrefixExpression() }
        registerPrefix(TokenType.MINUS) { parsePrefixExpression() }

        registerInfix(TokenType.PLUS) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.MINUS) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.SLASH) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.ASTERISK) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.EQ) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.NOT_EQ) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.LT) { left: Expression -> parseInfixExpression(left) }
        registerInfix(TokenType.GT) { left: Expression -> parseInfixExpression(left) }
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
        while (!currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseReturnStatement(): Statement {
        val stmt = ReturnStatement(currToken)

        nextToken()

        // TODO skip expression for now, read until semicolon
        while (!currTokenIs(TokenType.SEMICOLON)) {
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

        var leftExp = prefix.invoke()

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.rank < peekPrecedence().rank) {
            val infix = infixParseFnMap[peekToken.tokenType]

            if (infix == null) {
                errors.add("No infix parse function for $currToken found")
                return null
            }

            nextToken()

            leftExp = infix(leftExp)
        }

        return leftExp
    }

    private fun parseIdentifier(): Expression {
        return Identifier(currToken)
    }

    private fun parseIntegerLiteral(): Expression {
        return IntegerLiteral(currToken)
    }

    private fun parsePrefixExpression(): Expression {
        val expr = PrefixExpression(currToken)

        nextToken()

        expr.right = parseExpression(Precedence.PREFIX)

        return expr
    }

    private fun parseInfixExpression(left: Expression): Expression {
        val expr = InfixExpression(currToken, left)

        val precedence = currPrecedence()
        nextToken()
        expr.right = parseExpression(precedence)

        return expr
    }

    private fun parsePrefixFn(): Expression? {
        return null
    }

    private fun parseInfixFn(leftExp: Expression): Expression? {
        return null
    }

    private fun currPrecedence(): Precedence {
        return Precedence.findPrecedence(currToken.tokenType) ?: Precedence.LOWEST
    }

    private fun peekPrecedence(): Precedence {
        return Precedence.findPrecedence(peekToken.tokenType) ?: Precedence.LOWEST
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

    private fun registerInfix(type: TokenType, fn: (Expression) -> Expression) {
        infixParseFnMap[type] = fn
    }
}
