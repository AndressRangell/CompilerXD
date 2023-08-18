package andres.rangel.compilerxd.ui.screens

import andres.rangel.compilerxd.data.model.TokenType
import andres.rangel.compilerxd.ui.theme.RedCard
import andres.rangel.compilerxd.utils.Token.Companion.TOKEN_LIST
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TokensFragment() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text(text = "Tokens") }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(TOKEN_LIST.size) { index ->
                val token = TOKEN_LIST[index]
                TokenItem(token.tokenType, token.literal)
            }
        }
    }
}

@Composable
fun TokenItem(type: TokenType, literal: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = type.toString(),
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = if (type == TokenType.ILLEGAL) RedCard else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = literal,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = if (type == TokenType.ILLEGAL) RedCard else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}