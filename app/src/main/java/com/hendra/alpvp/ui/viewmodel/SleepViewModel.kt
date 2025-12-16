package com.hendra.alpvp.ui.viewmodel;

import android.util.Log;

class SleepViewModel(private val repository: SleepRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmResponse>>(emptyList())
    val alarm = _alarms.asStateFlow()
    fun loadData(){
        viewModelScope.launch {
            repository.getAlarms().onSuccess { response ->
                _alarms.value = response.data
            }.onFailure { error ->
                    Log.e("SleepViewModel", "GAGAL Load Data: ")
            }
        }
    }
}

