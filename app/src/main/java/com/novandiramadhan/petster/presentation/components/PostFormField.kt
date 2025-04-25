package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun PostFormField(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isDisabled: Boolean = false,
    maxCharacters: Int = 1000,
) {
    val charactersRemaining = maxCharacters - value.length

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            value = value,
            onValueChange = {
                if (it.length <= maxCharacters) {
                    onValueChange(it)
                }
            },
            enabled = !isDisabled,
            placeholder = {
                Text(
                    text = stringResource(R.string.new_post_field_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(.7f)
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, bottom = 16.dp),
            text = "$charactersRemaining ${stringResource(R.string.characters_remaining)}",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                charactersRemaining < 20 -> MaterialTheme.colorScheme.error
                charactersRemaining < 50 -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostFormFieldPreview() {
    PetsterTheme {
        var text by remember { mutableStateOf("This is a preview of a post form field that allows users to enter text for their posts. It has a character limit and shows how many characters are remaining. As the text grows, the field will expand to accommodate more content.") }
        PostFormField(
            value = text,
            onValueChange = { text = it },
        )
    }
}

@Preview(showBackground = true, name = "Long Text")
@Composable
fun LongTextPostFormFieldPreview() {
    PetsterTheme {
        var text by remember { mutableStateOf(
            """This is a very long post with multiple paragraphs to demonstrate how the text field expands.
                |
                |As you can see, when the content gets longer, the field grows taller to accommodate all the text without requiring the user to scroll inside the field itself.
                |
                |This makes it easier for users to see all of their content at once while composing a post. The character counter at the bottom keeps track of how many characters are still available.
                |
                |The minimum height ensures the field is still usable when empty or contains very little text.
            """.trimMargin()
        ) }
        PostFormField(
            value = text,
            onValueChange = { text = it },
        )
    }
}