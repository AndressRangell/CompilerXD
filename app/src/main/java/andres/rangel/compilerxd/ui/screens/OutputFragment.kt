package andres.rangel.compilerxd.ui.screens

import andres.rangel.compilerxd.utils.Token.Companion.ERROR_LIST
import andres.rangel.compilerxd.utils.Token.Companion.OUTPUT
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun OutputFragment() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(title = { Text(text = "Output") })
        Text(
            text = if(ERROR_LIST.isEmpty()) OUTPUT else "Build failed",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}