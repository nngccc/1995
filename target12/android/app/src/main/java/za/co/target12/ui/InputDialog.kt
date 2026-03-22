package za.co.target12.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import za.co.target12.GamePhase
import za.co.target12.GameState

@Composable
fun InputDialog(state: GameState, onDone: () -> Unit) {
    val label = when (state.phase) {
        GamePhase.INPUT_NAME -> "ENTER YOUR NAME"
        GamePhase.INPUT_TEAM -> "ENTER YOUR TEAM"
        GamePhase.INPUT_COMP -> "ENTER THE COMPETITION"
        else -> ""
    }
    val maxLen = when (state.phase) {
        GamePhase.INPUT_NAME -> 15
        GamePhase.INPUT_TEAM -> 6
        GamePhase.INPUT_COMP -> 11
        else -> 15
    }
    val currentValue = when (state.phase) {
        GamePhase.INPUT_NAME -> state.playerName
        GamePhase.INPUT_TEAM -> state.playerTeam
        GamePhase.INPUT_COMP -> state.playerComp
        else -> ""
    }

    var text by remember(state.phase) { mutableStateOf(currentValue) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.phase) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0f, 0f, 80f / 255f, 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                value = text,
                onValueChange = { if (it.length <= maxLen) text = it },
                modifier = Modifier
                    .width(240.dp)
                    .background(Color(0f, 0.4f, 0f))
                    .border(2.dp, Color(0f, 0.67f, 0f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester)
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.Enter -> {
                                    when (state.phase) {
                                        GamePhase.INPUT_NAME -> state.playerName = text
                                        GamePhase.INPUT_TEAM -> state.playerTeam = text
                                        GamePhase.INPUT_COMP -> state.playerComp = text
                                        else -> {}
                                    }
                                    onDone(); true
                                }
                                Key.Escape -> { onDone(); true }
                                else -> false
                            }
                        } else false
                    },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color(0f, 1f, 0f)),
                singleLine = true
            )
            androidx.compose.material3.Text(
                text = "Press Enter to confirm",
                color = Color(0.53f, 0.53f, 0.53f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
