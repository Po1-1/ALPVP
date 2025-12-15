package com.hendra.alpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.EventRepository
import com.hendra.alpvp.ui.model.EventRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    // ===== STATE =====
    private val _eventsState = MutableStateFlow<Any?>(null)
    val eventsState: StateFlow<Any?> = _eventsState

    private val _eventDetailState = MutableStateFlow<Any?>(null)
    val eventDetailState: StateFlow<Any?> = _eventDetailState

    private val _actionState = MutableStateFlow<Any?>(null)
    val actionState: StateFlow<Any?> = _actionState

    // ===== GET ALL EVENTS =====
    fun getAllEvents() {
        viewModelScope.launch {
            _eventsState.value = repository.getAllEvents()
        }
    }

    // ===== GET EVENT BY ID =====
    fun getEvent(id: Int) {
        viewModelScope.launch {
            _eventDetailState.value = repository.getEvent(id)
        }
    }

    // ===== CREATE EVENT =====
    fun createEvent(request: EventRequest) {
        viewModelScope.launch {
            _actionState.value = repository.createEvent(request)
        }
    }

    // ===== UPDATE EVENT =====
    fun updateEvent(eventId: Int, request: EventRequest) {
        viewModelScope.launch {
            _actionState.value = repository.updateEvent(eventId, request)
        }
    }

    // ===== DELETE EVENT =====
    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            _actionState.value = repository.deleteEvent(eventId)
        }
    }

    // ===== RESET STATE (optional) =====
    fun clearActionState() {
        _actionState.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                val repository = application.container.eventRepository
                EventViewModel(repository)
            }
        }
    }
}
