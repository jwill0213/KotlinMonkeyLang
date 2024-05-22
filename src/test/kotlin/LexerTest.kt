import org.example.lexer.Lexer
import org.example.lexer.Token
import org.example.lexer.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LexerTest {

    @Test
    fun test_nextToken_basic() {
        val input = "=+(){},;"

        val expectedTokens = listOf(
            Token(TokenType.ASSIGN),
            Token(TokenType.PLUS),
            Token(TokenType.LPAREN),
            Token(TokenType.RPAREN),
            Token(TokenType.LBRACE),
            Token(TokenType.RBRACE),
            Token(TokenType.COMMA),
            Token(TokenType.SEMICOLON),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }

    @Test
    fun test_nextToken_codeSnip() {
        val input = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
                x + y;
            };
            
            let result = add(five, ten);
        """.trimIndent()

        val expectedTokens = listOf(
            Token(TokenType.LET),
            Token(TokenType.IDENT, "five"),
            Token(TokenType.ASSIGN),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "ten"),
            Token(TokenType.ASSIGN),
            Token(TokenType.INT, "10"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "add"),
            Token(TokenType.ASSIGN),
            Token(TokenType.FUNCTION),
            Token(TokenType.LPAREN),
            Token(TokenType.IDENT, "x"),
            Token(TokenType.COMMA),
            Token(TokenType.IDENT, "y"),
            Token(TokenType.RPAREN),
            Token(TokenType.LBRACE),
            Token(TokenType.IDENT, "x"),
            Token(TokenType.PLUS),
            Token(TokenType.IDENT, "y"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "result"),
            Token(TokenType.ASSIGN),
            Token(TokenType.IDENT, "add"),
            Token(TokenType.LPAREN),
            Token(TokenType.IDENT, "five"),
            Token(TokenType.COMMA),
            Token(TokenType.IDENT, "ten"),
            Token(TokenType.RPAREN),
            Token(TokenType.SEMICOLON),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }

    @Test
    fun test_nextToken_moreTokens() {
        val input = """
            !-/*5;
            5 < 10 > 5;
        """.trimIndent()

        val expectedTokens = listOf(
            Token(TokenType.BANG),
            Token(TokenType.MINUS),
            Token(TokenType.SLASH),
            Token(TokenType.ASTERISK),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.INT, "5"),
            Token(TokenType.LT),
            Token(TokenType.INT, "10"),
            Token(TokenType.GT),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }

    @Test
    fun test_nextToken_ifElseKeywords() {
        val input = """
            if (5 < 10) {
                return true;
            } else {
                return false;
            }
        """.trimIndent()

        val expectedTokens = listOf(
            Token(TokenType.IF),
            Token(TokenType.LPAREN),
            Token(TokenType.INT, "5"),
            Token(TokenType.LT),
            Token(TokenType.INT, "10"),
            Token(TokenType.RPAREN),
            Token(TokenType.LBRACE),
            Token(TokenType.RETURN),
            Token(TokenType.TRUE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.ELSE),
            Token(TokenType.LBRACE),
            Token(TokenType.RETURN),
            Token(TokenType.FALSE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }

    @Test
    fun test_nextToken_twoCharTokens() {
        val input = """
            10 == 10
            10 != 9
        """.trimIndent()

        val expectedTokens = listOf(
            Token(TokenType.INT, "10"),
            Token(TokenType.EQ),
            Token(TokenType.INT, "10"),
            Token(TokenType.INT, "10"),
            Token(TokenType.NOT_EQ),
            Token(TokenType.INT, "9"),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }

    @Test
    fun test_nextToken_fullCode() {
        val input = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
                x + y;
            };
            
            let result = add(five, ten);
            !-/*5;
            5 < 10 > 5;
            
            if (5 < 10) {
                return true;
            } else {
                return false;
            }
            
            10 == 10;
            10 != 9;
            "foobar";
            "foo bar";
            [1, 2];
        """.trimIndent()

        val expectedTokens = listOf(
            Token(TokenType.LET),
            Token(TokenType.IDENT, "five"),
            Token(TokenType.ASSIGN),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "ten"),
            Token(TokenType.ASSIGN),
            Token(TokenType.INT, "10"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "add"),
            Token(TokenType.ASSIGN),
            Token(TokenType.FUNCTION),
            Token(TokenType.LPAREN),
            Token(TokenType.IDENT, "x"),
            Token(TokenType.COMMA),
            Token(TokenType.IDENT, "y"),
            Token(TokenType.RPAREN),
            Token(TokenType.LBRACE),
            Token(TokenType.IDENT, "x"),
            Token(TokenType.PLUS),
            Token(TokenType.IDENT, "y"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LET),
            Token(TokenType.IDENT, "result"),
            Token(TokenType.ASSIGN),
            Token(TokenType.IDENT, "add"),
            Token(TokenType.LPAREN),
            Token(TokenType.IDENT, "five"),
            Token(TokenType.COMMA),
            Token(TokenType.IDENT, "ten"),
            Token(TokenType.RPAREN),
            Token(TokenType.SEMICOLON),
            Token(TokenType.BANG),
            Token(TokenType.MINUS),
            Token(TokenType.SLASH),
            Token(TokenType.ASTERISK),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.INT, "5"),
            Token(TokenType.LT),
            Token(TokenType.INT, "10"),
            Token(TokenType.GT),
            Token(TokenType.INT, "5"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.IF),
            Token(TokenType.LPAREN),
            Token(TokenType.INT, "5"),
            Token(TokenType.LT),
            Token(TokenType.INT, "10"),
            Token(TokenType.RPAREN),
            Token(TokenType.LBRACE),
            Token(TokenType.RETURN),
            Token(TokenType.TRUE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.ELSE),
            Token(TokenType.LBRACE),
            Token(TokenType.RETURN),
            Token(TokenType.FALSE),
            Token(TokenType.SEMICOLON),
            Token(TokenType.RBRACE),
            Token(TokenType.INT, "10"),
            Token(TokenType.EQ),
            Token(TokenType.INT, "10"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.INT, "10"),
            Token(TokenType.NOT_EQ),
            Token(TokenType.INT, "9"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.STRING, "foobar"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.STRING, "foo bar"),
            Token(TokenType.SEMICOLON),
            Token(TokenType.LBRACKET),
            Token(TokenType.INT, "1"),
            Token(TokenType.COMMA),
            Token(TokenType.INT, "2"),
            Token(TokenType.RBRACKET),
            Token(TokenType.SEMICOLON),
            Token(TokenType.EOF),
        )

        val lexer = Lexer(input)

        for (t in expectedTokens) {
            assertEquals(t, lexer.nextToken())
        }
    }
}