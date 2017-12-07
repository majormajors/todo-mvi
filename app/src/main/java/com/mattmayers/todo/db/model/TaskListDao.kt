package com.mattmayers.todo.db.model

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface TaskListDao {
    @Query("SELECT * FROM ${TaskList.TABLE_NAME} WHERE task_list_group_id = :groupId")
    fun findAllInGroup(groupId: Long): Single<List<TaskList>>

    @Query("SELECT * FROM ${TaskList.TABLE_NAME} WHERE id = :id LIMIT 1")
    fun findById(id: Long): Single<TaskList>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(group: TaskList): Long

    @Update
    fun update(taskList: TaskList): Int

    @Delete
    fun delete(taskList: TaskList): Int
}