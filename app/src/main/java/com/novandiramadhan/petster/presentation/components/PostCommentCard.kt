package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.MAX_LINES_COLLAPSED
import com.novandiramadhan.petster.common.utils.capitalize
import com.novandiramadhan.petster.common.utils.formatPostDate
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.presentation.ui.theme.Blue
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import java.util.Date

@Composable
fun PostCommentCard(
    data: PostComment,
    replyAuthorName: String? = null,
    onReply: () -> Unit = {},
) {
    val authorName = when (val author = data.author) {
        is UserResult.ShelterResult -> author.shelter.name ?: "Unknown Shelter"
        is UserResult.VolunteerResult -> author.volunteer.name ?: "Unknown Volunteer"
        else -> "Unknown User"
    }
    val authorType = data.authorType ?: "unknown"
    val commentText = data.comment ?: ""
    val commentDate = data.createdAt
    var isExpanded by remember { mutableStateOf(false) }
    var canExpand by remember { mutableStateOf(false) }

    val showMoreText = stringResource(if (isExpanded) R.string.show_less else R.string.show_more)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = authorName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = authorType.capitalize(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                commentDate?.let {
                    Text(
                        text = formatPostDate(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(.7f)
                    )
                }

                Box(modifier = Modifier.padding(top = 4.dp, end = 6.dp)) {
                    Column {
                        val prefix = if (replyAuthorName.isNullOrBlank()) "" else "@$replyAuthorName "
                        Text(
                            text = buildAnnotatedString {
                                append(prefix + commentText)
                                if (canExpand) {
                                    append(" ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = Blue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(showMoreText)
                                    }
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = if (isExpanded) Int.MAX_VALUE else MAX_LINES_COLLAPSED,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textLayoutResult ->
                                if (!isExpanded) {
                                    canExpand = textLayoutResult.hasVisualOverflow
                                }
                            },
                            modifier = Modifier.clickable(
                                enabled = canExpand,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (canExpand) {
                                    isExpanded = !isExpanded
                                }
                            }
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .clickable(onClick = onReply),
                    text = stringResource(R.string.reply),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPostCommentCard() {
    PetsterTheme {
        PostCommentCard(
            data = PostComment(
                id = "1",
                authorId = "1",
                authorType = "volunteer",
                comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                replyToCommentId = null,
                author = UserResult.ShelterResult(
                    Shelter(
                        uuid = "shelter123",
                        name = "Happy Paws Shelter"
                    )
                ),
                createdAt = Date()
            ),
            replyAuthorName = "Ali"
        )
    }
}