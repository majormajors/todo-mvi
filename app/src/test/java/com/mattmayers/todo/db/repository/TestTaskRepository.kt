package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.Task

class TestTaskRepository(items: MutableList<Task>): BaseTestRepository<Task>(items)