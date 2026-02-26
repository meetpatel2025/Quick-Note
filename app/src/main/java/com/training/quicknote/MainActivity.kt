package com.training.quicknote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.training.quicknote.adapter.TaskAdapter
import com.training.quicknote.datamodel.Task
import com.training.quicknote.notepreview.TaskDetail
import com.training.quicknote.util.Category

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var taskAdapter: TaskAdapter
    val taskList = ArrayList<Task>()
    var selectedCategory = Category.PERSONAL
    val categories = listOf(Category.PERSONAL, Category.WORK, Category.STUDY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.noteList)
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )


        val dropDown = findViewById<Spinner>(R.id.categoryDropDown)
        val dropDownAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        dropDown.adapter = dropDownAdapter
        dropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = Category.PERSONAL
            }
        }


        val taskInput = findViewById<EditText>(R.id.noteInput)
        val saveBtn = findViewById<Button>(R.id.saveNoteBtn)


        saveBtn.setOnClickListener {
            val taskDescription = taskInput.text.toString()
            Log.d("$taskDescription", "Error")
            if (taskDescription.isEmpty()) {
                Toast.makeText(this, R.string.task_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val task = Task(taskDescription, selectedCategory.toString())
            taskList.add(task)
            taskAdapter.notifyItemInserted(taskList.size - 1)

            taskInput.text.clear()
        }

        taskAdapter = TaskAdapter(taskList) { clickedTask ->
            val taskDetails = Intent(
                this,
                TaskDetail::class.java
            )
            taskDetails.putExtra("task_Description", clickedTask.description)
            taskDetails.putExtra("task_category", clickedTask.category)
            startActivity(taskDetails)
        }
        recyclerView.adapter = taskAdapter


        val shareBtn = findViewById<Button>(R.id.shareAllBtn)

        shareBtn.setOnClickListener {

            if (taskList.isEmpty()) {
                Toast.makeText(this, "No tasks to share", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shareText = StringBuilder()

            for (task in taskList) {
                shareText.append("-------- My Task --------\n")
                shareText.append("Category: ${task.category}\n")
                shareText.append("Task: ${task.description}\n\n\n")
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText.toString())

            startActivity(Intent.createChooser(intent, "Share notes via"))
        }
    }
}
