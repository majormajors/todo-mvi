package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.Task
import org.junit.Ignore

@Ignore
class TestTaskRepository(items: MutableList<Task>): BaseTestRepository<Task>(items)