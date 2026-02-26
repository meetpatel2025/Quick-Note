package com.training.quicknote.notepreview

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class TaskDetail : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val description = intent.getStringExtra("task_description") ?: ""
        val category = intent.getStringExtra("task_category") ?: ""

        setContent {
            NoteDetail(
                taskTitle = category,
                taskDescription = description,
                onBackClick = { finish() },
                onShareClick = {
                    shareNote(category, description)
                }
            )
        }

    }
         private fun shareNote(category: String, description: String) {

            val shareText = """
            -------- My Task --------
            Category: $category
            Task: $description
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(intent, "Share note via"))
        }
}
