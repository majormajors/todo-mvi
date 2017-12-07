package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskDao
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import com.mattmayers.todo.framework.crud.DeleteFailedError
import com.mattmayers.todo.framework.crud.UpdateFailedError
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
        return Single.create {
            val id = dao.create(entity)
            it.onSuccess(DataResult.created(entity.copy(id = id)))
        }
    }

    override fun updateEntity(entity: Task): Single<DataResult<Task>> {
        return Single.create<DataResult<Task>> {
            if (dao.update(entity) > 0) {
                it.onSuccess(DataResult.updated(entity))
            } else {
                it.onError(UpdateFailedError())
            }
        }
    }

    override fun deleteEntity(entity: Task): Single<DataResult<Task>> {
        return Single.create<DataResult<Task>> {
            if (dao.delete(entity) > 0) {
                it.onSuccess(DataResult.deleted(entity))
            } else {
                it.onError(DeleteFailedError())
            }
        }
    }
}