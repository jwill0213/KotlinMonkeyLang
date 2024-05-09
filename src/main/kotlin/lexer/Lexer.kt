package org.example.lexer

data class Token(val tokenType: TokenType, val literal: String) {
    constructor(tokenType: TokenType) : this(tokenType, tokenType.token)
}

class Lexer(private val input: String) {
    private var position: Int = 0
    private var readPosition: Int = 0
    private var ch: Char? = null

    init {
        readChar()
    }

    fun nextToken(): Token {
        val tok: Token

        skipWhitespace()

        when (ch) {
            '=' -> {
                tok = if (peekChar() == '=') {
                    readChar()
                    Token(TokenType.EQ)
                } else {
                    Token(TokenType.ASSIGN)
                }
            }
            ';' -> tok = Token(TokenType.SEMICOLON)
            '(' -> tok = Token(TokenType.LPAREN)
            ')' -> tok = Token(TokenType.RPAREN)
            ',' -> tok = Token(TokenType.COMMA)
            '+' -> tok = Token(TokenType.PLUS)
            '{' -> tok = Token(TokenType.LBRACE)
            '}' -> tok = Token(TokenType.RBRACE)
            '-' -> tok = Token(TokenType.MINUS)
            '!' -> {
                tok = if (peekChar() == '=') {
                    readChar()
                    Token(TokenType.NOT_EQ)
                } else {
                    Token(TokenType.BANG)
                }
            }
            '*' -> tok = Token(TokenType.ASTERISK)
            '/' -> tok = Token(TokenType.SLASH)
            '<' -> tok = Token(TokenType.LT)
            '>' -> tok = Token(TokenType.GT)
            null -> tok = Token(TokenType.EOF)
            else -> {
                tok = if (ch!!.isLetter()) {
                    readIdentifier()
                } else if (ch!!.isDigit()) {
                    readNumber()
                } else {
                    Token(TokenType.ILLEGAL)
                }
                return tok
            }
        }

        readChar()
        return tok
    }

    private fun readChar() {
        ch = if (readPosition >= input.length) {
            null
        } else {
            input[readPosition]
        }
        position = readPosition
        readPosition += 1
    }

    private fun peekChar(): Char? {
        return if (readPosition >= input.length) {
            null
        } else {
            input[readPosition]
        }
    }

    private fun readIdentifier(): Token {
        val identStart = position
        while(ch?.isLetter() == true) {
            readChar()
        }

        val literal = input.substring(identStart, position)

        return Token(TokenType.findTokenType(literal), literal)
    }

    private fun readNumber(): Token {
        val numberStart = position
        while(ch?.isDigit() == true) {
            readChar()
        }

        return Token(TokenType.INT, input.substring(numberStart, position))
    }

    private fun skipWhitespace() {
        while (ch != null && ch!!.isWhitespace()) {
            readChar()
        }
    }
}