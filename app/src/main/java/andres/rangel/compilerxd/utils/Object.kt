package andres.rangel.compilerxd.utils

enum class ObjectType {
    BOOLEAN,
    BUILTIN,
    ERROR,
    FUNCTION,
    INTEGER,
    NULL,
    RETURN,
    STRING
}

interface Object {
    fun type(): ObjectType
    fun inspect(): String
}

class ObjectInteger(val value: Int) : Object {
    override fun type(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class ObjectBoolean(val value: Boolean) : Object {
    override fun type(): ObjectType {
        return ObjectType.BOOLEAN
    }

    override fun inspect(): String {
        return if (value) "true" else "false"
    }
}

class ObjectNull : Object {
    override fun type(): ObjectType {
        return ObjectType.NULL
    }

    override fun inspect(): String {
        return "null"
    }
}

class ObjectReturn(val value: Object) : Object {
    override fun type(): ObjectType {
        return ObjectType.RETURN
    }

    override fun inspect(): String {
        return value.inspect()
    }
}

class ObjectError(val message: String) : Object {
    override fun type(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "Error: $message"
    }
}

class ObjectEnvironment(private val outer: ObjectEnvironment? = null) : HashMap<String, Object>(),
    Object {
    override fun type(): ObjectType {
        throw UnsupportedOperationException("Environment has no ObjectType")
    }

    override fun inspect(): String {
        return "Environment"
    }

    override fun get(key: String): Object? {
        try {
            return super.get(key)
        } catch (e: NoSuchElementException) {
            if (outer != null) {
                return outer[key]
            }
            throw e
        }
    }
}

class ObjectFunction(
    val parameters: List<Identifier>,
    val body: Block,
    val env: ObjectEnvironment
) : Object {
    override fun type(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        val params = parameters.joinToString(", ") { it.toString() }
        return "function($params) {\n$body\n}"
    }
}

class ObjectString(val value: String) : Object {
    override fun type(): ObjectType {
        return ObjectType.STRING
    }

    override fun inspect(): String {
        return value
    }
}

typealias BuiltinFunction = (args: List<Object>) -> Object

class BuiltIn(val function: BuiltinFunction) : Object {

    override fun type(): ObjectType {
        return ObjectType.BUILTIN
    }

    override fun inspect(): String {
        return "builtin function"
    }
}