package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.utils.BuiltIns.Companion.BUILTINS

class Evaluator {

    val trueValue: ObjectBoolean = ObjectBoolean(true)
    val falseValue: ObjectBoolean = ObjectBoolean(false)
    val nullValue: ObjectNull = ObjectNull()

    fun evaluate(node: ASTNode, env: ObjectEnvironment): Object? {
        return when (node::class.java) {
            Program::class.java -> {
                val programNode = node as Program
                evaluateProgram(programNode, env)
            }

            ExpressionStatement::class.java -> {
                val expressionNode = node as ExpressionStatement
                expressionNode.expression?.let { evaluate(it, env) }
            }

            MyInteger::class.java -> {
                val integerNode = node as MyInteger
                integerNode.value?.let { ObjectInteger(it) }
            }

            MyBoolean::class.java -> {
                val booleanNode = node as MyBoolean
                booleanNode.value?.let { ObjectBoolean(it) }
            }

            Prefix::class.java -> {
                val prefixNode = node as Prefix
                prefixNode.right?.let { right ->
                    val evaluatedRight = evaluate(right, env)
                    evaluatedRight?.let { evaluatePrefixExpression(prefixNode.operator, it) }
                }
            }

            Infix::class.java -> {
                val infixNode = node as Infix
                infixNode.left.let { left ->
                    infixNode.right?.let { right ->
                        val evaluatedLeft = evaluate(left, env)
                        val evaluatedRight = evaluate(right, env)
                        if (evaluatedLeft != null && evaluatedRight != null) {
                            evaluateInfixExpression(
                                infixNode.operator,
                                evaluatedLeft,
                                evaluatedRight
                            )
                        } else {
                            null
                        }
                    }
                }
            }

            Block::class.java -> {
                val blockNode = node as Block
                evaluateBlockStatement(blockNode, env)
            }

            If::class.java -> {
                val ifNode = node as If
                evaluateIfExpression(ifNode, env)
            }

            ReturnStatement::class.java -> {
                val returnNode = node as ReturnStatement
                returnNode.returnValue?.let { value ->
                    val evaluatedValue = evaluate(value, env)
                    evaluatedValue?.let { ObjectReturn(it) }
                }
            }

            LetStatement::class.java -> {
                val letNode = node as LetStatement
                letNode.value?.let { value ->
                    val evaluatedValue = evaluate(value, env)
                    letNode.name?.let { name ->
                        if (evaluatedValue != null) {
                            env[name.value] = evaluatedValue
                        }
                    }
                }
                null
            }

            Identifier::class.java -> {
                val identifierNode = node as Identifier
                evaluateIdentifier(identifierNode, env)
            }

            Function::class.java -> {
                val functionNode = node as Function
                ObjectFunction(functionNode.parameters, functionNode.body!!, env)
            }

            Call::class.java -> {
                val callNode = node as Call
                val function = evaluate(callNode.function, env)
                callNode.arguments?.let { args ->
                    val evaluatedArgs = evaluateExpression(args, env)
                    function?.let { applyFunction(it, evaluatedArgs) }
                }
            }

            StringLiteral::class.java -> {
                val stringNode = node as StringLiteral
                ObjectString(stringNode.value)
            }

            else -> null
        }
    }

    private fun applyFunction(fn: Object, args: List<Object>): Object {
        return when (fn) {
            is ObjectFunction -> {
                val extendedEnvironment = extendFunctionEnvironment(fn, args)
                val evaluated = evaluate(fn.body, extendedEnvironment)
                unwrapReturnValue(evaluated!!)
            }

            is BuiltIn -> fn.function(args)
            else -> newError(NOT_A_FUNCTION, listOf(fn.type().name))
        }
    }

    private fun extendFunctionEnvironment(
        fn: ObjectFunction,
        args: List<Object>
    ): ObjectEnvironment {
        val env = ObjectEnvironment(outer = fn.env)

        for (idx in fn.parameters.indices) {
            val param = fn.parameters[idx]
            if (idx < args.size) {
                env[param.value] = args[idx]
            }
        }

        return env
    }

    private fun unwrapReturnValue(obj: Object): Object {
        return if (obj is ObjectReturn) {
            obj.value
        } else {
            obj
        }
    }

    private fun evaluateProgram(program: Program, env: ObjectEnvironment): Object? {
        var result: Object? = null

        for (statement in program.statements) {
            result = evaluate(statement, env)
            if (result is ObjectReturn) {
                return result.value
            } else if (result is Error) {
                return result
            }
        }

        return result
    }

    private fun evaluateBangOperatorExpression(right: Object): Object {
        return when (right.inspect()) {
            trueValue.inspect() -> falseValue
            falseValue.inspect() -> trueValue
            nullValue.inspect() -> trueValue
            else -> falseValue
        }
    }

