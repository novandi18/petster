package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.ArticleData
import com.novandiramadhan.petster.presentation.components.ArticleCard
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    navigateTo: (Destinations) -> Unit,
) {
    val articles = ArticleData.dummyArticles

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.pet_tips_screen_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles.size) { index ->
                ArticleCard(
                    article = articles[index],
                    onClick = { selectedArticle ->
                        navigateTo(
                            Destinations.ArticleDetail(
                                id = selectedArticle.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetTipsScreenPreview() {
    PetsterTheme {
        ArticleScreen(
            navigateTo = {}
        )
    }
}