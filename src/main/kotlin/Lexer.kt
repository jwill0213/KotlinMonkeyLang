package org.example

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
            '=' -> tok = Token(TokenType.ASSIGN, ch.toString())
            ';' -> tok = Token(TokenType.SEMICOLON, ch.toString())
            '(' -> tok = Token(TokenType.LPAREN, ch.toString())
            ')' -> tok = Token(TokenType.RPAREN, ch.toString())
            ',' -> tok = Token(TokenType.COMMA, ch.toString())
            '+' -> tok = Token(TokenType.PLUS, ch.toString())
            '{' -> tok = Token(TokenType.LBRACE, ch.toString())
            '}' -> tok = Token(TokenType.RBRACE, ch.toString())
            null -> tok = Token(TokenType.EOF, "")
            else -> {
                tok = if (ch!!.isLetter()) {
                    readIdentifier()
                } else if (ch!!.isDigit()) {
                    readNumber()
                } else {
                    Token(TokenType.ILLEGAL, ch.toString())
                }
                return tok
            }
        }

        readChar()
        return tok
    }

    private fun readIdentifier(): Token {
        val identStart = position
        while(ch!!.isLetter()) {
            readChar()
        }

        return when (val literal = input.substring(identStart, position)) {
            "fn" -> Token(TokenType.FUNCTION, literal)
            "let" -> Token(TokenType.LET, literal)
            else -> Token(TokenType.IDENT, literal)
        }
    }

    private fun readNumber(): Token {
        val numberStart = position
        while(ch!!.isDigit()) {
            readChar()
        }

        return Token(TokenType.INT, input.substring(numberStart, position))
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

    private fun skipWhitespace() {
        while (ch != null && ch!!.isWhitespace()) {
            readChar()
        }
    }
}