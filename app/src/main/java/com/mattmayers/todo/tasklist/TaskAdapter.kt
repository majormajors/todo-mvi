package com.mattmayers.todo.tasklist

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mattmayers.todo.R
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.kext.isTodayOrEarlier
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
        var tasks: List<Task> = listOf(),
        var showCompleted: Boolean = true
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val itemCheckChanges: Subject<Pair<Task, Boolean>> = PublishSubject.create()
    private val itemClicks: Subject<Task> = PublishSubject.create()

    fun itemCheckChanges(): Observable<Pair<Task, Boolean>> = itemCheckChanges.hide()
    fun itemClicks(): Observable<Task> = itemClicks.hide()

    override fun getItemCount(): Int {
        return if (showCompleted) {
            tasks.count()
        } else {
            tasks.count { !it.completed }
        }
    }

    private fun filteredTasks(): List<Task> {
        return if (showCompleted) {
            tasks
        } else {
            tasks.filter { !it.completed }
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder?, position: Int) {
        holder?.bind(filteredTasks()[position], itemCheckChanges, itemClicks)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val layout = inflater.inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(layout)
    }

    override fun onViewRecycled(holder: TaskViewHolder?) {
        super.onViewRecycled(holder)
        holder?.unbind()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateFormatter = SimpleDateFormat("M/d", Locale.getDefault())

        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val body: TextView = itemView.findViewById(R.id.body)
        private val dueDate: TextView = itemView.findViewById(R.id.dueDate)

        fun bind(
                task: Task,
                itemCheckChangesObserver: Observer<Pair<Task, Boolean>>,
                itemClicksObserver: Observer<Task>
        ) {
            val context = itemView.context
            // data binding
            checkbox.isChecked = task.completed
            body.text = task.body
            body.paintFlags = if (task.completed) {
                body.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                body.paintFlags.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }

            task.dueDate?.let {
                dueDate.text = dateFormatter.format(it)
                dueDate.setTextColor(context.resources.getColor(
                        if (it.isTodayOrEarlier()) {
                            R.color.overdue_task_color
                        } else {
                            R.color.lightGray
                        }))
            }

            // set new listeners
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                itemCheckChangesObserver.onNext(Pair(task, isChecked))
            }
            itemView.setOnClickListener {
                itemClicksObserver.onNext(task)
            }
        }

        fun unbind() {
            checkbox.setOnCheckedChangeListener(null)
            itemView.setOnClickListener(null)
        }
    }
}