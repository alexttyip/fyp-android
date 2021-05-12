package com.ytt.vmv.models

import androidx.lifecycle.*
import com.ytt.vmv.database.entities.Election
import com.ytt.vmv.database.repos.ElectionRepository
import kotlinx.coroutines.launch

class ElectionViewModel(private val repository: ElectionRepository) : ViewModel() {

    val allElections: LiveData<List<Election>> = repository.allElections.asLiveData()

    fun insert(election: Election) = viewModelScope.launch {
        repository.insert(election)
    }
}

class ElectionViewModelFactory(private val repository: ElectionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ElectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}