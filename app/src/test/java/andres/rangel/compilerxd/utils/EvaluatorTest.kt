package andres.rangel.compilerxd.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluatorTest {
    @Test
    fun testIntegerEvaluation() {
        val tests = listOf(
            Pair("5", 5),
            Pair("10", 10),
            Pair("-5", -5),
            Pair("-10", -10),
            Pair("5 + 5", 10),
            Pair("5 - 10", -5),
            Pair("2 * 2 * 2 * 2", 16),
            Pair("2 * 5 - 3", 7),
            Pair("50 / 2", 25),
            Pair("2 * (5 - 3)", 4),
            Pair("(2 + 7) / 3", 3),
            Pair("50 / 2 * 2 + 10", 60),
            Pair("5 / 2", 2)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testIntegerObject(evaluated, expected)
        }
    }

    @Test
    fun testBooleanEvaluation() {
        val tests = listOf(
            Pair("true", true),
            Pair("false", false),
            Pair("1 < 2", true),
            Pair("1 > 2", false),
            Pair("1 < 1", false),
            Pair("1 > 1", false),
            Pair("1 == 1", true),
            Pair("1 != 1", false),
            Pair("1 != 2", true),
            Pair("true == true", true),
            Pair("false == false", true),
            Pair("true == false", false),
            Pair("true != false", true),
            Pair("(1 < 2) == true", true),
            Pair("(1 < 2) == false", false),
            Pair("(1 > 2) == true", false),
            Pair("(1 > 2) == false", true)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testBooleanObject(evaluated, expected)
        }
    }

    @Test
    fun testBangOperator() {
        val tests = listOf(
            Pair("!true", false),
            Pair("!false", true),
            Pair("!!true", true),
            Pair("!!false", false),
            Pair("!5", false),
            Pair("!!5", true)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testBooleanObject(evaluated, expected)
        }
    }

    @Test
    fun testIfElseEvaluation() {
        val tests = listOf(
            Pair("if (true) { 10 }", 10),
            Pair("if (1) { 10 }", 10),
            Pair("if (1 < 2) { 10 }", 10),
            Pair("if (1 < 2) { 10 } else { 20 }", 10),
            Pair("if (1 > 2) { 10 } else { 20 }", 20)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testIntegerObject(evaluated, expected)
        }
    }

    @Test
    fun testReturnEvaluation() {
        val tests = listOf(
            Pair("return 10;", 10),
            Pair("return 10; 9;", 10),
            Pair("return 2 * 5; 9;", 10),
            Pair("9; return 3 * 6; 9;", 18),
            Pair(
                """
            if (10 > 1) {
                if (20 > 10) {
                    return 1;
                }

                return 0;
            }
        """.trimIndent(), 1
            )
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testIntegerObject(evaluated, expected)
        }
    }

    @Test
    fun testErrorHandling() {
        val tests = listOf(
            Pair("5 + true", "Error type mismatch: INTEGER + BOOLEAN"),
            Pair("-true", "Error unknown operator: - BOOLEAN"),
            Pair("true + false;", "Error unknown operator: BOOLEAN + BOOLEAN"),
            Pair("5; true - false;", "Error unknown operator: BOOLEAN - BOOLEAN"),
            Pair(
                """
            if (10 > 7) {
                return true + false;
            }
        """.trimIndent(), "Error unknown operator: BOOLEAN + BOOLEAN"
            ),
            Pair(
                """
            if (10 > 1) {
                if (true) {
                    return true * false
                }
                return 1;
            }
        """.trimIndent(), "Error unknown operator: BOOLEAN * BOOLEAN"
            ),
            Pair(
                """
            if (5 < 2) {
                return 1;
            } else {
                return true / false;
            }
        """.trimIndent(), "Error unknown operator: BOOLEAN / BOOLEAN"
            ),
            Pair("foobar;", "Error identifier not found: foobar"),
            Pair("\"Foo\" - \"Bar\";", "Error unknown operator: STRING - STRING")
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)

            assertTrue(evaluated is ObjectError)

            val error = evaluated as ObjectError
            assertEquals(expected, error.message)
        }
    }

    @Test
    fun testAssignmentEvaluation() {
        val tests = listOf(
            Pair("var a = 5; a;", 5),
            Pair("var a = 5 * 5; a;", 25),
            Pair("var a = 5; var b = a; b;", 5),
            Pair("var a = 5; var b = a; var c = a + b + 5; c;", 15)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testIntegerObject(evaluated, expected)
        }
    }

    @Test
    fun testFunctionEvaluation() {
        val source = "function(x) { x + 2; };"

        val evaluated = evaluateTest(source)

        assertTrue(evaluated is ObjectFunction)

        val function = evaluated as ObjectFunction
        assertEquals(1, function.parameters.size)
        assertEquals("x", function.parameters[0].toString())
        assertEquals("(x + 2)", function.body.toString())
    }

    @Test
    fun testFunctionCalls() {
        val tests = listOf(
            Pair("var identity = function(x) { x }; identity(5);", 5),
            Pair(
                """
            var identity = function(x) { 
                return x; 
            }; 
            identity(5);
        """.trimIndent(), 5
            ),
            Pair(
                """
            var double = function(x) {
                return 2 * x;
            };
            double(5);
        """.trimIndent(), 10
            ),
            Pair(
                """
            var sum = function(x, y) {
                return x + y;
            };
            sum(3, 8);
        """.trimIndent(), 11
            ),
            Pair(
                """
            var sum = function(x, y) {
                return x + y;
            };
            sum(5 + 5, sum(10, 10));
        """.trimIndent(), 30
            ),
            Pair("function(x) { x }(5)", 5)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testIntegerObject(evaluated, expected)
        }
    }

    @Test
    fun testStringEvaluation() {
        val tests = listOf(
            Pair("\"Hello world!\"", "Hello world!"),
            Pair("function() { return \"Code test\"; }()", "Code test")
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testStringObject(evaluated, expected)
        }
    }

    @Test
    fun testStringConcatenation() {
        val tests = listOf(
            Pair("\"Foo\" + \"bar\";", "Foobar"),
            Pair("\"Hello,\" + \" \" + \"world!\"", "Hello, world!"),
            Pair(
                """
            var greeting = function(name) {
                return "Hola " + name + "!";
            };
            greeting("David");
        """.trimIndent(), "Hola David!"
            )
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testStringObject(evaluated, expected)
        }
    }

    @Test
    fun testStringComparison() {
        val tests = listOf(
            Pair("\"a\" == \"a\"", true),
            Pair("\"a\" != \"a\"", false),
            Pair("\"a\" == \"b\"", false),
            Pair("\"a\" != \"b\"", true)
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)
            testBooleanObject(evaluated, expected)
        }
    }

    @Test
    fun testBuiltinFunctions() {
        val tests = listOf(
            Pair("length(\"\");", 0),
            Pair("length(\"cuatro\");", 6),
            Pair("length(\"Hola mundo\");", 10),
            Pair("length(1);", "Error argument not supported"),
            Pair("length(\"one\", \"two\");", "Error incorrect number of arguments")
        )

        for ((source, expected) in tests) {
            val evaluated = evaluateTest(source)

            if (expected is Int) {
                testIntegerObject(evaluated, expected)
            } else {
                testErrorObject(evaluated, expected as String)
            }
        }
    }

    private fun testErrorObject(evaluated: Object, expected: String) {
        assertTrue(evaluated is ObjectError)

        val error = evaluated as ObjectError
        assertEquals(expected, error.message)
    }

    private fun testStringObject(evaluated: Object, expected: String) {
        assertTrue(evaluated is ObjectString)

        val string = evaluated as ObjectString
        assertEquals(expected, string.value)
    }

    private fun testNullObject(evaluated: Object) {
        assertEquals(Evaluator().nullValue, evaluated)
    }

    private fun evaluateTest(source: String): Object {
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        val env = ObjectEnvironment()

        val evaluated = Evaluator().evaluate(program, env)

        assertNotNull(evaluated)
        return evaluated!!
    }

    private fun testBooleanObject(evaluated: Object, expected: Boolean) {
        assertTrue(evaluated is ObjectBoolean)

        val boolean = evaluated as ObjectBoolean
        assertEquals(expected, boolean.value)
    }

    private fun testIntegerObject(evaluated: Object, expected: Int) {
        assertTrue(evaluated is ObjectInteger)

        val integer = evaluated as ObjectInteger
        assertEquals(expected, integer.value)
    }
}