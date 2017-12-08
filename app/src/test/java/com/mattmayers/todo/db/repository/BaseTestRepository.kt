package com.mattmayers.todo.db.repository

import com.mattmayers.todo.db.model.DbModel
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.crud.DataResult
import io.reactivex.Single

open class BaseTestRepository<T : DbModel>(private var items: MutableList<T>) : Repository<T> {
    override fun getCount(): Single<Int> {
        return Single.just(items.count())
    }

    override fun getEntityList(id: Long): Single<DataResult<List<T>>> {
        return Single.fromCallable {
            DataResult.retrieved(items)
        }
    }

    override fun getEntity(id: Long): Single<DataResult<T>> {
        return Single.fromCallable {
            DataResult.retrieved(items.find { it.id == id }!!)
        }
    }

    override fun createEntity(entity: T): Single<DataResult<T>> {
        items.add(entity)
        return Single.just(DataResult.created(entity))
    }

    override fun updateEntity(entity: T): Single<DataResult<T>> {
        val index = items.indexOfFirst { it.id == entity.id }
        items[index] = entity
        return Single.just(DataResult.updated(entity))
    }

    override fun deleteEntity(entity: T): Single<DataResult<T>> {
        items.removeIf { it.id == entity.id }
        return Single.just(DataResult.deleted(entity))
    }
}