package com.hendra.alpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope

import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.EventRepository
import com.hendra.alpvp.ui.model.EventRequest
import com.hendra.alpvp.ui.model.EventResponse
import com.hendra.alpvp.ui.model.WebResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _eventsState = MutableStateFlow<Any?>(null)
    val eventsState: StateFlow<Any?> = _eventsState

    private val _actionState = MutableStateFlow<Any?>(null)
    val actionState: StateFlow<Any?> = _actionState

    fun getAllEvents() {
        viewModelScope.launch {
            _eventsState.value = repository.getAllEvents()
        }
    }

    fun createEvent(request: EventRequest) {
        viewModelScope.launch {
            val result = repository.createEvent(request)
            _actionState.value = result
            if (result.isSuccess) {
                getAllEvents() // Automatically refresh on success
            }
        }
    }

    fun updateEvent(eventId: Int, request: EventRequest) {
        viewModelScope.launch {
            val result = repository.updateEvent(eventId, request)
            _actionState.value = result
            if (result.isSuccess) {
                getAllEvents() // Automatically refresh on success
            }
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            val result = repository.deleteEvent(eventId)
            _actionState.value = result
            if (result.isSuccess) {
                getAllEvents() // Automatically refresh on success
            }
        }
    }

    fun getEventsForDate(date: LocalDate): List<EventResponse> {
        val state = _eventsState.value
        if (state is Result<*>) {
            return try {
                @Suppress("UNCHECKED_CAST")
                val webResponse = state.getOrNull() as? WebResponse<List<EventResponse>>
                val allEvents = webResponse?.data ?: emptyList()
                allEvents.filter {
                    try {
                        val eventDate = LocalDate.parse(it.date.take(10))
                        eventDate.isEqual(date)
                    } catch (e: Exception) {
                        false
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
        return emptyList()
    }

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
