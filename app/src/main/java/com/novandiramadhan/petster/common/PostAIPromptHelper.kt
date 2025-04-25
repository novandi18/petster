package com.novandiramadhan.petster.common

import android.content.Context
import com.novandiramadhan.petster.R

object PostAIPromptHelper {
    fun getPromptForOption(context: Context, selectedOption: String, postText: String): String {
        val aiOptions = context.resources.getStringArray(R.array.create_community_post_ai)
        val aiPromptTemplates = context.resources.getStringArray(R.array.create_community_post_ai_prompt)

        val promptTemplate = when (selectedOption) {
            aiOptions[0] -> aiPromptTemplates[0]
            aiOptions[1] -> aiPromptTemplates[1]
            aiOptions[2] -> aiPromptTemplates[2]
            aiOptions[3] -> aiPromptTemplates[3]
            aiOptions[4] -> aiPromptTemplates[4]
            else -> "Tolong tinjau dan tingkatkan kualitas teks berikut: %s"
        }

        return String.format(promptTemplate, postText)
    }
}