    private fun evaluateBlockStatement(block: Block, env: ObjectEnvironment): Object? {
        var result: Object? = null

        for (statement in block.statements) {
            result = evaluate(statement, env)

            if (result != null && (result.type() == ObjectType.RETURN || result.type() == ObjectType.ERROR)) {
                return result
            }
        }

        return result
    }

    fun evaluateExpression(expressions: List<Expression>, env: ObjectEnvironment): List<Object> {
        val result: MutableList<Object> = mutableListOf()

        for (expression in expressions) {
            val evaluated = evaluate(expression, env)
            evaluated?.let { result.add(it) }
        }

        return result
    }

    private fun evaluateIdentifier(node: Identifier, env: ObjectEnvironment): Object? {
        val valueInEnv = env.getOrDefault(node.value, null)
        return valueInEnv ?: BUILTINS.getOrDefault(node.value, newError(UNKNOWN_IDENTIFIER, listOf(node.value)))
    }

    private fun evaluateIfExpression(ifExpression: If, env: ObjectEnvironment): Object? {
        val condition = evaluate(ifExpression.condition!!, env)

        return if (isTruthy(condition!!)) {
            ifExpression.consequence?.let { evaluate(it, env) }
        } else {
            ifExpression.alternative?.let { evaluate(it, env) }
        }
    }

    private fun isTruthy(obj: Object): Boolean {
        return when (obj) {
            nullValue -> false
            trueValue -> true
            falseValue -> false
            else -> true
        }
    }

    private fun evaluateInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left.type() == ObjectType.INTEGER && right.type() == ObjectType.INTEGER) {
            return evaluateIntegerInfixExpression(operator, left, right)
        } else if (left.type() == ObjectType.STRING && right.type() == ObjectType.STRING) {
            return evaluateStringInfixExpression(operator, left, right)
        } else if (operator == "==") {
            return toBooleanObject(left.inspect() == right.inspect())
        } else if (operator == "!=") {
            return toBooleanObject(left.inspect() != right.inspect())
        } else if (left.type() != right.type()) {
            return newError(TYPE_MISMATCH, listOf(left.type().name, operator, right.type().name))
        } else {
            return newError(
                UNKNOWN_INFIX_OPERATOR,
                listOf(left.type().name, operator, right.type().name)
            )
        }
    }

    private fun evaluateStringInfixExpression(
        operator: String,
        left: Object,
        right: Object
    ): Object {
        val leftValue = (left as ObjectString).value
        val rightValue = (right as ObjectString).value

        return when (operator) {
            "+" -> ObjectString(leftValue + rightValue)
            "==" -> toBooleanObject(leftValue == rightValue)
            "!=" -> toBooleanObject(leftValue != rightValue)
            else -> newError(
                UNKNOWN_INFIX_OPERATOR,
                listOf(left.type().name, operator, right.type().name)
            )
        }
    }

    private fun evaluateIntegerInfixExpression(
        operator: String,
        left: Object,
        right: Object
    ): Object {
        val leftValue = (left as ObjectInteger).value
        val rightValue = (right as ObjectInteger).value

        return when (operator) {
            "+" -> ObjectInteger(leftValue + rightValue)
            "-" -> ObjectInteger(leftValue - rightValue)
            "*" -> ObjectInteger(leftValue * rightValue)
            "/" -> ObjectInteger(leftValue / rightValue)
            "<" -> toBooleanObject(leftValue < rightValue)
            ">" -> toBooleanObject(leftValue > rightValue)
            "==" -> toBooleanObject(leftValue == rightValue)
            "!=" -> toBooleanObject(leftValue != rightValue)
            else -> newError(
                UNKNOWN_INFIX_OPERATOR,
                listOf(left.type().name, operator, right.type().name)
            )
        }
    }

    private fun evaluateMinusOperatorExpression(right: Object): Object {
        return if (right is ObjectInteger) {
            ObjectInteger(-right.value)
        } else {
            newError(UNKNOWN_PREFIX_OPERATOR, listOf("-", right.type().name))
        }
    }

    private fun evaluatePrefixExpression(operator: String, right: Object): Object {
        return when (operator) {
            "!" -> evaluateBangOperatorExpression(right)
            "-" -> evaluateMinusOperatorExpression(right)
            else -> newError(UNKNOWN_PREFIX_OPERATOR, listOf(operator, right.type().name))
        }
    }

    private fun newError(message: String, args: List<Any>): ObjectError {
        return ObjectError("$message${args.toString().replace(Regex("[\\[\\],]"), "")}")
    }

    private fun toBooleanObject(value: Boolean): ObjectBoolean {
        return if (value) trueValue else falseValue
    }

    companion object {
        const val NOT_A_FUNCTION: String = "Error not a function: "
        const val TYPE_MISMATCH: String = "Error type mismatch: "
        const val UNKNOWN_PREFIX_OPERATOR: String = "Error unknown operator: "
        const val UNKNOWN_INFIX_OPERATOR: String = "Error unknown operator: "
        const val UNKNOWN_IDENTIFIER: String = "Error identifier not found: "
    }

}