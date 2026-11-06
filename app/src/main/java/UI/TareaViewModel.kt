package com.example.crudroom.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.crudroom.data.AppDatabase
import com.example.crudroom.data.Tarea
import com.example.crudroom.data.TareaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(app: Application) : AndroidViewModel(app) {

    private val repository =
        TareaRepository(AppDatabase.getInstance(app).tareaDao())

    val tareas: StateFlow<List<Tarea>> = repository.tareas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun agregar(titulo: String, descripcion: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.insertar(Tarea(titulo = titulo.trim(), descripcion = descripcion.trim()))
        }
    }

    fun editar(tarea: Tarea, titulo: String, descripcion: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.actualizar(tarea.copy(titulo = titulo.trim(), descripcion = descripcion.trim()))
        }
    }

    fun alternarCompletada(tarea: Tarea) {
        viewModelScope.launch {
            repository.actualizar(tarea.copy(completada = !tarea.completada))
        }
    }

    fun eliminar(tarea: Tarea) {
        viewModelScope.launch { repository.eliminar(tarea) }
    }
}