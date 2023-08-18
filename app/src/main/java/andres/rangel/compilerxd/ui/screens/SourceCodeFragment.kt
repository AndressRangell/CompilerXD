package andres.rangel.compilerxd.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compilerTools.Functions

@Composable
fun SourceCodeFragment() {
    Column {
        TopAppBar(
            title = { Text(text = "Nombre del Archivo") },
            actions = {
                IconButton(onClick = { /* Acción de configuración */ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )
        CodeEditor()
    }
}

@Composable
fun CodeEditor() {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.padding(0.dp, 5.dp)
    ) {
        LineNumberedTextField(text = text) {
            text = it
        }
    }
}

@Composable
fun LineNumberedTextField(
    text: String,
    onTextChanged: (String) -> Unit
) {
    var lines by remember(text) { mutableStateOf(text.lines()) }

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