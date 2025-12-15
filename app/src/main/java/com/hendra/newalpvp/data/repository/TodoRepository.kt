package com.hendra.newalpvp.data.repository

import com.hendra.newalpvp.data.service.ApiService
import com.hendra.newalpvp.data.util.safeApiCall
import com.hendra.newalpvp.ui.model.TodoRequest

class TodoRepository(private val api: ApiService) {
    suspend fun getTodos() = safeApiCall { api.getTodos() }
    suspend fun createTodo(req: TodoRequest) = safeApiCall { api.createTodo(req) }
    suspend fun toggleTodo(id: String) = safeApiCall { api.toggleTodo(id) }
    suspend fun deleteTodo(id: String) = safeApiCall { api.deleteTodo(id) }
}