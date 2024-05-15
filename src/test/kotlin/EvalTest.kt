import org.example.lexer.Lexer
import org.example.`object`.*
import org.example.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EvalTest {

    @Test
    fun test_evalIntExpression() {
        val tests = listOf(
            Pair("5", 5),
            Pair("10", 10),
            Pair("5000", 5000),
            Pair("-5", -5),
            Pair("-10", -10),
            Pair("5 + 5 + 5 + 5 - 10", 10),
            Pair("2 * 2 * 2 * 2 * 2", 32),
            Pair("-50 + 100 + -50", 0),
            Pair("5 * 2 + 10", 20),
            Pair("5 + 2 * 10", 25),
            Pair("20 + 2 * -10", 0),
            Pair("50 / 2 * 2 + 10", 60),
            Pair("2 * (5 + 10)", 30),
            Pair("3 * 3 * 3 + 10", 37),
            Pair("3 * (3 * 3) + 10", 37),
            Pair("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertIntegerObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_evalBoolExpression() {
        val tests = listOf(
            Pair("true", true),
            Pair("false", false),
            Pair("1 < 2", true),
            Pair("1 > 2", false),
            Pair("1 < 1", false),
            Pair("1 > 1", false),
            Pair("1 + 1 > 1", true),
            Pair("1 == 1", true),
            Pair("1 != 1", false),
            Pair("1 == 2", false),
            Pair("1 != 2", true),
            Pair("true == true", true),
            Pair("false == false", true),
            Pair("true == false", false),
            Pair("true != false", true),
            Pair("false != true", true),
            Pair("(1 < 2) == true", true),
            Pair("(1 < 2) == false", false),
            Pair("(1 > 2) == true", false),
            Pair("(1 > 2) == false", true)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertBooleanObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_evalBangExpression() {
        val tests = listOf(
            Pair("!true", false),
            Pair("!false", true),
            Pair("!5", false),
            Pair("!!true", true),
            Pair("!!false", false),
            Pair("!!5", true)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertBooleanObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_evalIfExpression() {
        val tests = listOf(
            Pair("if (true) { 10 }", 10),
            Pair("if (false) { 10 }", null),
            Pair("if (1) { 10 }", 10),
            Pair("if (1 < 2) { 10 }", 10),
            Pair("if (1 > 2) { 10 }", null),
            Pair("if (1 > 2) { 10 } else { 20 }", 20),
            Pair("if (1 < 2) { 10 } else { 20 }", 10),
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            if (testCase.second is Int) {
                assertIntegerObject(evaluatedValue, testCase.second as Int)
            } else {
                assertNullObject(evaluatedValue)
            }
        }
    }

    @Test
    fun test_evalReturnStatements() {
        val tests = listOf(
            Pair("return 10;", 10),
            Pair("return 10; 9;", 10),
            Pair("return 2 * 5; 9;", 10),
            Pair("9; return 2 * 5; 9;", 10),
            Pair(
                """
                if (10 > 1) {
                    if (10 > 1) {
                        return 10;
                    }

                    return 1;
                }
                """.trimIndent(), 10
            ),
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertIntegerObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_errorHandling() {
        val tests = listOf(
            Pair("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
            Pair("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
            Pair("-true", "unknown operator: -BOOLEAN"),
            Pair("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
            Pair("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
            Pair("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
            Pair(
                """
                if (10 > 1) {
                    if (10 > 1) {
                        return true + false;
                    }

                    return 1;
                }
                """.trimIndent(),
                "unknown operator: BOOLEAN + BOOLEAN",
            ),
            Pair("foobar;", "identifier not found: foobar"),
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertTrue(
                evaluatedValue is MonkeyError,
                "Expected 'MonkeyError' but got '${evaluatedValue.javaClass.simpleName}'"
            )
            assertEquals(testCase.second, evaluatedValue.message)
        }
    }

    @Test
    fun test_letStatements() {
        val tests = listOf(
            Pair("let a = 5; a;", 5),
            Pair("let a = 5 * 5; a;", 25),
            Pair("let a = 5; let b = a; b;", 5),
            Pair("let a = 5; let b = a; let c = a + b + 5; c;", 15)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertIntegerObject(evaluatedValue, testCase.second)
        }
    }

    private fun evalProgramForTest(input: String): MonkeyObject {
        val program = Parser(Lexer(input)).parseProgram()
        assertNotNull(program)
        val evaluatedValue = program.eval()
        assertNotNull(evaluatedValue)
        return evaluatedValue
    }

    private fun assertIntegerObject(obj: MonkeyObject?, expected: Int) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyInt, "Expected 'MonkeyInt' but got '${obj.javaClass.simpleName}'")
        assertEquals(expected, obj.value)
    }

    private fun assertBooleanObject(obj: MonkeyObject?, expected: Boolean) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyBool, "Expected 'MonkeyBool' but got '${obj.javaClass.simpleName}'")
        assertEquals(expected, obj.value)
    }

    private fun assertNullObject(obj: MonkeyObject?) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyNull, "Expected 'MonkeyNull' but got '${obj.javaClass.simpleName}'")
    }
}