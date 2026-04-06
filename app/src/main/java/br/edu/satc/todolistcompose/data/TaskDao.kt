package br.edu.satc.todolistcompose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    suspend fun getAll(): List<TaskData>

    @Insert
    suspend fun insert(task: TaskData): Long

    @Update
    suspend fun update(task: TaskData)
}