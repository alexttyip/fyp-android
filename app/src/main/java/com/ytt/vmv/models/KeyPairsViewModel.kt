package com.ytt.vmv.models

import androidx.lifecycle.*
import com.ytt.vmv.database.entities.KeyPairs
import com.ytt.vmv.database.repos.KeyPairsRepository
import kotlinx.coroutines.launch

class KeyPairsViewModel(private val repository: KeyPairsRepository) : ViewModel() {

    val allKeyPairs: LiveData<List<KeyPairs>> = repository.allKeyPairs.asLiveData()

    fun insert(keyPairs: KeyPairs) = viewModelScope.launch {
        repository.insert(keyPairs)
    }
}

class KeyPairsViewModelFactory(private val repository: KeyPairsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KeyPairsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KeyPairsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
