package andres.rangel.compilerxd.ui.screens

import andres.rangel.compilerxd.data.model.Screen
import andres.rangel.compilerxd.utils.Evaluator
import andres.rangel.compilerxd.utils.Lexer
import andres.rangel.compilerxd.utils.ObjectEnvironment
import andres.rangel.compilerxd.utils.Parser
import andres.rangel.compilerxd.utils.Token
import andres.rangel.compilerxd.utils.Token.Companion.ERROR_LIST
import andres.rangel.compilerxd.utils.Token.Companion.OUTPUT
import andres.rangel.compilerxd.utils.Token.Companion.SOURCE_CODE
import andres.rangel.compilerxd.utils.Token.Companion.TOKEN_LIST
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SourceCodeFragment(navController: NavHostController) {
    val textState = rememberSaveable { mutableStateOf(SOURCE_CODE) }

    Column {
        TopAppBar(
            title = { Text(text = "CompilerXD") },
            actions = {
                IconButton(onClick = {
                    TOKEN_LIST.clear()
                    ERROR_LIST.clear()
                    OUTPUT = "Build successful"
                    performPlayAction(textState.value)
                    navController.navigate(Screen.TokensScreen.route)
                }) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                }
            }
        )
        CodeEditor(textState.value) { newText ->
            textState.value = newText
            SOURCE_CODE = newText
        }
    }
}

@Composable
fun CodeEditor(
    text: String,
    onTextChanged: (String) -> Unit
) {
    var lines by rememberSaveable { mutableStateOf(text.lines()) }

    Box {
        BasicTextField(
            value = text,
            onValueChange = {
                onTextChanged(it)
                lines = it.lines()
            },
            textStyle = TextStyle.Default.copy(
                color = Color.Gray,
                fontSize = 18.sp,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp)
        )
        LineNumbers(lines.size)
    }
}

fun performPlayAction(code: String) {
    val lexer = Lexer(code)
    val parser = Parser(lexer)
    val program = parser.parseProgram()
    val environment = ObjectEnvironment()
    TOKEN_LIST.removeLast()
    if (parser.errorsList.isNotEmpty()) {
        ERROR_LIST.addAll(parser.errorsList)
    }
    val evaluated = Evaluator().evaluate(program, environment)
    if (evaluated != null) {
        OUTPUT = evaluated.inspect()
    }
}

@Composable
private fun LineNumbers(lineCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 1.dp, end = 4.dp, top = 1.dp)
    ) {
        repeat(lineCount) { lineNumber ->
            LineNumberItem(lineNumber + 1)
        }
    }
}

@Composable
private fun LineNumberItem(lineNumber: Int) {
    Text(
        text = lineNumber.toString(),
        style = TextStyle.Default.copy(
            color = Color.Black,
            fontSize = 16.sp
        ),
        modifier = Modifier.padding(end = 4.dp)
    )
}