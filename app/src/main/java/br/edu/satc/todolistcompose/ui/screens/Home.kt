@file:OptIn(ExperimentalMaterial3Api::class)

package br.edu.satc.todolistcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.satc.todolistcompose.data.AppDatabase
import br.edu.satc.todolistcompose.data.TaskData
import br.edu.satc.todolistcompose.ui.components.TaskCard
import br.edu.satc.todolistcompose.ui.theme.ToDoListComposeTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ToDoListComposeTheme { HomeScreen() }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val taskDao = remember { AppDatabase.getDatabase(context).taskDao() }
    val tasks = remember { mutableStateListOf<TaskData>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        tasks.clear()
        tasks.addAll(taskDao.getAll())
    }

    // Usando Scaffold para gerenciar o background e o FAB automaticamente
    androidx.compose.material3.Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            NewTask(
                onSaveTask = { title, description ->
                    scope.launch {
                        val id = taskDao.insert(
                            TaskData(title = title, description = description, complete = false)
                        ).toInt()

                        tasks.add(
                            TaskData(id = id, title = title, description = description, complete = false)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            Content(
                tasks = tasks,
                onTaskCheckedChange = { updatedTask ->
                    scope.launch {
                        taskDao.update(updatedTask)
                        val index = tasks.indexOfFirst { it.id == updatedTask.id }
                        if (index != -1) {
                            tasks[index] = updatedTask
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Content(
    tasks: List<TaskData>,
    onTaskCheckedChange: (TaskData) -> Unit
) {
    LazyColumn {
        items(
            items = tasks,
            key = { task -> task.id }
        ) { task ->
            TaskCard(
                taskData = task,
                onTaskCheckedChange = { isChecked ->
                    onTaskCheckedChange(task.copy(complete = isChecked))
                }
            )
        }
    }
}

@Composable
fun NewTask(onSaveTask: (title: String, description: String) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            text = { Text("Nova tarefa") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "") },
            onClick = {
                showBottomSheet = true
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text(text = "Título da tarefa") }
                )
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text(text = "Descrição da tarefa") }
                )
                Button(
                    modifier = Modifier.padding(top = 4.dp),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }

                        onSaveTask(taskTitle, taskDescription)

                        taskTitle = ""
                        taskDescription = ""
                    }
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}