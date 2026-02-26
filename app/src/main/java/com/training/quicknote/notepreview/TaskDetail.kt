package com.training.quicknote.notepreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class TaskDetail : ComponentActivity(){

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val description = intent.getStringExtra("task_desc") ?: ""
            val category = intent.getStringExtra("task_category") ?: ""

            setContent {
                NoteDetail(
                    taskTitle = category,
                    taskDescription = description,
                    onBackClick = { finish() }
                )
            }
        }
}
