package com.hendra.alpvp.data.repository

import com.hendra.alpvp.data.service.ApiService
import com.hendra.alpvp.data.util.safeApiCall
import com.hendra.alpvp.ui.model.TodoRequest

class TodoRepository(private val api: ApiService) {
    suspend fun getTodos() = safeApiCall { api.getTodos() }
    suspend fun createTodo(req: TodoRequest) = safeApiCall { api.createTodo(req) }
    suspend fun toggleTodo(id: String) = safeApiCall { api.toggleTodo(id) }
    suspend fun deleteTodo(id: String) = safeApiCall { api.deleteTodo(id) }
}