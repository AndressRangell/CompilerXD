package andres.rangel.compilerxd.utils

class BuiltIns {

    companion object {
        private fun longitud(args: List<Object>): Object {
            return if (args.size != 1) {
                ObjectError("$WRONG_NUMBER_OF_ARGS")
            } else if (args[0] is ObjectString) {
                val argument = args[0] as ObjectString
                ObjectInteger(argument.value.length)
            } else {
                ObjectError(UNSUPPORTED_ARGUMENT_TYPE)
            }
        }

        const val UNSUPPORTED_ARGUMENT_TYPE = "Error argument not supported"
        const val WRONG_NUMBER_OF_ARGS = "Error incorrect number of arguments"
        val BUILTINS: Map<String, BuiltIn> = mapOf(
            "length" to BuiltIn(::longitud)
        )
    }

}