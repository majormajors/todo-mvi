package com.mattmayers.todo.tasklist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mattmayers.todo.R
import com.mattmayers.todo.db.model.Task

class TaskAdapter(
        var tasks: List<Task> = listOf()
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    override fun getItemCount(): Int = tasks.count()

    override fun onBindViewHolder(holder: TaskViewHolder?, position: Int) {
        holder?.bind(tasks[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val layout = inflater.inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(layout)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val body = itemView.findViewById<TextView>(R.id.body)

        fun bind(task: Task) {
            checkbox.isChecked = task.completed
            body.text = task.body
        }
    }
}