package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.TaskList

class TestTaskListRepository(items: MutableList<TaskList>) : BaseTestRepository<TaskList>(items)