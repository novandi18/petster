package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.MAX_COMMENT_LENGTH
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun PostCommentField(
    modifier: Modifier = Modifier,
    onSubmit: (String) -> Unit = {},
    replyOnCommentId: String? = null,
    replyOnCommentAuthorName: String? = null,
    onCancelReply: () -> Unit = {}
) {
    var comment by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val isCommentValid = comment.isNotBlank() && comment.length <= MAX_COMMENT_LENGTH
    val charactersRemaining = MAX_COMMENT_LENGTH - comment.length
    val isNearLimit = charactersRemaining <= 100
    val isReplyMode = replyOnCommentId != null && replyOnCommentAuthorName != null

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp,
                    top = if (isReplyMode) 0.dp else 12.dp
                )
        ) {
            if (isReplyMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Reply,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = stringResource(
                            R.string.replying_to,
                            replyOnCommentAuthorName
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = onCancelReply) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = {
                    if (it.length <= MAX_COMMENT_LENGTH) {
                        comment = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = if (isReplyMode)
                            stringResource(R.string.write_reply_hint, "@${replyOnCommentAuthorName}")
                        else
                            stringResource(R.string.write_comment_hint),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .04f),
                    focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .08f),
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (isCommentValid) {
                            onSubmit(comment)
                            comment = ""
                        }
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            )

            if (comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$charactersRemaining ${stringResource(R.string.characters_remaining)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            charactersRemaining < 0 -> MaterialTheme.colorScheme.error
                            isNearLimit -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        },
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (isCommentValid) {
                                onSubmit(comment)
                                comment = ""
                                focusManager.clearFocus()
                            }
                        },
                        enabled = isCommentValid
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = stringResource(R.string.send_comment),
                            tint = if (isCommentValid) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PostCommentFieldPreview() {
    PetsterTheme {
        PostCommentField(
            replyOnCommentId = "1",
            replyOnCommentAuthorName = "Novandi Ramadhan"
        )
    }
}