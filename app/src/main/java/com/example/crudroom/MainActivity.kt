package com.example.crudroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crudroom.data.Tarea
import com.example.crudroom.ui.TareaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaTareas()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareaViewModel = viewModel()) {
    val tareas by vm.tareas.collectAsStateWithLifecycle()

    // null = diálogo cerrado; Tarea(id = 0) = nueva; otra = edición
    var tareaEnEdicion by remember { mutableStateOf<Tarea?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis tareas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { tareaEnEdicion = Tarea(titulo = "") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        if (tareas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay tareas. Toca + para crear una.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareas, key = { it.id }) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onToggle = { vm.alternarCompletada(tarea) },
                        onEditar = { tareaEnEdicion = tarea },
                        onEliminar = { vm.eliminar(tarea) }
                    )
                }
            }
        }
    }

    tareaEnEdicion?.let { actual ->
        DialogoTarea(
            tarea = actual,
            onCancelar = { tareaEnEdicion = null },
            onGuardar = { titulo, descripcion ->
                if (actual.id == 0) vm.agregar(titulo, descripcion)
                else vm.editar(actual, titulo, descripcion)
                tareaEnEdicion = null
            }
        )
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onToggle: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, "Editar") }
            IconButton(onClick = onEliminar) { Icon(Icons.Default.Delete, "Eliminar") }
        }
    }
}

@Composable
fun DialogoTarea(
    tarea: Tarea,
    onCancelar: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by remember { mutableStateOf(tarea.titulo) }
    var descripcion by remember { mutableStateOf(tarea.descripcion) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (tarea.id == 0) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onGuardar(titulo, descripcion) }, enabled = titulo.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}