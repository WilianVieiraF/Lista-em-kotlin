package br.edu.satc.todolistcompose.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskData)

    @Update
    suspend fun updateTask(task: TaskData)

    @Delete
    suspend fun deleteTask(task: TaskData)
}