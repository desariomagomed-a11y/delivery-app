package com.todoapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.todoapp.data.models.Priority
import com.todoapp.data.models.TodoItem
import com.todoapp.ui.theme.*
import com.todoapp.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel = hiltViewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var expandedId by remember { mutableStateOf<Int?>(null) }

    val todos = viewModel.todos.collectAsState(initial = emptyList()).value
    val stats = viewModel.todoStats.collectAsState().value
    val filter = viewModel.filter.collectAsState().value

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(title = { Text("My Tasks") })

        StatsCard(stats)

        FilterRow(filter) { viewModel.setFilter(it) }

        if (todos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tasks yet", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(todos) { todo ->
                    TodoCard(
                        todo, expandedId == todo.id,
                        onToggle = { expandedId = if (expandedId == todo.id) null else todo.id },
                        onComplete = { viewModel.toggleTodo(todo.id, todo.isCompleted) },
                        onDelete = { viewModel.deleteTodo(todo) }
                    )
                }
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = { showDialog = true },
            Modifier.padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, "Add")
        }
    }

    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },
            onAdd = { title, desc, priority ->
                viewModel.addTodo(title, desc, priority)
                showDialog = false
            }
        )
    }
}

@Composable
fun StatsCard(stats: com.todoapp.data.models.TodoStats) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            StatItem("Total", stats.totalTodos.toString())
            Divider(Modifier.height(40.dp).width(1.dp))
            StatItem("Completed", stats.completedTodos.toString())
            Divider(Modifier.height(40.dp).width(1.dp))
            StatItem("Pending", stats.pendingTodos.toString())
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun FilterRow(selected: String, onChange: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        FilterChip(selected == "all", onClick = { onChange("all") }, label = { Text("All") })
        FilterChip(selected == "active", onClick = { onChange("active") }, label = { Text("Active") })
        FilterChip(selected == "completed", onClick = { onChange("completed") }, label = { Text("Done") })
    }
}

@Composable
fun TodoCard(
    todo: TodoItem,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onToggle() }
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.weight(1f).padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(todo.isCompleted, { onComplete() })
                    Column(Modifier.weight(1f)) {
                        Text(
                            todo.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        if (todo.description.isNotEmpty() && !isExpanded) {
                            Text(
                                todo.description.take(50) + if (todo.description.length > 50) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1
                            )
                        }
                    }
                }
                Box(
                    Modifier
                        .size(40.dp)
                        .background(getPriorityColor(todo.priority), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(todo.priority.name.first().toString(), color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (isExpanded) {
                Divider(Modifier.padding(vertical = 8.dp))
                if (todo.description.isNotEmpty()) {
                    Text(todo.description, style = MaterialTheme.typography.bodyMedium, Modifier.padding(bottom = 8.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTodoDialog(onDismiss: () -> Unit, onAdd: (String, String, Priority) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, label = { Text("Title") }, Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(description, { description = it }, label = { Text("Description") }, Modifier.fillMaxWidth().height(100.dp).padding(bottom = 8.dp), maxLines = 4)
                Text("Priority", style = MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Priority.values().forEach { p ->
                        Button(
                            onClick = { priority = p },
                            Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (priority == p) getPriorityColor(p) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(p.name.take(1), fontSize = MaterialTheme.typography.labelSmall.fontSize)
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (title.isNotEmpty()) onAdd(title, description, priority) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

fun getPriorityColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> colorLowPriority
    Priority.MEDIUM -> colorMediumPriority
    Priority.HIGH -> colorHighPriority
    Priority.URGENT -> colorUrgentPriority
}