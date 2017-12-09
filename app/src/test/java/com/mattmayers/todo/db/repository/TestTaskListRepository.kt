package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.TaskList
import org.junit.Ignore

@Ignore
class TestTaskListRepository(items: MutableList<TaskList>) : BaseTestRepository<TaskList>(items)