package com.example.crudroom.data

import kotlinx.coroutines.flow.Flow

class TareaRepository(private val dao: TareaDao) {

    val tareas: Flow<List<Tarea>> = dao.obtenerTodas()

    suspend fun insertar(tarea: Tarea) = dao.insertar(tarea)
    suspend fun actualizar(tarea: Tarea) = dao.actualizar(tarea)
    suspend fun eliminar(tarea: Tarea) = dao.eliminar(tarea)
    suspend fun obtenerPorId(id: Int) = dao.obtenerPorId(id)
}
