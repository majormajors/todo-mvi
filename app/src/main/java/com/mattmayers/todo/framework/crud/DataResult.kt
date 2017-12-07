package com.mattmayers.todo.framework.crud

data class DataResult<out T>(val entity: T, val resultType: ResultType) {
    companion object {
        fun <T> created(entity: T) = DataResult(entity, ResultType.CREATED)
        fun <T> retrieved(entity: T) = DataResult(entity, ResultType.RETRIEVED)
        fun <T> updated(entity: T) = DataResult(entity, ResultType.UPDATED)
        fun <T> deleted(entity: T) = DataResult(entity, ResultType.DELETED)
    }
}