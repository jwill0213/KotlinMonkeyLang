import org.example.lexer.Token
import org.example.lexer.TokenType
import org.example.parser.ast.Program
import org.example.parser.ast.expressions.Identifier
import org.example.parser.ast.statements.LetStatement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AstTest {
    @Test
    fun test_toString() {
        val program = Program()
        program.statements.add(
            LetStatement(
                Token(TokenType.LET),
                Identifier(Token(TokenType.IDENT, "myVar")),
                Identifier(Token(TokenType.IDENT, "anotherVar"))
            )
        )

        assertEquals("let myVar = anotherVar;", program.toString())
    }
}