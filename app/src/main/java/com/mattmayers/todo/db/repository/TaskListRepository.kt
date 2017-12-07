package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListDao
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import io.reactivex.Single

class TaskListRepository(private val dao: TaskListDao) : Repository<TaskList> {
    override fun getEntityList(id: Long): Single<DataResult<List<TaskList>>> {
        return dao.findAllInGroup(id).map { DataResult.retrieved(it) }
    }

    override fun getEntity(id: Long): Single<DataResult<TaskList>> {
        return dao.findById(id).map { DataResult.retrieved(it) }
    }

    override fun createEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.fromCallable { dao.create(entity) }
                .flatMap { id -> createdResult(id, { entity.copy(id = id) }) }
    }

    override fun updateEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.fromCallable { dao.update(entity) }
                .flatMap { rows -> updatedResult(rows, entity) }
    }

    override fun deleteEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.fromCallable { dao.delete(entity) }
                .flatMap { rows -> deletedResult(rows, entity) }
    }

    override fun getCount(): Single<Int> = notImplemented()
}