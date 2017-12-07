package com.mattmayers.todo.tasklist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mattmayers.todo.R
import com.mattmayers.todo.db.model.Task
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class TaskAdapter(
        var tasks: List<Task> = listOf()
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val itemCheckChanges: Subject<Pair<Task, Boolean>> = PublishSubject.create()
    private val itemClicks: Subject<Task> = PublishSubject.create()

    fun itemCheckChanges(): Observable<Pair<Task, Boolean>> = itemCheckChanges.hide()
    fun itemClicks(): Observable<Task> = itemClicks.hide()

    override fun getItemCount(): Int = tasks.count()

    override fun onBindViewHolder(holder: TaskViewHolder?, position: Int) {
        holder?.bind(tasks[position])
        holder?.checkbox?.setOnCheckedChangeListener { _, isChecked ->
            itemCheckChanges.onNext(Pair(tasks[holder.adapterPosition], isChecked))
        }
        holder?.itemView?.setOnClickListener {
            itemClicks.onNext(tasks[holder.adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val layout = inflater.inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(layout)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val body = itemView.findViewById<TextView>(R.id.body)

        fun bind(task: Task) {
            checkbox.isChecked = task.completed
            body.text = task.body
        }
    }
}