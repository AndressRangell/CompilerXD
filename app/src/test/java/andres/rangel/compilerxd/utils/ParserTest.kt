package andres.rangel.compilerxd.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserTest {
    @Test
    fun testParseProgram() {
        val source = "var x = 5;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertNotNull(program)
    }

    @Test
    fun testLetStatements() {
        val source = """
            var x = 5;
            var y = 10;
            var foo = 20;
            var bar = true;
        """.trimIndent()
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(4, program.statements.size)

        val expectedIdentifiersAndValues = listOf(
            Pair("x", 5),
            Pair("y", 10),
            Pair("foo", 20),
            Pair("bar", true)
        )

        for ((statement, expected) in program.statements.zip(expectedIdentifiersAndValues)) {
            assertEquals("var", statement.tokenLiteral())
            assertTrue(statement is LetStatement)

            val letStatement = statement as LetStatement

            assertEquals(expected.first, letStatement.name?.value)

            when (val value = letStatement.value) {
                is Identifier -> assertEquals(expected.second, value.value)
                is MyInteger -> assertEquals(expected.second, value.value)
                is MyBoolean -> assertEquals(expected.second, value.value)
            }
        }
    }

    @Test
    fun testNamesInLetStatements() {
        val source = """
            var x = 5;
            var y = 10;
            var foo = 20;
        """.trimIndent()
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val names = program.statements.map { (it as LetStatement).name?.value }

        val expectedNames = listOf("x", "y", "foo")

        assertEquals(expectedNames, names)
    }

    @Test
    fun testParseErrors() {
        val source = "var x 5;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        parser.parseProgram()

        assertEquals(1, parser.errorsList.size)
    }

    @Test
    fun testReturnStatement() {
        val source = """
        return 5;
        return foo;
        return true;
        return false;
    """.trimIndent()
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(4, program.statements.size)

        val expectedReturnValues = listOf<Any>(
            5,
            "foo",
            true,
            false
        )

        for ((statement, expectedReturnValue) in program.statements.zip(expectedReturnValues)) {
            assertEquals("return", statement.tokenLiteral())
            assertTrue(statement is ReturnStatement)

            val returnStatement = statement as ReturnStatement

            when (val returnValue = returnStatement.returnValue) {
                is Identifier -> assertEquals(expectedReturnValue, returnValue.value)
                is MyInteger -> assertEquals(expectedReturnValue, returnValue.value)
                is MyBoolean -> assertEquals(expectedReturnValue, returnValue.value)
            }
        }
    }

    @Test
    fun testIdentifierExpression() {
        val source = "foobar;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement

        assertTrue(expressionStatement.expression is Identifier)
        assertEquals("foobar", (expressionStatement.expression as Identifier).value)
    }

    @Test
    fun testIntegerExpressions() {
        val source = "5;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement

        assertTrue(expressionStatement.expression is MyInteger)
        assertEquals(5, (expressionStatement.expression as MyInteger).value)
    }

    @Test
    fun testPrefixExpression() {
        val source = "!5; -15; !true; !false;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(4, program.statements.size)

        val expectedOperatorsAndValues = listOf(
            Pair("!", 5),
            Pair("-", 15),
            Pair("!", true),
            Pair("!", false)
        )

        for ((statement, expected) in program.statements.zip(expectedOperatorsAndValues)) {
            assertTrue(statement is ExpressionStatement)

            val expressionStatement = statement as ExpressionStatement
            assertTrue(expressionStatement.expression is Prefix)

            val prefix = expressionStatement.expression as Prefix
            assertEquals(expected.first, prefix.operator)

            when (val right = prefix.right) {
                is Identifier -> assertEquals(expected.second, right.value)
                is MyInteger -> assertEquals(expected.second, right.value)
                is MyBoolean -> assertEquals(expected.second, right.value)
            }
        }
    }

    @Test
    fun testInfixExpressions() {
        val source = """
        5 + 5;
        5 - 5;
        5 * 5;
        5 / 5;
        5 > 5;
        5 < 5;
        5 == 5;
        5 != 5;
        true == true;
        true != false;
        false == false;
    """.trimIndent()
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(11, program.statements.size)

        val expectedOperatorsAndValues = listOf(
            Triple(5, "+", 5),
            Triple(5, "-", 5),
            Triple(5, "*", 5),
            Triple(5, "/", 5),
            Triple(5, ">", 5),
            Triple(5, "<", 5),
            Triple(5, "==", 5),
            Triple(5, "!=", 5),
            Triple(true, "==", true),
            Triple(true, "!=", false),
            Triple(false, "==", false)
        )

        for ((statement, expected) in program.statements.zip(expectedOperatorsAndValues)) {
            assertTrue(statement is ExpressionStatement)

            val expressionStatement = statement as ExpressionStatement
            assertTrue(expressionStatement.expression is Infix)

            val infix = expressionStatement.expression as Infix
            assertEquals(expected.second, infix.operator)

            when (val left = infix.left) {
                is Identifier -> assertEquals(expected.first, left.value)
                is MyInteger -> assertEquals(expected.first, left.value)
                is MyBoolean -> assertEquals(expected.first, left.value)
            }

            when (val right = infix.right) {
                is Identifier -> assertEquals(expected.third, right.value)
                is MyInteger -> assertEquals(expected.third, right.value)
                is MyBoolean -> assertEquals(expected.third, right.value)
            }
        }
    }

    @Test
    fun testBooleanExpression() {
        val source = "true; false;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(2, program.statements.size)

        val expectedValues = listOf(true, false)

        for ((statement, expectedValue) in program.statements.zip(expectedValues)) {
            assertTrue(statement is ExpressionStatement)

            val expressionStatement = statement as ExpressionStatement
            assertTrue(expressionStatement.expression is MyBoolean)
            assertEquals(expectedValue, (expressionStatement.expression as MyBoolean).value)
        }
    }

    @Test
    fun testOperatorPrecedence() {
        val testSources = listOf(
            Triple("-a * b;", "((-a) * b)", 1),
            Triple("!-a;", "(!(-a))", 1),
            Triple("a + b + c;", "((a + b) + c)", 1),
            Triple("a + b - c;", "((a + b) - c)", 1),
            Triple("a * b * c;", "((a * b) * c)", 1),
            Triple("a + b / c;", "(a + (b / c))", 1),
            Triple("a * b / c;", "((a * b) / c)", 1),
            Triple("a + b * c + d / e - f;", "(((a + (b * c)) + (d / e)) - f)", 1),
            Triple("5 > 4 == 3 < 4;", "((5 > 4) == (3 < 4))", 1),
            Triple("3 - 4 * 5 == 3 * 1 + 4 * 5;", "((3 - (4 * 5)) == ((3 * 1) + (4 * 5)))", 1),
            Triple("3 + 4; -5 * 5;", "(3 + 4)((-5) * 5)", 2),
            Triple("true;", "true", 1),
            Triple("false;", "false", 1),
            Triple("3 > 5 == true;", "((3 > 5) == true)", 1),
            Triple("3 < 5 == false;", "((3 < 5) == false)", 1),
            Triple("1 + (2 + 3) + 4;", "((1 + (2 + 3)) + 4)", 1),
            Triple("(5 + 5) * 2;", "((5 + 5) * 2)", 1),
            Triple("2 / (5 + 5);", "(2 / (5 + 5))", 1),
            Triple("-(5 + 5);", "(-(5 + 5))", 1),
            Triple("a + suma(b * c) + d;", "((a + suma((b * c))) + d)", 1),
            Triple(
                "suma(a, b, 1, 2 * 3, 4 + 5, suma(6, 7 * 8));",
                "suma(a, b, 1, (2 * 3), (4 + 5), suma(6, (7 * 8)))",
                1
            ),
            Triple("suma(a + b + c * d / f + g);", "suma((((a + b) + ((c * d) / f)) + g))", 1)
        )

        for ((source, expectedResult, expectedStatementCount) in testSources) {
            val lexer = Lexer(source)
            val parser = Parser(lexer)

            val program = parser.parseProgram()

            assertEquals(expectedStatementCount, program.statements.size)
            assertEquals(expectedResult, program.toString())
        }
    }

    @Test
    fun testCallExpression() {
        val source = "suma(1, 2 * 3, 4 + 5);"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        assertTrue(expressionStatement.expression is Call)

        val call = expressionStatement.expression as Call
        assertEquals("suma", call.function.toString())

        // Test arguments
        assertEquals(3, call.arguments?.size)
        assertEquals("1", call.arguments?.get(0).toString())
        assertEquals("(2 * 3)", call.arguments?.get(1).toString())
        assertEquals("(4 + 5)", call.arguments?.get(2).toString())
    }

    @Test
    fun testIfExpression() {
        val source = "if (x < y) { z }"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        assertTrue(expressionStatement.expression is If)

        val ifExpression = expressionStatement.expression as If

        assertEquals("(x < y)", ifExpression.condition.toString())

        assertEquals(1, ifExpression.consequence?.statements?.size)
        assertEquals(
            "z",
            (ifExpression.consequence?.statements?.get(0) as ExpressionStatement).expression.toString()
        )

        assertNull(ifExpression.alternative)
    }

    @Test
    fun testIfElseExpression() {
        val source = "if (x != y) { x } else { y }"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        assertTrue(expressionStatement.expression is If)

        val ifExpression = expressionStatement.expression as If

        assertEquals("(x != y)", ifExpression.condition.toString())

        assertEquals(1, ifExpression.consequence?.statements?.size)
        assertEquals(
            "x",
            (ifExpression.consequence?.statements?.get(0) as ExpressionStatement).expression.toString()
        )

        assertEquals(1, ifExpression.alternative?.statements?.size)
        assertEquals(
            "y",
            (ifExpression.alternative?.statements?.get(0) as ExpressionStatement).expression.toString()
        )
    }

    @Test
    fun testFunctionLiteral() {
        val source = "function(x, y) { x + y}"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assertEquals(1, program.statements.size)
        assertTrue(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        assertTrue(expressionStatement.expression is Function)

        val functionLiteral = expressionStatement.expression as Function

        assertEquals(2, functionLiteral.parameters.size)
        assertEquals("x", functionLiteral.parameters[0].toString())
        assertEquals("y", functionLiteral.parameters[1].toString())

        assertEquals(1, functionLiteral.body?.statements?.size)
        assertEquals(
            "(x + y)",
            (functionLiteral.body?.statements?.get(0) as ExpressionStatement).expression.toString()
        )
    }

    @Test
    fun testFunctionParameters() {
        val tests = listOf(
            mapOf("input" to "function() {};", "expectedParams" to listOf<String>()),
            mapOf("input" to "function(x) {};", "expectedParams" to listOf("x")),
            mapOf("input" to "function(x, y, z) {};", "expectedParams" to listOf("x", "y", "z"))
        )

        for (test in tests) {
            val lexer = Lexer(test["input"] as String)
            val parser = Parser(lexer)

            val program = parser.parseProgram()

            val function = (program.statements[0] as ExpressionStatement).expression as Function

            val expectedParams = test["expectedParams"] as? List<*>
            assertEquals(expectedParams?.size, function.parameters.size)

            for (i in function.parameters.indices) {
                assertEquals(expectedParams?.get(i), function.parameters[i].value)
            }
        }
    }

    @Test
    fun testStringLiteralExpression() {
        val source = "\"hello world!\""
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val expressionStatement = program.statements[0] as ExpressionStatement
        val stringLiteral = expressionStatement.expression as StringLiteral

        assertEquals("hello world!", stringLiteral.value)
    }

    @Test
    fun testBoolean() {
        val source = "true; false;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val expectedValues = listOf(true, false)

        for ((statement, expectedValue) in program.statements.zip(expectedValues)) {
            val expressionStatement = statement as ExpressionStatement
            val boolean = expressionStatement.expression as MyBoolean

            assertEquals(expectedValue, boolean.value)
            assertEquals(if (expectedValue) "true" else "false", boolean.tokenLiteral())
        }
    }

    @Test
    fun testInfixExpression() {
        val source =
            "5 + 5; 5 - 5; 5 * 5; 5 / 5; 5 > 5; 5 < 5; 5 == 5; 5 != 5; true == true; true != false; false == false;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val expectedOperatorsAndValues = listOf(
            Triple(5, "+", 5),
            Triple(5, "-", 5),
            Triple(5, "*", 5),
            Triple(5, "/", 5),
            Triple(5, ">", 5),
            Triple(5, "<", 5),
            Triple(5, "==", 5),
            Triple(5, "!=", 5),
            Triple(true, "==", true),
            Triple(true, "!=", false),
            Triple(false, "==", false)
        )

        for ((statement, expected) in program.statements.zip(expectedOperatorsAndValues)) {
            val expressionStatement = statement as ExpressionStatement
            val infix = expressionStatement.expression as Infix

            when (val left = infix.left) {
                is Identifier -> assertEquals(expected.first, left.value)
                is MyInteger -> assertEquals(expected.first, left.value)
                is MyBoolean -> assertEquals(expected.first, left.value)
            }

            assertEquals(expected.second, infix.operator)

            when (val right = infix.right) {
                is Identifier -> assertEquals(expected.third, right.value)
                is MyInteger -> assertEquals(expected.third, right.value)
                is MyBoolean -> assertEquals(expected.third, right.value)
            }
        }
    }
}