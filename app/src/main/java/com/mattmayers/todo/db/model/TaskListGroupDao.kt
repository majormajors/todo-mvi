package com.mattmayers.todo.db.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface TaskListGroupDao {
    @Query("SELECT * FROM ${TaskListGroup.TABLE_NAME} WHERE id = :id LIMIT 1")
    fun findById(id: Long): Single<TaskListGroup>

    @Query("SELECT count(*) FROM ${TaskListGroup.TABLE_NAME}")
    fun countAll(): Single<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(group: TaskListGroup): Long
}