package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.db.model.TaskListGroupDao
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import io.reactivex.Single

class TaskListGroupRepository(private val dao: TaskListGroupDao): Repository<TaskListGroup> {
    override fun getCount(): Single<Int> = dao.countAll()
    override fun getEntity(id: Long): Single<DataResult<TaskListGroup>> =
            dao.findById(id).map { DataResult.retrieved(it) }
    override fun createEntity(entity: TaskListGroup): Single<DataResult<TaskListGroup>> = Single.create {
        val id = dao.create(entity)
        it.onSuccess(DataResult.created(entity.copy(id = id)))
    }
    override fun updateEntity(entity: TaskListGroup): Single<DataResult<TaskListGroup>> = notImplemented()
    override fun deleteEntity(entity: TaskListGroup): Single<DataResult<TaskListGroup>> = notImplemented()
    override fun getEntityList(id: Long): Single<DataResult<List<TaskListGroup>>> = notImplemented()
}