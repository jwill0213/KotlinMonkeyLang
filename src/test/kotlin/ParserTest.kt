import org.example.Lexer
import org.example.Parser
import org.example.ast.statements.LetStatement
import org.example.ast.statements.Statement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun test_parseLetStatements() {
        val input = """
            let x = 5;
            let y = 10;
            let foobar = 838383;
        """.trimIndent()

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()

        assertNotNull(program)

        val statements = program!!.statements

        assertEquals(3, statements.size, "Should be 3 statements")

        assertLetStatement(statements[0], "x")
        assertLetStatement(statements[1], "y")
        assertLetStatement(statements[2], "foobar")
    }

    private fun assertLetStatement(statement: Statement, name: String) {
        assertTrue(statement is LetStatement)
        val letStatement: LetStatement = statement as LetStatement
        assertEquals(statement.getTokenLiteral(), "let")
        assertEquals(letStatement.name.getTokenLiteral(), name)
    }
}