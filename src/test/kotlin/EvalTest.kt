import org.example.lexer.Lexer
import org.example.`object`.*
import org.example.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

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
            Pair("\"Hello\" - \"World\"", "unknown operator: STRING - STRING"),
            Pair("{\"name\": \"Monkey\"}[fn(x) { x }];", "unusable as hash key: FUNCTION")
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

    @Test
    fun test_functionObject() {
        val test = "fn(x) { x + 2; };"

        val evaluated = evalProgramForTest(test)

        assertTrue(evaluated is MonkeyFunction)
        assertEquals(1, evaluated.params.size)
        assertEquals("x", evaluated.params[0].toString())
        assertEquals("(x + 2)", evaluated.body.toString())
    }

    @Test
    fun test_functionApplication() {
        val tests = listOf(
            Pair("let identity = fn(x) { x; }; identity(5);", 5),
            Pair("let identity = fn(x) { return x; }; identity(5);", 5),
            Pair("let double = fn(x) { x * 2; }; double(5);", 10),
            Pair("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
            Pair("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
            Pair("fn(x) { x; }(5)", 5),
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertIntegerObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_evalStringLiteral() {
        val input = "\"Hello World!\";"

        val evaluated = evalProgramForTest(input)
        assertTrue(evaluated is MonkeyString)
        assertEquals("Hello World!", evaluated.value)
    }

    @Test
    fun test_evalStringConcat() {
        val input = "\"Hello\" + \" \" +  \"World!\";"

        val evaluated = evalProgramForTest(input)
        assertTrue(
            evaluated is MonkeyString,
            "Expected 'MonkeyString' but got '${evaluated.javaClass.simpleName}'"
        )
        assertEquals("Hello World!", evaluated.value)
    }

    @Test
    fun test_evalBuiltinFunctions() {
        val tests = listOf(
            Pair("len(\"\");", 0),
            Pair("len(\"four\");", 4),
            Pair("len(\"hello world\");", 11),
            Pair("len(1);", "argument to 'len' not supported, got INTEGER"),
            Pair("len(\"one\", \"two\");", "wrong number of arguments for 'len'. got=2, want=1"),
            Pair("len([]);", 0),
            Pair("len([1]);", 1),
            Pair("len([1, 2, 3, 4]);", 4),
            Pair("len([1], [1, 2]);", "wrong number of arguments for 'len'. got=2, want=1"),
            Pair("rest([1, 2, 3, 4, 5]);", listOf(2, 3, 4, 5)),
            Pair("rest(rest(rest([1, 2, 3, 4, 5])));", listOf(4, 5)),
            Pair("rest(rest(rest([1, 2, 3, 4, 5])));", listOf(4, 5)),
            Pair("rest(rest(rest([1, 2, 3])));", null),
            Pair("push([1, 2, 3, 4, 5]);", "wrong number of arguments for 'push'. got=1, want=2"),
            Pair("push([1], 5);", listOf(1, 5)),
        )

        for (testCase in tests) {
            val evaluated = evalProgramForTest(testCase.first)

            when (testCase.second) {
                is Int -> assertIntegerObject(evaluated, testCase.second as Int)
                is List<*> -> {
                    assertTrue(evaluated is MonkeyArray)
                    assertMonkeyObject(evaluated, testCase.second)
                }
                is String -> {
                    assertTrue(evaluated is MonkeyError, "should be error for string")
                    assertEquals(testCase.second as String, evaluated.message)
                }
            }
        }
    }

    @Test
    fun test_evalArrayLiteral() {
        val test = "[1, 2 * 2, 3 + 3]"

        val evaluated = evalProgramForTest(test)

        assertTrue(evaluated is MonkeyArray)
        assertEquals(3, evaluated.elements.size)
        assertIntegerObject(evaluated.elements[0], 1)
        assertIntegerObject(evaluated.elements[1], 4)
        assertIntegerObject(evaluated.elements[2], 6)
    }

    @Test
    fun test_arrayIndexExpression() {
        val tests = listOf(
            Pair("[1, 2, 3][0]", 1),
            Pair("[1, 2, 3][1]", 2),
            Pair("[1, 2, 3][2]", 3),
            Pair("let i = 0; [1][i]", 1),
            Pair("[1, 2, 3][1 + 1];", 3),
            Pair("let myArray = [1, 2, 3]; myArray[2];", 3),
            Pair("let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];", 6),
            Pair("let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]", 2),
            Pair("[1, 2, 3][3]", null),
            Pair("[1, 2, 3][-1]", null),
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertMonkeyObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_stringHashKey() {
        val hello1 = MonkeyString("Hello World")
        val hello2 = MonkeyString("Hello World")
        val diff1 = MonkeyString("My name is johnny")
        val diff2 = MonkeyString("My name is johnny")

        assertEquals(hello1.hashCode(), hello2.hashCode())
        assertEquals(diff1.hashCode(), diff2.hashCode())
        assertNotEquals(hello1.hashCode(), diff1.hashCode())
    }

    @Test
    fun test_hashLiteral() {
        val input = """
            let two = "two";
            {
                "one": 10 - 9,
                two: 1 + 1,
                "thr" + "ee": 6 / 2,
                4: 4,
                true: 5,
                false: 6
            }
        """.trimIndent()

        val evaluated = evalProgramForTest(input)

        assertTrue(
            evaluated is MonkeyHash,
            "Expected 'MonkeyHash' but got '${evaluated.javaClass.simpleName}'"
        )

        val expected = mapOf(
            Pair(MonkeyString("one").hashCode(), 1),
            Pair(MonkeyString("two").hashCode(), 2),
            Pair(MonkeyString("three").hashCode(), 3),
            Pair(MonkeyInt(4).hashCode(), 4),
            Pair(MonkeyBool.TRUE.hashCode(), 5),
            Pair(MonkeyBool.FALSE.hashCode(), 6)
        )

        assertEquals(expected.size, evaluated.objMap.size)

        for (expectedEntry in expected.entries) {
            val retObj = evaluated.objMap[expectedEntry.key]
            assertIntegerObject(retObj, expectedEntry.value)
        }
    }

    @Test
    fun test_hashIndexExpression() {
        val tests = listOf(
            Pair("{\"foo\": 5, \"bar\": 16}[\"bar\"]", 16),
            Pair("{\"foo\": 5}[\"bar\"]", null),
            Pair("let key = \"foo\"; {\"foo\": 5}[key]", 5),
            Pair("{}[\"foo\"]", null),
            Pair("{5: 5}[5]", 5),
            Pair("{true: 5}[true]", 5),
            Pair("{false: 5, true: 8}[false]", 5)
        )

        for (testCase in tests) {
            val evaluated = evalProgramForTest(testCase.first)

            if (testCase.second is Int) {
                assertIntegerObject(evaluated, testCase.second as Int)
            } else {
                assertNullObject(evaluated)
            }
        }
    }

    private fun evalProgramForTest(input: String): MonkeyObject {
        val parser = Parser(Lexer(input))
        val program = parser.parseProgram()
        assertNotNull(program)
        val evaluatedValue = program.eval(Environment())
        assertNotNull(evaluatedValue)
        return evaluatedValue
    }

    private fun assertMonkeyObject(obj: MonkeyObject?, expected: Any?) {
        assertNotNull(obj)
        when (expected) {
            is Int -> assertIntegerObject(obj, expected)
            is Boolean -> assertBooleanObject(obj, expected)
            is String -> assertStringObject(obj, expected)
            is List<*> -> assertArrayObject(obj, expected)
            else -> if (expected == null) assertNullObject(obj) else fail("Unexpected assert type")
        }
    }

    private fun assertArrayObject(obj: MonkeyObject?, expected: List<*>) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyArray, "Expected 'MonkeyArray' but got '${obj}'")
        obj.elements.forEachIndexed { index, monkeyObj ->
            expected[index]?.let { assertMonkeyObject(monkeyObj, it) }
        }
    }

    private fun assertStringObject(obj: MonkeyObject?, expected: String) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyString, "Expected 'MonkeyString' but got '${obj}'")
        assertEquals(expected, obj.value)
    }

    private fun assertIntegerObject(obj: MonkeyObject?, expected: Int) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyInt, "Expected 'MonkeyInt' but got '${obj}'")
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