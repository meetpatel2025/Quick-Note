package com.training.quicknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.training.quicknote.R
import com.training.quicknote.datamodel.Task
import com.training.quicknote.util.Category

class TaskAdapter(val taskList: List<Task>,
                  val onItemClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val taskView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_list_card, parent, false)

        return TaskViewHolder(taskView)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val currentTask = taskList[position]
//        holder.category.text = "${currentTask.category}:"

        var categoryString: String = holder.category.text.toString()

        holder.taskDescription.setText(taskList[position].description)
        holder.category.text = "${taskList[position].category}:"

//        if (holder.category != null && currentTask.category.equals(Category.PERSONAL)) {
//            holder.catColor.setBackgroundColor(
//                holder.itemView.context.getColor(R.color.personalCatClr)
//            )
//        } else if (holder.category != null && currentTask.category.equals(Category.WORK)) {
//            holder.catColor.setBackgroundColor(
//                holder.itemView.context.getColor(R.color.workCatClr)
//            )
//        } else if (holder.category != null && currentTask.category.equals(Category.STUDY)) {
//            holder.catColor.setBackgroundColor(
//                holder.itemView.context.getColor(R.color.studyCatClr)
//            )
//        }

        val categoryColor = when (currentTask.category) {
            Category.PERSONAL -> R.color.personalCatClr
            Category.WORK -> R.color.workCatClr
            Category.STUDY -> R.color.studyCatClr
            else -> {
                R.color.personalCatClr
            }
        }

        val color = androidx.core.content.ContextCompat.getColor(holder.itemView.context, categoryColor)
        holder.catColor.setBackgroundColor(color)
//        holder.category.setTextColor(color)

        holder.itemView.setOnClickListener {
            onItemClick(currentTask)
        }
    }


    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskDescription = view.findViewById<TextView>(R.id.taskDesc)
        var category = view.findViewById<TextView>(R.id.categoryTxt)
        var catColor = view.findViewById<View>(R.id.catColorBox)

    }
}