package com.training.quicknote

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.training.quicknote.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.observeOn

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var taskAdapter: TaskAdapter
    val taskList = ArrayList<Task>()
    var selectedCategory = Category.WORK
    val categories = Category.values().toList()

    private var pendingTaskDescription: String? = null

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
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
        ) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                styleText(view, position)
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
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
        dropDown.setSelection(categories.indexOf(selectedCategory))
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

        createNotificationChannel()
        saveBtn.setOnClickListener {

            val taskDescription = taskInput.text.toString()
            if (taskDescription.isEmpty()) {
                Toast.makeText(this, R.string.task_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                saveNoteAndNotify(taskDescription)
            } else {
                // Save the task description temporarily and request permission
                pendingTaskDescription = taskDescription
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }

        // sends the data to recycler view
        taskAdapter = TaskAdapter(viewModel.taskList) { clickedTask ->
            val taskDetails = Intent(
                this,
                TaskDetail::class.java
            )
            taskDetails.putExtra("task_description", clickedTask.description)
            taskDetails.putExtra("task_category", clickedTask.category.displayName)
            startActivity(taskDetails)
        }
        recyclerView.adapter = taskAdapter


        val shareBtn = findViewById<Button>(R.id.shareAllBtn)


        // share button -> share all listed task
        shareBtn.setOnClickListener {

            if (viewModel.taskList.isEmpty()) {
                Toast.makeText(this, "No tasks to share", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val shareText = StringBuilder()

            // message formmat
            for (task in viewModel.taskList) {
                shareText.append("-------- My Task --------\n")
                shareText.append("Category: ${task.category}\n")
                shareText.append("Task: ${task.description}\n\n\n")
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText.toString())

            startActivity(Intent.createChooser(intent, "Share notes via"))
        }

        // preview button -> opens last taks
        val previewButton = findViewById<Button>(R.id.previewBtn)

        previewButton.setOnClickListener {
            if (viewModel.taskList.isEmpty()) {
                Toast.makeText(this, " No notes to preview", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val lastTast = viewModel.taskList.last()

            val lastTaskDetail = Intent(this, TaskDetail::class.java)
            lastTaskDetail.putExtra("task_description", lastTast.description)
            lastTaskDetail.putExtra("task_category", lastTast.category)

            startActivity(lastTaskDetail)
        }
    }



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingTaskDescription?.let {
                    saveNoteAndNotify(it)
                }
            } else {
                Toast.makeText(
                    this,
                    "Notification permission denied. Note not saved.",
                    Toast.LENGTH_LONG
                ).show()
            }
            pendingTaskDescription = null // reset
        }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "note_channel",
            "Note Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // handles post notification
    private fun saveNoteAndNotify(taskDescription: String) {
        val task = Task(taskDescription, selectedCategory)
//        taskList.add(task)
        viewModel.taskList.add(task)
        taskAdapter.notifyItemInserted(viewModel.taskList.size - 1)

        val taskInput = findViewById<EditText>(R.id.noteInput)
        taskInput.text.clear()

        val notification = NotificationCompat.Builder(this, "note_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quick Note")
            .setContentText("Note Saved")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this).notify(1001, notification)
            }
        } else {
            NotificationManagerCompat.from(this).notify(1001, notification)
        }
    }

    // prints the logs of lifecycle
    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle State", "--------- onStart() ---------")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle State", "--------- onResume() ---------")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle State", "--------- onPause() ---------")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle State", "--------- onStop() ---------")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle State", "--------- onRestart() ---------")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle State", "--------- onDestroy() ---------")
    }

}
