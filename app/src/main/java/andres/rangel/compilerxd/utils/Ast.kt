package andres.rangel.compilerxd.utils

abstract class ASTNode {
    abstract fun tokenLiteral(): String
    abstract override fun toString(): String
}

open class Statement(private val token: Token) : ASTNode() {
    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "Statement(token=$token)"
    }
}

open class Expression(private val token: Token) : ASTNode() {
    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "Expression(token=$token)"
    }
}

class Program(val statements: MutableList<Statement>) : ASTNode() {
    override fun tokenLiteral(): String {
        return if (statements.isNotEmpty()) {
            statements[0].tokenLiteral()
        } else {
            ""
        }
    }

    override fun toString(): String {
        return statements.joinToString("") { it.toString() }
    }
}

class Identifier(token: Token, val value: String) : Expression(token) {
    override fun toString(): String {
        return value
    }
}

class LetStatement(token: Token, var name: Identifier? = null, var value: Expression? = null) :
    Statement(token) {
    override fun toString(): String {
        return "${tokenLiteral()} ${name.toString()} = ${value.toString()};"
    }
}

class ReturnStatement(token: Token, var returnValue: Expression? = null) : Statement(token) {
    override fun toString(): String {
        return "${tokenLiteral()} ${returnValue.toString()};"
    }
}

class ExpressionStatement(token: Token, var expression: Expression? = null) : Statement(token) {
    override fun toString(): String {
        return expression.toString()
    }
}

class MyInteger(token: Token, var value: Int? = null) : Expression(token) {
    override fun toString(): String {
        return value.toString()
    }
}

class Prefix(token: Token, val operator: String, var right: Expression? = null) :
    Expression(token) {
    override fun toString(): String {
        return "($operator${right.toString()})"
    }
}

class Infix(
    token: Token, val left: Expression, val operator: String, var right: Expression? = null
) : Expression(token) {
    override fun toString(): String {
        return "($left $operator ${right.toString()})"
    }
}

class MyBoolean(token: Token, var value: Boolean? = null) : Expression(token) {
    override fun toString(): String {
        return tokenLiteral()
    }
}

class Block(token: Token, val statements: MutableList<Statement>) : Statement(token) {
    override fun toString(): String {
        val out = StringBuilder()
        for (statement in statements) {
            out.append(statement.toString())
        }
        return out.toString()
    }
}

class If(
    token: Token,
    var condition: Expression? = null,
    var consequence: Block? = null,
    var alternative: Block? = null
) : Expression(token) {
    override fun toString(): String {
        val out = StringBuilder()
        out.append("if ${condition.toString()} ${consequence.toString()}")
        if (alternative != null) {
            out.append("else ${alternative.toString()}")
        }
        return out.toString()
    }
}

class Function(token: Token, var parameters: List<Identifier> = listOf(), var body: Block? = null) :
    Expression(token) {
    override fun toString(): String {
        val paramList = parameters.joinToString(", ") { it.toString() }
        return "${tokenLiteral()}($paramList) ${body.toString()}"
    }
}

class Call(token: Token, val function: Expression, var arguments: List<Expression>? = null) :
    Expression(token) {
    override fun toString(): String {
        val argList = arguments?.joinToString(", ") { it.toString() } ?: ""
        return "$function($argList)"
    }
}

class StringLiteral(token: Token, val value: String) : Expression(token) {
    override fun toString(): String {
        return super.tokenLiteral()
    }
}