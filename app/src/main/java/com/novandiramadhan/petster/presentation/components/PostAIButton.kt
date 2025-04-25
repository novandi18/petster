package com.novandiramadhan.petster.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.icons.starsIcon
import com.novandiramadhan.petster.presentation.ui.theme.Blue
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.TealGreen

@Composable
fun PostAIButton(
    text: String = "",
    isLoading: Boolean = false,
    isDisabled: Boolean = false,
    onClick: () -> Unit = {},
) {
    val transition = rememberInfiniteTransition(label = "animations")
    val translateAnim = transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translate"
    )
    val shimmerAnim = transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    val isButtonEnabled = !isLoading && !isDisabled

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .drawWithCache {
                val gradientWidth = size.width * 3

                val normalGradient = Brush.horizontalGradient(
                    colors = listOf(
                        if (isDisabled) Blue.copy(alpha = 0.4f) else Blue,
                        if (isDisabled) TealGreen.copy(alpha = 0.4f) else TealGreen
                    ),
                    startX = 0f,
                    endX = size.width
                )

                val loadingGradient = Brush.horizontalGradient(
                    colors = listOf(TealGreen, Blue, TealGreen, Blue),
                    startX = size.width * translateAnim.value,
                    endX = gradientWidth + (size.width * translateAnim.value)
                )

                onDrawWithContent {
                    drawRect(brush = if (isLoading) loadingGradient else normalGradient)
                    drawContent()

                    if (isLoading) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.0f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.0f)
                                ),
                                startX = size.width * shimmerAnim.value - (size.width / 3),
                                endX = size.width * shimmerAnim.value + (size.width / 3)
                            )
                        )
                    }
                }
            }
            .clickable(enabled = isButtonEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = starsIcon,
                contentDescription = null,
                tint = if (isDisabled) Color.White.copy(alpha = 0.6f) else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isDisabled) Color.White.copy(alpha = 0.6f) else Color.White
            )
        }
    }
}

@Preview("Normal")
@Composable
private fun PostAIButtonPreview() {
    PetsterTheme {
        PostAIButton(
            text = stringArrayResource(R.array.create_community_post_ai).first()
        )
    }
}

@Preview("Loading")
@Composable
private fun PostAIButtonLoadingPreview() {
    PetsterTheme {
        PostAIButton(
            text = stringArrayResource(R.array.create_community_post_ai).first(),
            isLoading = true
        )
    }
}