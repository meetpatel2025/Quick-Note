package com.training.quicknote

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
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
    val categories = Category.values().toList()

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


        // dropdown styling + items
        val dropDown = findViewById<Spinner>(R.id.categoryDropDown)
        val dropDownAdapter = object : ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        ){

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                styleText(view, position)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                styleText(view, position)
                return view
            }

            private fun styleText(view: View, position: Int) {
                val textView = view.findViewById<TextView>(android.R.id.text1)
                val category = categories[position]

                textView.text = category.displayName
                textView.setTypeface(null, Typeface.BOLD)

                val color = ContextCompat.getColor(context, category.Categorycolor)
                textView.setTextColor(color)
            }
        }

        // set the selected dropdown value
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


        // save click handle -> show the recycler view
        val taskInput = findViewById<EditText>(R.id.noteInput)
        val saveBtn = findViewById<Button>(R.id.saveNoteBtn)

        saveBtn.setOnClickListener {
            val taskDescription = taskInput.text.toString()
            if (taskDescription.isEmpty()) {
                Toast.makeText(this, R.string.task_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val task = Task(taskDescription, selectedCategory.toString())
            taskList.add(task)
            taskAdapter.notifyItemInserted(taskList.size - 1)

            taskInput.text.clear()
        }

        // sends the data to recycler view
        taskAdapter = TaskAdapter(taskList) { clickedTask ->
            val taskDetails = Intent(
                this,
                TaskDetail::class.java
            )
            taskDetails.putExtra("task_description", clickedTask.description)
            taskDetails.putExtra("task_category", clickedTask.category)
            startActivity(taskDetails)
        }
        recyclerView.adapter = taskAdapter


        val shareBtn = findViewById<Button>(R.id.shareAllBtn)


        // share button -> share all listed task
        shareBtn.setOnClickListener {

            if (taskList.isEmpty()) {
                Toast.makeText(this, "No tasks to share", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shareText = StringBuilder()

            // message formmat
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

        val previewButton = findViewById<Button>(R.id.previewBtn)

        previewButton.setOnClickListener {
            if(taskList.isEmpty()){
                Toast.makeText(this, " No notes to preview", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lastTast = taskList.last()

            val lastTaskDetail = Intent(this, TaskDetail::class.java)
            lastTaskDetail.putExtra("task_description" , lastTast.description)
            lastTaskDetail.putExtra("task_category" , lastTast.category)

            startActivity(lastTaskDetail)
        }
    }

}
