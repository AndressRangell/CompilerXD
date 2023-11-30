package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.data.model.TokenType

enum class Precedence(val value: Int) {
    LOWEST(1),
    EQUALS(2),
    LESS_GREATER(3),
    SUM(4),
    PRODUCT(5),
    PREFIX(6),
    CALL(7)
}

val PRECEDENCES: MutableMap<TokenType, Precedence> = mutableMapOf(
    TokenType.EQUALS to Precedence.EQUALS,
    TokenType.NOT_EQUALS to Precedence.EQUALS,
    TokenType.LESS_THAN to Precedence.LESS_GREATER,
    TokenType.GREATER_THAN to Precedence.LESS_GREATER,
    TokenType.PLUS to Precedence.SUM,
    TokenType.MINUS to Precedence.SUM,
    TokenType.DIVISION to Precedence.PRODUCT,
    TokenType.MULTIPLICATION to Precedence.PRODUCT,
    TokenType.LEFT_PAREN to Precedence.CALL
)

class Parser(private val lexer: Lexer) {
    private var currentToken: Token? = null
    private var peekToken: Token? = null
    private val errors: MutableList<String> = mutableListOf()

    private val prefixParseFns: MutableMap<TokenType, () -> Expression?> = registerPrefixFns()
    private val infixParseFns: MutableMap<TokenType, (Expression) -> Expression?> = registerInfixFns()

    init {
        advanceTokens()
        advanceTokens()
    }

    val errorsList: List<String>
        get() = errors

    fun parseProgram(): Program {
        val program = Program(mutableListOf())
        while (currentToken?.tokenType != TokenType.END_OF_FUNCTION) {
            val statement = parseStatement()
            statement?.let { program.statements.add(it) }
            advanceTokens()
        }
        return program
    }

    private fun advanceTokens() {
        currentToken = peekToken
        peekToken = lexer.nextToken()
        Token.TOKEN_LIST.add(peekToken!!)
    }

    private fun currentPrecedence(): Precedence {
        return currentToken?.tokenType?.let { PRECEDENCES[it] } ?: Precedence.LOWEST
    }

    private fun expectedToken(tokenType: TokenType): Boolean {
        return if (peekToken?.tokenType == tokenType) {
            advanceTokens()
            true
        } else {
            expectedTokenError(tokenType)
            false
        }
    }

    private fun expectedTokenError(tokenType: TokenType) {
        val error =
            "Formal language error, expected token $tokenType"
        errors.add(error)
    }

    private fun parseBlock(): Block {
        checkNotNull(currentToken)
        val blockStatement = Block(token = currentToken!!, statements = mutableListOf())
        advanceTokens()
        while (currentToken?.tokenType != TokenType.RIGHT_BRACE && currentToken?.tokenType != TokenType.END_OF_FUNCTION) {
            val statement = parseStatement()
            statement?.let {
                blockStatement.statements.add(it)
            }
            advanceTokens()
        }
        return blockStatement
    }

    private fun parseBoolean(): MyBoolean {
        checkNotNull(currentToken)
        return MyBoolean(token = currentToken!!, value = currentToken?.tokenType == TokenType.TRUE)
    }

    private fun parseCall(function: Expression): Call {
        checkNotNull(currentToken)
        val call = Call(currentToken!!, function)
        call.arguments = parseCallArguments()

        return call
    }

