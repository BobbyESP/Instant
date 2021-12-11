package com.ebf.instant.ui.comment

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.ui.theme.InstantTheme
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CommentScreen(
    postId: String,
    onBack: () -> Unit,
    viewModel: CommentScreenViewModel = getViewModel { parametersOf(postId) }
) {
    val viewState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Comments") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Rounded.ArrowBackIos, contentDescription = "Back button")
                    }
                }
            )
        }
    ) { innerPadding ->
        CommentScreenContent(
            comments = viewState.comments,
            modifier = Modifier.padding(innerPadding),
            onCommentSend = { message ->
                viewModel.comment(postId, message)
            }
        )
    }
}

@Composable
fun CommentScreenContent(
    comments: List<CommentWithUser>,
    onCommentSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Comments(
            comments = comments,
            modifier = Modifier.weight(1f)
        )
        UserInput { onCommentSend(it) }
    }
}

@Composable
fun Comments(comments: List<CommentWithUser>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        comments.forEach { commentWithUser ->
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(commentWithUser.user.username)
                }
                append(" ")
                append(commentWithUser.comment.content)
            }, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun UserInput(
    onMessageSend: (String) -> Unit
) {

    var textState by remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current

    fun onSend() {
        onMessageSend(textState.text)
        // Reset text field and close keyboard
        focusManager.clearFocus()
        textState = TextFieldValue()
    }

    Divider()

    Surface(modifier = Modifier.height(48.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                var focusState by remember { mutableStateOf(false) }
                BasicTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onSend() }
                    ),
                    textStyle = TextStyle.Default.copy(color = MaterialTheme.colors.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .onFocusChanged { state ->
                            focusState = state.isFocused
                        },
                )

                if (textState.text.isEmpty() && !focusState) {
                    val disableContentColor =
                        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                    Text(
                        text = "Write a comment...",
                        style = MaterialTheme.typography.body1.copy(color = disableContentColor),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )
                }
            }
            IconButton(
                enabled = textState.text.isNotBlank(),
                onClick = { onSend() }
            ) {
                Icon(Icons.Rounded.Send, contentDescription = "Send button")
            }
        }

    }
}

@Preview(name = "User input light")
@Preview(name = "User input dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun UserInputPreview() {
    InstantTheme {
        UserInput(onMessageSend = {})
    }
}

@Preview
@Composable
fun CommentScreenPreview() {
    InstantTheme {
        CommentScreenContent(comments = listOf(), onCommentSend = {})
    }
}