package br.edu.satc.todolistcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import br.edu.satc.todolistcompose.ui.screens.HomeScreen
import br.edu.satc.todolistcompose.ui.theme.ToDoListComposeTheme
import br.edu.satc.todolistcompose.ui.TaskViewModel

class MainActivity : ComponentActivity() {
    // Instancia o ViewModel
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListComposeTheme {
                // Passa o viewModel ou os dados para a HomeScreen
                HomeScreen(viewModel)
            }
        }
    }
}