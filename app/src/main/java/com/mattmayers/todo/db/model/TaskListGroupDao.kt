package com.mattmayers.todo.db.model

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface TaskListGroupDao {
    @Query("SELECT id FROM ${TaskListGroup.TABLE_NAME} ORDER BY id ASC LIMIT 1")
    fun findFirstId(): Single<Long>

    @Query("SELECT * FROM ${TaskListGroup.TABLE_NAME} WHERE id = :id LIMIT 1")
    fun findById(id: Long): Single<TaskListGroup>

    @Query("SELECT count(*) FROM ${TaskListGroup.TABLE_NAME}")
    fun countAll(): Single<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(group: TaskListGroup): Long

    @Update
    fun update(group: TaskListGroup): Int

    @Delete
    fun delete(group: TaskListGroup): Int
}