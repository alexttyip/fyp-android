package com.ytt.vmv.models

import androidx.lifecycle.*
import com.ytt.vmv.database.Election
import com.ytt.vmv.database.ElectionOption
import com.ytt.vmv.database.ElectionRepository
import kotlinx.coroutines.launch

class ElectionViewModel(private val repository: ElectionRepository) : ViewModel() {

    val allElections: LiveData<List<Election>> = repository.allElections.asLiveData()

    fun getByName(name: String) = repository.getByName(name).asLiveData()

    fun insert(election: Election) = viewModelScope.launch {
        repository.insert(election)
    }

    fun insert(electionOption: ElectionOption) = viewModelScope.launch {
        repository.insert(electionOption)
    }

    fun update(election: Election) = viewModelScope.launch {
        repository.update(election)
    }

    fun updateFromRemote() = viewModelScope.launch {
        repository.updateFromRemote()
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
