package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListDao
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import com.mattmayers.todo.framework.crud.DeleteFailedError
import com.mattmayers.todo.framework.crud.UpdateFailedError
import io.reactivex.Single

class TaskListRepository(private val dao: TaskListDao) : Repository<TaskList> {
    override fun getEntityList(id: Long): Single<DataResult<List<TaskList>>> {
        return dao.findAllInGroup(id).map { DataResult.retrieved(it) }
    }

    override fun getEntity(id: Long): Single<DataResult<TaskList>> {
        return dao.findById(id).map { DataResult.retrieved(it) }
    }

    override fun createEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.create {
            val id = dao.create(entity)
            it.onSuccess(DataResult.created(entity.copy(id = id)))
        }
    }

    override fun updateEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.create<DataResult<TaskList>> {
            if (dao.update(entity) > 0) {
                it.onSuccess(DataResult.updated(entity))
            } else {
                it.onError(UpdateFailedError())
            }
        }
    }

    override fun deleteEntity(entity: TaskList): Single<DataResult<TaskList>> {
        return Single.create<DataResult<TaskList>> {
            if (dao.delete(entity) > 0) {
                it.onSuccess(DataResult.deleted(entity))
            } else {
                it.onError(DeleteFailedError())
            }
        }
    }

    override fun getCount(): Single<Int> = notImplemented()
}