package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskDao
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import io.reactivex.Single

class TaskRepository(private val dao: TaskDao) : Repository<Task> {
    override fun getCount(): Single<Int> = notImplemented()

    override fun getEntityList(id: Long): Single<DataResult<List<Task>>> {
        return dao.findAllInList(id).map { DataResult.retrieved(it) }
    }

    override fun getEntity(id: Long): Single<DataResult<Task>> {
        return dao.findById(id).map { DataResult.retrieved(it) }
    }

    override fun createEntity(entity: Task): Single<DataResult<Task>> {
        return Single.fromCallable { dao.create(entity) }
                .flatMap { id -> createdResult(id, { entity.copy(id = id) }) }
    }

    override fun updateEntity(entity: Task): Single<DataResult<Task>> {
        return Single.fromCallable { dao.update(entity) }
                .flatMap { rows -> updatedResult(rows, entity) }
    }

    override fun deleteEntity(entity: Task): Single<DataResult<Task>> {
        return Single.fromCallable { dao.delete(entity) }
                .flatMap { rows -> deletedResult(rows, entity) }
    }
}