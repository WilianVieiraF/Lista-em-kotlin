package br.edu.satc.todolistcompose.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.satc.todolistcompose.data.AppDatabase
import br.edu.satc.todolistcompose.data.TaskData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = AppDatabase.Companion.getDatabase(application).taskDao()

    // Flow que observa as mudanças no banco de dados automaticamente
    val tasks: StateFlow<List<TaskData>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            taskDao.insertTask(TaskData(title = title, description = description, done = false))
        }
    }

    fun toggleTaskDone(task: TaskData) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(done = !task.done))
        }
    }
}