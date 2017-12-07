package com.mattmayers.todo.framework

import com.mattmayers.todo.framework.crud.CreateFailedError
import com.mattmayers.todo.framework.crud.DataResult
import com.mattmayers.todo.framework.crud.DeleteFailedError
import com.mattmayers.todo.framework.crud.UpdateFailedError
import io.reactivex.Single

interface Repository<T> {
    fun getCount(): Single<Int>
    fun getEntityList(id: Long): Single<DataResult<List<T>>>
    fun getEntity(id: Long): Single<DataResult<T>>
    fun createEntity(entity: T): Single<DataResult<T>>
    fun updateEntity(entity: T): Single<DataResult<T>>
    fun deleteEntity(entity: T): Single<DataResult<T>>

    fun <R> notImplemented(): Single<R> = Single.error(NotImplementedError())

    fun createdResult(id: Long, provider: () -> T): Single<DataResult<T>> {
        return when (id) {
            0L -> Single.error(CreateFailedError())
            else -> Single.just(DataResult.created(provider()))
        }
    }

    fun updatedResult(rows: Int, entity: T): Single<DataResult<T>> {
        return when (rows) {
            0 -> Single.error(UpdateFailedError())
            else -> Single.just(DataResult.updated(entity))
        }
    }

    fun deletedResult(rows: Int, entity: T): Single<DataResult<T>> {
        return when (rows) {
            0 -> Single.error(DeleteFailedError())
            else -> Single.just(DataResult.deleted(entity))
        }
    }
}