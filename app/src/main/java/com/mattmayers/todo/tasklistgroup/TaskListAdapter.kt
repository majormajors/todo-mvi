package com.mattmayers.todo.tasklistgroup

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mattmayers.todo.R
import com.mattmayers.todo.db.model.TaskList
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class TaskListAdapter(
        var taskLists: List<TaskList> = listOf()
): RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {
    private val itemClicks: Subject<TaskList> = PublishSubject.create()

    fun itemClicks() = itemClicks.hide()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskListViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val layout = inflater.inflate(R.layout.task_list_group_item, parent, false)
        return TaskListViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder?, position: Int) {
        holder?.bind(taskLists[position])
        holder?.itemView?.setOnClickListener {
            itemClicks.onNext(taskLists[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = taskLists.count()

    class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.name)

        fun bind(taskList: TaskList) {
            nameView.text = taskList.name

        }
    }
}