    private fun parseCallArguments(): List<Expression>? {
        val arguments: MutableList<Expression> = mutableListOf()
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.RIGHT_PAREN) {
            advanceTokens()
            return arguments
        }
        advanceTokens()
        parseExpression(Precedence.LOWEST)?.let {
            arguments.add(it)
        }
        while (peekToken?.tokenType == TokenType.COMMA) {
            advanceTokens()
            advanceTokens()
            parseExpression(Precedence.LOWEST)?.let {
                arguments.add(it)
            }
        }
        if (!expectedToken(TokenType.RIGHT_PAREN)) {
            return null
        }
        return arguments
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        checkNotNull(currentToken)
        val prefixParseFn = prefixParseFns[currentToken!!.tokenType] ?: run {
            val message = "Formal language error, incorrect token '${currentToken!!.literal}'"
            errors.add(message)
            return null
        }
        var leftExpression: Expression? = prefixParseFn()
        checkNotNull(peekToken)
        while (peekToken?.tokenType != TokenType.SEMI_COLON && precedence < peekPrecedence()) {
            val infixParseFn = infixParseFns[peekToken!!.tokenType]
            advanceTokens()
            checkNotNull(leftExpression)
            leftExpression = infixParseFn?.invoke(leftExpression) ?: return leftExpression
        }
        return leftExpression
    }

    private fun parseExpressionStatement(): ExpressionStatement? {
        checkNotNull(currentToken)
        val expressionStatement = ExpressionStatement(token = currentToken!!)
        expressionStatement.expression = parseExpression(Precedence.LOWEST)
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.SEMI_COLON) {
            advanceTokens()
        }
        return expressionStatement
    }

    private fun parseGroupedExpression(): Expression? {
        advanceTokens()
        val expression = parseExpression(Precedence.LOWEST)
        if (!expectedToken(TokenType.RIGHT_PAREN)) {
            return null
        }
        return expression
    }

    private fun parseFunction(): Function? {
        checkNotNull(currentToken)
        val function = Function(token = currentToken!!)
        if (!expectedToken(TokenType.LEFT_PAREN)) {
            return null
        }
        function.parameters = parseFunctionParameters()
        if (!expectedToken(TokenType.LEFT_BRACE)) {
            return null
        }
        function.body = parseBlock()
        return function
    }

    private fun parseFunctionParameters(): List<Identifier> {
        val params: MutableList<Identifier> = mutableListOf()
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.RIGHT_PAREN) {
            advanceTokens()
            return params
        }
        advanceTokens()
        checkNotNull(currentToken)
        val identifier = Identifier(token = currentToken!!, value = currentToken!!.literal)
        params.add(identifier)
        while (peekToken?.tokenType == TokenType.COMMA) {
            advanceTokens()
            advanceTokens()
            val identifier = Identifier(token = currentToken!!, value = currentToken!!.literal)
            params.add(identifier)
        }
        if (!expectedToken(TokenType.RIGHT_PAREN)) {
            return emptyList()
        }
        return params
    }

    private fun parseIdentifier(): Identifier {
        checkNotNull(currentToken)
        return Identifier(token = currentToken!!, value = currentToken!!.literal)
    }

    private fun parseIf(): If? {
        checkNotNull(currentToken)
        val ifExpression = If(token = currentToken!!)
        if (!expectedToken(TokenType.LEFT_PAREN)) {
            return null
        }
        advanceTokens()
        ifExpression.condition = parseExpression(Precedence.LOWEST)
        if (!expectedToken(TokenType.RIGHT_PAREN)) {
            return null
        }
        if (!expectedToken(TokenType.LEFT_BRACE)) {
            return null
        }
        ifExpression.consequence = parseBlock()
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.ELSE) {
            advanceTokens()
            if (!expectedToken(TokenType.LEFT_BRACE)) {
                return null
            }
            ifExpression.alternative = parseBlock()
        }
        return ifExpression
    }

    private fun parseInfixExpression(left: Expression): Infix {
        checkNotNull(currentToken)
        val infix = Infix(token = currentToken!!, operator = currentToken!!.literal, left = left)
        val precedence = currentPrecedence()
        advanceTokens()
        infix.right = parseExpression(precedence)
        return infix
    }

    private fun parseInteger(): MyInteger? {
        checkNotNull(currentToken)
        val integer = MyInteger(token = currentToken!!)
        try {
            integer.value = currentToken!!.literal.toInt()
        } catch (e: NumberFormatException) {
            val message = "Error parsing ${currentToken!!.literal} as integer"
            errors.add(message)
            return null
        }
        return integer
    }

    private fun parseLetStatement(): LetStatement? {
        checkNotNull(currentToken)
        val letStatement = LetStatement(token = currentToken!!)
        if (!expectedToken(TokenType.IDENTIFIER)) {
            return null
        }
        letStatement.name = parseIdentifier()
        if (!expectedToken(TokenType.ASSIGN)) {
            return null
        }
        advanceTokens()
        letStatement.value = parseExpression(Precedence.LOWEST)
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.SEMI_COLON) {
            advanceTokens()
        }
        return letStatement
    }

    private fun parsePrefixExpression(): Prefix {
        checkNotNull(currentToken)
        val prefixExpression = Prefix(token = currentToken!!, operator = currentToken!!.literal)
        advanceTokens()
        prefixExpression.right = parseExpression(Precedence.PREFIX)
        return prefixExpression
    }

    private fun parseReturnStatement(): ReturnStatement? {
        checkNotNull(currentToken)
        val returnStatement = ReturnStatement(token = currentToken!!)
        advanceTokens()
        returnStatement.returnValue = parseExpression(Precedence.LOWEST)
        checkNotNull(peekToken)
        if (peekToken?.tokenType == TokenType.SEMI_COLON) {
            advanceTokens()
        }
        return returnStatement
    }

    private fun parseStatement(): Statement? {
        checkNotNull(currentToken)
        return when (currentToken!!.tokenType) {
            TokenType.VAR -> parseLetStatement()
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseStringLiteral(): Expression {
        checkNotNull(currentToken)
        return StringLiteral(token = currentToken!!, value = currentToken!!.literal)
    }

    private fun peekPrecedence(): Precedence {
        checkNotNull(peekToken)
        return PRECEDENCES[peekToken!!.tokenType] ?: Precedence.LOWEST
    }

    private fun registerInfixFns(): MutableMap<TokenType, (Expression) -> Expression?> {
        return mutableMapOf(
            TokenType.PLUS to this::parseInfixExpression,
            TokenType.MINUS to this::parseInfixExpression,
            TokenType.DIVISION to this::parseInfixExpression,
            TokenType.MULTIPLICATION to this::parseInfixExpression,
            TokenType.EQUALS to this::parseInfixExpression,
            TokenType.NOT_EQUALS to this::parseInfixExpression,
            TokenType.LESS_THAN to this::parseInfixExpression,
            TokenType.GREATER_THAN to this::parseInfixExpression,
            TokenType.LEFT_PAREN to this::parseCall
        )
    }

    private fun registerPrefixFns(): MutableMap<TokenType, () -> Expression?> {
        return mutableMapOf(
            TokenType.FALSE to this::parseBoolean,
            TokenType.FUNCTION to this::parseFunction,
            TokenType.IDENTIFIER to this::parseIdentifier,
            TokenType.IF to this::parseIf,
            TokenType.INTEGER to this::parseInteger,
            TokenType.LEFT_PAREN to this::parseGroupedExpression,
            TokenType.MINUS to this::parsePrefixExpression,
            TokenType.NEGATION to this::parsePrefixExpression,
            TokenType.TRUE to this::parseBoolean,
            TokenType.STRING to this::parseStringLiteral
        )
    }
}