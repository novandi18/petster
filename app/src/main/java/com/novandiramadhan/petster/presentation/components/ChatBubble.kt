package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.Chat
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatBubble(
    message: Chat,
    onRetryClicked: (Chat) -> Unit,
    isRetryShow: Boolean,
    isRetrying: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .alpha(if (isRetrying) 0.6f else 1f),
        horizontalArrangement = if (message.direction) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.direction) 16.dp else 0.dp,
                        bottomEnd = if (message.direction) 0.dp else 16.dp
                    )
                )
                .background(
                    if (message.direction) LimeGreen
                    else MaterialTheme.colorScheme.surface
                )
                .padding(12.dp)
        ) {
            MarkdownText(
                modifier = Modifier.padding(4.dp),
                markdown = message.message,
                style = TextStyle(
                    color = if (message.direction) Black
                    else MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    lineHeight = 20.sp
                ),
                fontResource = R.font.albert_sans
            )

            if (!message.direction && isRetryShow) {
                IconButton(
                    onClick = { onRetryClicked(message) },
                    modifier = Modifier.size(36.dp).padding(top = 8.dp),
                    enabled = !isRetrying
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.retry),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatBubblePreview() {
    PetsterTheme {
        ChatBubble(
            message = Chat(
                userId = "1",
                message = stringResource(R.string.default_prompt_assistant),
                direction = false,
                role = "assistant",
            ),
            onRetryClicked = {},
            isRetryShow = true,
            isRetrying = false
        )
    }
}