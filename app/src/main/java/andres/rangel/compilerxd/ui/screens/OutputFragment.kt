package andres.rangel.compilerxd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun OutputFragment() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(title = { Text(text = "Output") })
        TextField(
            value = "Here is the output code.",
            onValueChange = { },
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.fillMaxSize(),
            maxLines = Int.MAX_VALUE,
            readOnly = true
        )
    }
}