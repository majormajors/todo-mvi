package com.mattmayers.todo.framework

import com.mattmayers.todo.framework.crud.DataResult
import io.reactivex.Single

interface Repository<T> {
    fun getCount(): Single<Int>
    fun getEntityList(id: Long): Single<DataResult<List<T>>>
    fun getEntity(id: Long): Single<DataResult<T>>
    fun createEntity(entity: T): Single<DataResult<T>>
    fun updateEntity(entity: T): Single<DataResult<T>>
    fun deleteEntity(entity: T): Single<DataResult<T>>

    fun <R> notImplemented(): Single<R> = Single.error(NotImplementedError())
}