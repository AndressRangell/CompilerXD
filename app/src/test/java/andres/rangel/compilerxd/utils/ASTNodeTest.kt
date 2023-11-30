package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.data.model.TokenType
import org.junit.Assert.assertEquals
import org.junit.Test

class ASTTest {
    @Test
    fun testLetStatement() {
        val program = Program(
            statements = mutableListOf(
                LetStatement(
                    token = Token(TokenType.VAR, "var"),
                    name = Identifier(
                        token = Token(TokenType.IDENTIFIER, "var1"),
                        value = "var1"
                    ),
                    value = Identifier(
                        token = Token(TokenType.IDENTIFIER, "var2"),
                        value = "var2"
                    )
                )
            )
        )

        val programString = program.toString()

        assertEquals("var var1 = var2;", programString)
    }

    @Test
    fun testReturnStatement() {
        val program = Program(
            statements = mutableListOf(
                ReturnStatement(
                    token = Token(TokenType.RETURN, "return"),
                    returnValue = Identifier(
                        token = Token(TokenType.IDENTIFIER, "var1"),
                        value = "var1"
                    )
                )
            )
        )

        val programString = program.toString()

        assertEquals("return var1;", programString)
    }

    @Test
    fun testIntegerExpressions() {
        val program = Program(
            statements = mutableListOf(
                ExpressionStatement(
                    token = Token(TokenType.INTEGER, "5"),
                    expression = MyInteger(
                        token = Token(TokenType.INTEGER, "5"),
                        value = 5
                    )
                )
            )
        )

        val programString = program.toString()

        assertEquals("5", programString)
    }
}