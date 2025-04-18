package com.novandiramadhan.petster.common.dummy

import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.Article

object ArticleData {
    val dummyArticles = listOf(
        Article(
            id = 1,
            titleResId = R.string.article_1_title,
            contentResId = R.string.article_1_content,
            imageResId = R.drawable.article_1_image
        ),
        Article(
            id = 2,
            titleResId = R.string.article_2_title,
            contentResId = R.string.article_2_content,
            imageResId = R.drawable.article_2_image
        ),
        Article(
            id = 3,
            titleResId = R.string.article_3_title,
            contentResId = R.string.article_3_content,
            imageResId = R.drawable.article_3_image
        ),
        Article(
            id = 4,
            titleResId = R.string.article_4_title,
            contentResId = R.string.article_4_content,
            imageResId = R.drawable.article_4_image
        ),
        Article(
            id = 5,
            titleResId = R.string.article_5_title,
            contentResId = R.string.article_5_content,
            imageResId = R.drawable.article_5_image
        )
    )
}