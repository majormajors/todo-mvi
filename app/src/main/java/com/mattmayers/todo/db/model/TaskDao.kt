package com.mattmayers.todo.db.model

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface TaskDao {
    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE task_list_id = :listId")
    fun findAllInList(listId: Long): Single<List<Task>>

    @Query("SELECT * FROM ${Task.TABLE_NAME} WHERE id = :id LIMIT 1")
    fun findById(id: Long): Single<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(task: Task): Long

    @Update
    fun update(task: Task): Int

    @Delete
    fun delete(task: Task): Int
}